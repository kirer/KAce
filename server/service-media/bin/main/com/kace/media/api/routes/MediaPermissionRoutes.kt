package com.kace.media.api.routes

import com.kace.media.api.controller.MediaPermissionController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * 配置媒体权限路由
 */
fun Application.configureMediaPermissionRoutes() {
    val mediaPermissionController: MediaPermissionController by inject()
    
    routing {
        route("/api/v1") {
            // 媒体权限相关路由
            route("/media/{mediaId}/permissions") {
                get {
                    mediaPermissionController.getMediaPermissions(call)
                }
                
                post {
                    mediaPermissionController.createPermission(call)
                }
                
                // 授权用户权限
                post("/users") {
                    mediaPermissionController.grantUserMediaPermission(call)
                }
                
                // 授权角色权限
                post("/roles") {
                    mediaPermissionController.grantRoleMediaPermission(call)
                }
                
                // 授权组织权限
                post("/organizations") {
                    mediaPermissionController.grantOrganizationMediaPermission(call)
                }
                
                // 设为公开
                post("/public") {
                    mediaPermissionController.makeMediaPublic(call)
                }
                
                // 设为私有
                delete("/public") {
                    mediaPermissionController.makeMediaPrivate(call)
                }
                
                // 权限检查
                post("/check") {
                    mediaPermissionController.checkMediaPermission(call)
                }
            }
            
            // 文件夹权限相关路由
            route("/folders/{folderId}/permissions") {
                get {
                    mediaPermissionController.getFolderPermissions(call)
                }
                
                post {
                    mediaPermissionController.createPermission(call)
                }
                
                // 授权用户权限
                post("/users") {
                    mediaPermissionController.grantUserFolderPermission(call)
                }
                
                // 授权角色权限
                post("/roles") {
                    mediaPermissionController.grantRoleFolderPermission(call)
                }
                
                // 授权组织权限
                post("/organizations") {
                    mediaPermissionController.grantOrganizationFolderPermission(call)
                }
                
                // 设为公开
                post("/public") {
                    mediaPermissionController.makeFolderPublic(call)
                }
                
                // 设为私有
                delete("/public") {
                    mediaPermissionController.makeFolderPrivate(call)
                }
                
                // 权限检查
                post("/check") {
                    mediaPermissionController.checkFolderPermission(call)
                }
            }
            
            // 单个权限操作
            route("/permissions/{id}") {
                delete {
                    mediaPermissionController.deletePermission(call)
                }
            }
        }
    }
} 