package com.kace.common.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

/**
 * 全局异常处理器
 */
object GlobalExceptionHandler {
    
    /**
     * 配置全局异常处理
     */
    fun Application.configureExceptionHandling() {
        install(StatusPages) {
            exception<ApiException> { call, cause ->
                val response = ErrorResponse(
                    status = cause.statusCode.value,
                    message = cause.message,
                    errorCode = cause.errorCode,
                    details = cause.details
                )
                call.respond(cause.statusCode, response)
            }
            
            exception<Throwable> { call, cause ->
                val response = ErrorResponse(
                    status = HttpStatusCode.InternalServerError.value,
                    message = cause.message ?: "Internal server error",
                    errorCode = "INTERNAL_SERVER_ERROR"
                )
                call.respond(HttpStatusCode.InternalServerError, response)
            }
        }
    }
    
    /**
     * 错误响应数据类
     */
    @Serializable
    data class ErrorResponse(
        val status: Int,
        val message: String,
        val errorCode: String? = null,
        val details: Map<String, String>? = null
    )
} 