package com.kace.media

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.kace.media.infrastructure.config.configureKoin
import com.kace.media.infrastructure.config.configureRouting
import com.kace.media.infrastructure.config.configureSecurity
import com.kace.media.infrastructure.config.configureSerialization
import com.kace.media.infrastructure.config.configureMonitoring
import com.kace.media.infrastructure.config.configureDatabases
import com.kace.media.infrastructure.config.configureStorage
import com.kace.media.infrastructure.config.configureCdn

/**
 * 媒体服务应用程序入口
 */
fun main() {
    embeddedServer(Netty, port = 8082, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * 应用程序模块配置
 */
fun Application.module() {
    configureKoin()
    configureSerialization()
    configureDatabases()
    configureSecurity()
    configureMonitoring()
    configureStorage()
    configureCdn()
    configureRouting()
} 