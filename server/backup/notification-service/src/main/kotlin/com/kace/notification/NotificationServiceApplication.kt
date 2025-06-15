package com.kace.notification

import com.kace.common.config.configureSerialization
import com.kace.common.exception.configureExceptionHandling
import com.kace.notification.api.route.configureRouting
import com.kace.notification.infrastructure.config.configureKoin
import com.kace.notification.infrastructure.config.configureDatabases
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

/**
 * 通知服务应用入口
 */
fun main() {
    embeddedServer(Netty, port = 8084, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * 应用程序模块配置
 */
fun Application.module() {
    // 配置依赖注入
    configureKoin()
    
    // 配置数据库
    configureDatabases()
    
    // 配置序列化
    configureSerialization()
    
    // 配置异常处理
    configureExceptionHandling()
    
    // 配置日志
    install(CallLogging) {
        level = Level.INFO
    }
    
    // 配置路由
    configureRouting()
    
    // 启动日志
    log.info("通知服务启动成功，监听端口: 8084")
} 