package com.kace.user.infrastructure.config

import com.kace.user.api.controller.permissionRoutes
import com.kace.user.api.controller.roleRoutes
import com.kace.user.api.controller.userRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 配置路由
 */
fun Application.configureRouting() {
    routing {
        // 健康检查路由
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "UP"))
        }
        
        // 公共API路由
        route("/api/public") {
            // 公共API路由配置
        }
        
        // 需要认证的API路由
        authenticate("jwt") {
            // 用户路由
            userRoutes()
            
            // 角色路由
            roleRoutes()
            
            // 权限路由
            permissionRoutes()
        }
    }
} 