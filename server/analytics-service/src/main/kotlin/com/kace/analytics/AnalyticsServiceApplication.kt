package com.kace.analytics

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kace.analytics.config.*

/**
 * 分析服务应用入口
 */
fun main() {
    embeddedServer(Netty, port = 8083, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * 应用模块配置
 */
fun Application.module() {
    configureKoin()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureDatabases()
    configureRouting()
} 