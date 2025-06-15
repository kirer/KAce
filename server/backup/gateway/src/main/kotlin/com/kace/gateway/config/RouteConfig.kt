package com.kace.gateway.config

import com.kace.gateway.filter.AuthFilter
import com.kace.gateway.filter.LoggingFilter
import com.kace.gateway.filter.RateLimitFilter
import com.kace.gateway.handler.ErrorHandler
import com.kace.gateway.handler.FallbackHandler
import com.kace.gateway.service.ServiceClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.net.ConnectException

/**
 * 配置路由
 */
fun Application.configureRouting() {
    // 获取服务客户端
    val serviceClient by inject<ServiceClient>()
    
    // 获取过滤器
    val authFilter = AuthFilter()
    val loggingFilter = LoggingFilter()
    val rateLimitFilter = RateLimitFilter()
    
    // 获取处理器
    val errorHandler = ErrorHandler()
    val fallbackHandler = FallbackHandler()
    
    // 日志记录器
    val logger = LoggerFactory.getLogger("RouteConfig")
    
    // 配置路由
    routing {
        // 健康检查
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "UP"))
        }
        
        // API路由
        route("/api") {
            // 认证服务
            route("/auth") {
                proxyTo(serviceClient, ServiceClient.ServiceType.AUTH, authFilter, loggingFilter, rateLimitFilter)
            }
            
            // 用户服务
            route("/users") {
                authenticate("auth-jwt") {
                    proxyTo(serviceClient, ServiceClient.ServiceType.USER, authFilter, loggingFilter, rateLimitFilter)
                }
            }
            
            // 内容服务
            route("/content") {
                authenticate("auth-jwt") {
                    proxyTo(serviceClient, ServiceClient.ServiceType.CONTENT, authFilter, loggingFilter, rateLimitFilter)
                }
            }
            
            // 媒体服务
            route("/media") {
                authenticate("auth-jwt") {
                    proxyTo(serviceClient, ServiceClient.ServiceType.MEDIA, authFilter, loggingFilter, rateLimitFilter)
                }
            }
            
            // 分析服务
            route("/analytics") {
                authenticate("auth-jwt") {
                    proxyTo(serviceClient, ServiceClient.ServiceType.ANALYTICS, authFilter, loggingFilter, rateLimitFilter)
                }
            }
            
            // 通知服务
            route("/notifications") {
                authenticate("auth-jwt") {
                    proxyTo(serviceClient, ServiceClient.ServiceType.NOTIFICATION, authFilter, loggingFilter, rateLimitFilter)
                }
            }
        }
        
        // 404处理
        route("{...}") {
            handle {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "资源不存在"))
            }
        }
    }
}

/**
 * 代理到目标服务
 */
private fun Route.proxyTo(
    serviceClient: ServiceClient,
    serviceType: ServiceClient.ServiceType,
    authFilter: AuthFilter,
    loggingFilter: LoggingFilter,
    rateLimitFilter: RateLimitFilter
) {
    val logger = LoggerFactory.getLogger("ProxyHandler")
    val requestTimeout = 30000L // 请求超时时间，30秒
    
    // 代理所有请求
    handle {
        // 应用过滤器
        if (!authFilter.filter(call)) return@handle
        if (!loggingFilter.filter(call)) return@handle
        if (!rateLimitFilter.filter(call)) return@handle
        
        // 获取目标服务URL
        val baseUrl = serviceClient.getServiceUrl(serviceType)
        val path = call.request.path().substringAfter("/api")
        val targetUrl = "$baseUrl$path${call.request.queryString().let { if (it.isNotEmpty()) "?$it" else "" }}"
        
        logger.info("转发请求到: $targetUrl")
        
        try {
            // 读取请求体
            val requestBody = call.receiveChannel()
            
            // 转发请求到目标服务，带超时
            val response = withTimeoutOrNull(requestTimeout) {
                serviceClient.client.request(targetUrl) {
                    method = call.request.httpMethod
                    
                    // 复制请求头
                    call.request.headers.forEach { key, values ->
                        // 排除一些不应该转发的头
                        if (key !in listOf("Host", "Content-Length")) {
                            headers.appendAll(key, values)
                        }
                    }
                    
                    // 转发请求体
                    if (requestBody.availableForRead > 0) {
                        setBody(requestBody)
                    }
                }
            }
            
            if (response == null) {
                // 请求超时
                logger.warn("请求超时: $targetUrl")
                call.respond(HttpStatusCode.GatewayTimeout, mapOf("error" to "请求处理超时，请稍后重试"))
                return@handle
            }
            
            // 复制响应状态
            val status = response.status
            
            // 复制响应头
            response.headers.forEach { key, values ->
                // 排除一些不应该转发的头
                if (key !in listOf("Content-Length", "Transfer-Encoding")) {
                    values.forEach { value ->
                        call.response.headers.append(key, value)
                    }
                }
            }
            
            // 转发响应体
            call.respond(status, response.bodyAsChannel())
            
        } catch (e: ConnectException) {
            // 连接失败，可能是服务不可用
            logger.error("服务连接失败: ${e.message}")
            call.respond(HttpStatusCode.ServiceUnavailable, mapOf("error" to "服务暂时不可用，请稍后重试"))
        } catch (e: Exception) {
            // 其他异常
            logger.error("请求转发失败: ${e.message}", e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "请求处理失败，请稍后重试"))
        }
    }
} 