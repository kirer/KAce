package com.kace.content

import com.kace.common.exception.GlobalExceptionHandler.configureExceptionHandling
import com.kace.content.infrastructure.config.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

/**
 * 内容服务应用入口
 */
fun main() {
    embeddedServer(Netty, port = 8083, host = "0.0.0.0", module = Application::module)
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
    configureDatabase()
    
    // 配置安全
    configureSecurity()
    
    // 配置监控
    configureMonitoring()
    
    // 配置异常处理
    configureExceptionHandling()
    
    // 配置路由
    configureRouting()
    
    // 配置消息队列
    configureMessaging()
    
    // 配置搜索引擎
    configureSearch()
    
    // 应用启动日志
    LoggerFactory.getLogger("ContentServiceApplication").info("内容服务启动成功，运行在端口: 8083")
} 