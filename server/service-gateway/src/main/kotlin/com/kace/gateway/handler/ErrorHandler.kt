package com.kace.gateway.handler

import com.kace.common.exception.ApiException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeoutException

/**
 * 错误处理器
 * 用于统一处理网关中的异常
 */
class ErrorHandler {
    // 日志记录器
    private val logger = LoggerFactory.getLogger(ErrorHandler::class.java)
    
    /**
     * 配置错误处理
     */
    fun configure(application: Application) {
        application.install(StatusPages) {
            // 处理API异常
            exception<ApiException> { call, cause ->
                logger.warn("API异常: ${cause.message}")
                call.respond(
                    HttpStatusCode.fromValue(cause.statusCode),
                    mapOf(
                        "error" to cause.message,
                        "code" to cause.errorCode
                    )
                )
            }
            
            // 处理超时异常
            exception<TimeoutException> { call, cause ->
                logger.error("请求超时: ${cause.message}")
                call.respond(
                    HttpStatusCode.GatewayTimeout,
                    mapOf("error" to "请求处理超时，请稍后重试")
                )
            }
            
            // 处理未授权异常
            exception<SecurityException> { call, cause ->
                logger.warn("未授权访问: ${cause.message}")
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to (cause.message ?: "未授权访问"))
                )
            }
            
            // 处理其他所有异常
            exception<Throwable> { call, cause ->
                logger.error("未处理的异常: ${cause.message}", cause)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "服务器内部错误，请稍后重试")
                )
            }
            
            // 处理404错误
            status(HttpStatusCode.NotFound) { call, status ->
                call.respond(
                    status,
                    mapOf("error" to "请求的资源不存在")
                )
            }
        }
    }
} 