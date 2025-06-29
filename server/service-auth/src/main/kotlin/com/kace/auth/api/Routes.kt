package com.kace.auth.api

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.kace.auth.api.controller.AuthController
import com.kace.auth.api.controller.UserController
import com.kace.auth.api.controller.RoleController
import com.kace.auth.api.controller.PermissionController
import org.koin.ktor.ext.inject

/**
 * 配置路由
 */
fun Application.configureRoutes() {
    routing {
        // 注入控制器
        val authController by inject<AuthController>()
        val userController by inject<UserController>()
        val roleController by inject<RoleController>()
        val permissionController by inject<PermissionController>()
        
        // 认证路由
        authController.authRoutes()
        
        // 用户路由
        userController.userRoutes()
        
        // 角色路由
        roleController.roleRoutes()
        
        // 权限路由
        permissionController.permissionRoutes()
    }
}
