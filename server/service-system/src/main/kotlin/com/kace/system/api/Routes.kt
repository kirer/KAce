package com.kace.system.api

import com.kace.system.api.controller.SystemConfigController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * 配置API路由
 */
fun Application.configureRouting() {
    routing {
        route("/api/system") {
            // 系统配置API
            val systemConfigController by inject<SystemConfigController>()
            systemConfigController.registerRoutes(this)
            
            // 其他系统服务API可以添加在这里
        }
    }
}