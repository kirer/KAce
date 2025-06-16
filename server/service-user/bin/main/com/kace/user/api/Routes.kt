package com.kace.user.api

import com.kace.user.api.controller.authRoutes
import com.kace.user.api.controller.organizationController
import com.kace.user.api.controller.roleController
import com.kace.user.api.controller.userController
import com.kace.user.api.controller.userPreferenceRoutes
import com.kace.user.domain.service.AuthenticationService
import com.kace.user.domain.service.UserPreferenceService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * 配置API路由
 */
fun Application.configureRoutes() {
    val authService by inject<AuthenticationService>()
    val preferenceService by inject<UserPreferenceService>()
    
    routing {
        route("/api") {
            // 受保护的路由
            authenticate("jwt") {
                userController()
                roleController()
                organizationController()
            }
        }
        
        // 认证路由
        authRoutes(authService)
        
        // 用户偏好设置路由
        userPreferenceRoutes(preferenceService)
    }
} 