package com.github.kirer.kace.exception

import com.github.kirer.kace.log.LogService
import com.github.kirer.kace.log.LogType
import com.github.kirer.kace.log.LoggerFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.slf4j.event.Level
import java.util.*

/**
 * 错误响应DTO
 */
@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val error: ErrorDetails,
    val requestId: String = UUID.randomUUID().toString()
)

/**
 * 错误详情DTO
 */
@Serializable
data class ErrorDetails(
    val code: String,
    val message: String,
    val details: Map<String, String> = emptyMap()
)

/**
 * 异常处理器
 * 负责捕获异常、记录日志并返回标准化错误响应
 */
class ExceptionHandler(private val logService: LogService) {
    
    private val systemLogger = LoggerFactory.getSystemLogger(ExceptionHandler::class.java)
    private val securityLogger = LoggerFactory.getSecurityLogger(ExceptionHandler::class.java)
    
    /**
     * 配置Ktor状态页面插件，处理异常
     */
    fun configureStatusPages(statusPages: StatusPagesConfig) {
        // KAce框架异常处理
        statusPages.exception<KAceException> { call, cause ->
            logException(cause)
            
            val errorDetails = ErrorDetails(
                code = cause.errorCode,
                message = cause.message ?: "Unknown error",
                details = cause.details.mapValues { it.value.toString() }
            )
            
            val errorResponse = ErrorResponse(
                error = errorDetails
            )
            
            call.respond(HttpStatusCode.fromValue(cause.httpStatusCode), errorResponse)
        }
        
        // 处理其他未捕获的异常
        statusPages.exception<Throwable> { call, cause ->
            logException(cause)
            
            val errorDetails = ErrorDetails(
                code = "INTERNAL_SERVER_ERROR",
                message = "An internal server error occurred"
            )
            
            val errorResponse = ErrorResponse(
                error = errorDetails
            )
            
            call.respond(HttpStatusCode.InternalServerError, errorResponse)
        }
    }
    
    /**
     * 记录异常日志
     */
    private fun logException(exception: Throwable) {
        when (exception) {
            is SecurityException -> {
                // 安全异常记录到安全日志
                securityLogger.logSecurityEvent(
                    event = exception.javaClass.simpleName,
                    level = Level.ERROR,
                    subject = "system",
                    details = exception.message ?: "No details"
                )
            }
            is KAceException -> {
                // KAce框架异常记录到系统日志
                systemLogger.logSystemAction(
                    action = "EXCEPTION_THROWN",
                    result = false,
                    details = "${exception.javaClass.name}: ${exception.message}"
                )
            }
            else -> {
                // 其他异常记录到系统日志
                systemLogger.error("Unhandled exception", exception)
            }
        }
    }
} 