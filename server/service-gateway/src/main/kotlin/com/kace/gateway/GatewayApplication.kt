package com.kace.gateway

import com.kace.gateway.config.configureKoin
import com.kace.gateway.config.configureMonitoring
import com.kace.gateway.config.configureRouting
import com.kace.gateway.config.configureSecurity
import com.kace.gateway.config.configureSerialization
import com.kace.gateway.handler.ErrorHandler
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

/**
 * API网关应用入口
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * 应用模块配置
 */
fun Application.module() {
    val logger = LoggerFactory.getLogger("GatewayApplication")
    
    try {
        // 配置依赖注入
        configureKoin()
        
        // 配置序列化
        configureSerialization()
        
        // 配置错误处理
        val errorHandler = ErrorHandler()
        errorHandler.configure(this)
        
        // 配置安全
        configureSecurity()
        
        // 配置监控
        configureMonitoring()
        
        // 配置路由
        configureRouting()
        
        // 应用启动日志
        logger.info("API网关启动成功，运行在端口: ${environment.config.property("ktor.deployment.port").getString()}")
    } catch (e: Exception) {
        logger.error("API网关启动失败: ${e.message}", e)
        throw e
    }
} 