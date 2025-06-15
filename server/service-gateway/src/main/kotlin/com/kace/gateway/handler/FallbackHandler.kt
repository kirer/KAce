package com.kace.gateway.handler

import com.kace.gateway.service.ServiceClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

/**
 * 降级处理器
 * 用于处理服务不可用时的降级策略
 */
class FallbackHandler {
    // 日志记录器
    private val logger = LoggerFactory.getLogger(FallbackHandler::class.java)
    
    /**
     * 处理服务降级
     * @param call ApplicationCall 应用调用
     * @param serviceType ServiceClient.ServiceType 服务类型
     * @param error Throwable 错误
     * @return Boolean 是否已处理
     */
    suspend fun handle(call: ApplicationCall, serviceType: ServiceClient.ServiceType, error: Throwable): Boolean {
        // 记录错误
        logger.error("服务 ${serviceType.name} 不可用: ${error.message}", error)
        
        // 根据服务类型返回不同的降级响应
        when (serviceType) {
            ServiceClient.ServiceType.AUTH -> {
                // 认证服务不可用，返回503错误
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    mapOf("error" to "认证服务暂时不可用，请稍后重试")
                )
            }
            ServiceClient.ServiceType.USER -> {
                // 用户服务不可用，返回降级数据
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "warning" to "用户服务暂时不可用，显示缓存数据",
                        "data" to emptyList<Any>()
                    )
                )
            }
            ServiceClient.ServiceType.CONTENT -> {
                // 内容服务不可用，返回降级数据
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "warning" to "内容服务暂时不可用，显示缓存数据",
                        "data" to emptyList<Any>()
                    )
                )
            }
            ServiceClient.ServiceType.MEDIA -> {
                // 媒体服务不可用，返回降级数据
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "warning" to "媒体服务暂时不可用，显示缓存数据",
                        "data" to emptyList<Any>()
                    )
                )
            }
            ServiceClient.ServiceType.ANALYTICS -> {
                // 分析服务不可用，返回降级数据
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "warning" to "分析服务暂时不可用，显示缓存数据",
                        "data" to emptyList<Any>()
                    )
                )
            }
            ServiceClient.ServiceType.NOTIFICATION -> {
                // 通知服务不可用，返回降级数据
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "warning" to "通知服务暂时不可用，显示缓存数据",
                        "data" to emptyList<Any>()
                    )
                )
            }
        }
        
        return true
    }
} 