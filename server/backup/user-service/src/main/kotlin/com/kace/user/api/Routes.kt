package com.kace.user.api

import com.kace.user.api.controller.organizationController
import com.kace.user.api.controller.roleController
import com.kace.user.api.controller.userController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

/**
 * 配置路由
 */
fun Application.configureRouting() {
    routing {
        route("/api") {
            // 受保护的路由
            authenticate("jwt") {
                userController()
                roleController()
                organizationController()
            }
        }
    }
} 