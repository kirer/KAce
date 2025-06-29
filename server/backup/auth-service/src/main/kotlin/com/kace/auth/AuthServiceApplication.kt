package com.kace.auth

import com.kace.auth.api.configureRoutes
import com.kace.auth.infrastructure.config.configureDatabases
import com.kace.auth.infrastructure.config.configureKoin
import com.kace.auth.infrastructure.config.configureMonitoring
import com.kace.auth.infrastructure.config.configureSecurity
import com.kace.auth.infrastructure.config.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kace.common.exception.GlobalExceptionHandler.configureExceptionHandling

/**
 * 应用入口
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * 应用模块配置
 */
fun Application.module() {
    // 配置依赖注入
    configureKoin()
    
    // 配置序列化
    configureSerialization()
    
    // 配置数据库
    configureDatabases()
    
    // 配置安全
    configureSecurity()
    
    // 配置监控
    configureMonitoring()
    
    // 配置路由
    configureRoutes()
    
    // 配置异常处理
    configureExceptionHandling()
    
    // 应用启动日志
    log.info("认证服务启动成功，运行在端口: 8080")
}
