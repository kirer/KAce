package com.kace.user.api

import com.kace.user.api.controller.userController
import io.ktor.server.application.*
import io.ktor.server.routing.*

/**
 * 配置路由
 */
fun Application.configureRouting() {
    routing {
        route("/api") {
            userController()
            // 组织控制器将在这里添加
        }
    }
} 