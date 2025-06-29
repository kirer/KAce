package com.kace.auth.infrastructure.config

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

/**
 * 监控配置
 */
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}
