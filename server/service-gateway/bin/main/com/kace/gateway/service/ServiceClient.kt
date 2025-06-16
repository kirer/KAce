package com.kace.gateway.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * 服务客户端
 * 用于与其他微服务通信
 */
class ServiceClient(
    val authServiceUrl: String,
    val userServiceUrl: String,
    val contentServiceUrl: String,
    val mediaServiceUrl: String,
    val analyticsServiceUrl: String,
    val notificationServiceUrl: String
) {
    // 创建HTTP客户端
    val client = HttpClient(CIO) {
        // 配置内容协商
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        // 配置日志
        install(Logging) {
            level = LogLevel.INFO
        }
    }
    
    /**
     * 获取服务URL
     */
    fun getServiceUrl(service: ServiceType): String {
        return when (service) {
            ServiceType.AUTH -> authServiceUrl
            ServiceType.USER -> userServiceUrl
            ServiceType.CONTENT -> contentServiceUrl
            ServiceType.MEDIA -> mediaServiceUrl
            ServiceType.ANALYTICS -> analyticsServiceUrl
            ServiceType.NOTIFICATION -> notificationServiceUrl
        }
    }
    
    /**
     * 服务类型
     */
    enum class ServiceType {
        AUTH,
        USER,
        CONTENT,
        MEDIA,
        ANALYTICS,
        NOTIFICATION
    }
} 