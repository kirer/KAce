package com.kace.auth.api.controller

import com.kace.auth.api.response.PermissionResponse
import com.kace.auth.domain.service.PermissionService
import com.kace.common.exception.BadRequestException
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.ResponseDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

/**
 * 权限控制器
 */
class PermissionController {
    private val permissionService by inject<PermissionService>()
    
    /**
     * 配置权限路由
     */
    fun Route.permissionRoutes() {
        route("/api/permissions") {
            // 获取所有权限
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                val resource = call.request.queryParameters["resource"]
                
                val pageRequest = PageRequest(page = page, size = size)
                
                val permissions = if (resource != null) {
                    permissionService.getPermissionsByResource(resource, pageRequest)
                } else {
                    permissionService.getAllPermissions(pageRequest)
                }
                
                val permissionResponses = permissions.data.map { permission ->
                    PermissionResponse(
                        id = permission.id.toString(),
                        name = permission.name,
                        description = permission.description,
                        resource = permission.resource,
                        action = permission.action,
                        isSystem = permission.isSystem
                    )
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(
                        data = permissionResponses,
                        meta = mapOf(
                            "page" to permissions.page,
                            "size" to permissions.size,
                            "total" to permissions.total,
                            "pages" to permissions.pages
                        )
                    )
                )
            }
            
            // 获取单个权限
            get("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少权限ID")
                
                val permission = permissionService.getPermission(UUID.fromString(id))
                
                val permissionResponse = PermissionResponse(
                    id = permission.id.toString(),
                    name = permission.name,
                    description = permission.description,
                    resource = permission.resource,
                    action = permission.action,
                    isSystem = permission.isSystem
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(permissionResponse)
                )
            }
            
            // 创建权限
            post {
                val request = call.receive<com.kace.auth.api.request.CreatePermissionRequest>()
                
                val permission = permissionService.createPermission(
                    name = request.name,
                    description = request.description,
                    resource = request.resource,
                    action = request.action
                )
                
                val permissionResponse = PermissionResponse(
                    id = permission.id.toString(),
                    name = permission.name,
                    description = permission.description,
                    resource = permission.resource,
                    action = permission.action,
                    isSystem = permission.isSystem
                )
                
                call.respond(
                    HttpStatusCode.Created,
                    ResponseDto.success(permissionResponse)
                )
            }
            
            // 更新权限
            put("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少权限ID")
                val request = call.receive<com.kace.auth.api.request.UpdatePermissionRequest>()
                
                val permission = permissionService.updatePermission(
                    id = UUID.fromString(id),
                    name = request.name,
                    description = request.description,
                    resource = request.resource,
                    action = request.action
                )
                
                val permissionResponse = PermissionResponse(
                    id = permission.id.toString(),
                    name = permission.name,
                    description = permission.description,
                    resource = permission.resource,
                    action = permission.action,
                    isSystem = permission.isSystem
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(permissionResponse)
                )
            }
            
            // 删除权限
            delete("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少权限ID")
                
                val success = permissionService.deletePermission(UUID.fromString(id))
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(mapOf("success" to success))
                )
            }
        }
    }
} 