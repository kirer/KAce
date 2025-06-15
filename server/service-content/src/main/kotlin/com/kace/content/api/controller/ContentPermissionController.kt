package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.CreateContentPermissionRequest
import com.kace.content.api.request.UpdateContentPermissionRequest
import com.kace.content.api.response.ContentPermissionResponse
import com.kace.content.domain.model.ContentPermission
import com.kace.content.domain.service.ContentPermissionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class ContentPermissionController {
    
    fun Route.contentPermissionRoutes() {
        val contentPermissionService by inject<ContentPermissionService>()
        
        route("/api/v1/content-permissions") {
            // 创建内容权限
            post {
                val request = call.receive<CreateContentPermissionRequest>()
                val permission = contentPermissionService.createContentPermission(
                    contentId = request.contentId,
                    principalId = request.principalId,
                    principalType = request.principalType,
                    permission = request.permission
                )
                call.respond(HttpStatusCode.Created, Response.success(permission.toContentPermissionResponse()))
            }
            
            // 获取内容权限列表
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val contentId = call.request.queryParameters["contentId"]?.let { UUID.fromString(it) }
                val principalId = call.request.queryParameters["principalId"]
                val principalType = call.request.queryParameters["principalType"]
                
                val pageRequest = PageRequest(page, size)
                val permissions = contentPermissionService.findContentPermissions(
                    pageRequest = pageRequest,
                    contentId = contentId,
                    principalId = principalId,
                    principalType = principalType
                )
                
                val permissionResponses = permissions.items.map { it.toContentPermissionResponse() }
                val pageResponse = PageResponse(
                    items = permissionResponses,
                    page = permissions.page,
                    size = permissions.size,
                    total = permissions.total,
                    totalPages = permissions.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 根据ID获取内容权限
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid permission ID"))
                
                val permission = contentPermissionService.findContentPermissionById(id) 
                    ?: return@get call.respond(HttpStatusCode.NotFound, Response.error("Content permission not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(permission.toContentPermissionResponse()))
            }
            
            // 更新内容权限
            put("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@put call.respond(HttpStatusCode.BadRequest, Response.error("Invalid permission ID"))
                
                val request = call.receive<UpdateContentPermissionRequest>()
                val permission = contentPermissionService.updateContentPermission(
                    id = id,
                    permission = request.permission
                ) ?: return@put call.respond(HttpStatusCode.NotFound, Response.error("Content permission not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(permission.toContentPermissionResponse()))
            }
            
            // 删除内容权限
            delete("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, Response.error("Invalid permission ID"))
                
                val success = contentPermissionService.deleteContentPermission(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent, Response.success(null))
                } else {
                    call.respond(HttpStatusCode.NotFound, Response.error("Content permission not found"))
                }
            }
            
            // 检查用户是否有权限访问内容
            get("/check") {
                val contentId = call.request.queryParameters["contentId"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Content ID is required"))
                
                val principalId = call.request.queryParameters["principalId"] 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Principal ID is required"))
                
                val principalType = call.request.queryParameters["principalType"] 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Principal type is required"))
                
                val permissionType = call.request.queryParameters["permission"] 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Permission type is required"))
                
                val hasPermission = contentPermissionService.checkPermission(
                    contentId = contentId,
                    principalId = principalId,
                    principalType = principalType,
                    permission = permissionType
                )
                
                call.respond(HttpStatusCode.OK, Response.success(mapOf("hasPermission" to hasPermission)))
            }
            
            // 获取内容的所有权限
            get("/content/{contentId}") {
                val contentId = call.parameters["contentId"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                
                val permissions = contentPermissionService.findPermissionsByContent(contentId)
                call.respond(HttpStatusCode.OK, Response.success(permissions.map { it.toContentPermissionResponse() }))
            }
            
            // 获取用户的所有内容权限
            get("/principal/{principalType}/{principalId}") {
                val principalType = call.parameters["principalType"] 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Principal type is required"))
                
                val principalId = call.parameters["principalId"] 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Principal ID is required"))
                
                val permissions = contentPermissionService.findPermissionsByPrincipal(principalType, principalId)
                call.respond(HttpStatusCode.OK, Response.success(permissions.map { it.toContentPermissionResponse() }))
            }
        }
    }
    
    private fun ContentPermission.toContentPermissionResponse(): ContentPermissionResponse {
        return ContentPermissionResponse(
            id = id,
            contentId = contentId,
            principalId = principalId,
            principalType = principalType,
            permission = permission,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 