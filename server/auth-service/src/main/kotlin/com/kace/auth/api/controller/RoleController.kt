package com.kace.auth.api.controller

import com.kace.auth.api.response.RoleResponse
import com.kace.auth.domain.service.RoleService
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
 * 角色控制器
 */
class RoleController {
    private val roleService by inject<RoleService>()
    
    /**
     * 配置角色路由
     */
    fun Route.roleRoutes() {
        route("/api/roles") {
            // 获取所有角色
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                
                val pageRequest = PageRequest(page = page, size = size)
                val roles = roleService.getAllRoles(pageRequest)
                
                val roleResponses = roles.data.map { role ->
                    RoleResponse(
                        id = role.id.toString(),
                        name = role.name,
                        description = role.description,
                        permissions = role.permissions.map { permission ->
                            com.kace.auth.api.response.PermissionResponse(
                                id = permission.id.toString(),
                                name = permission.name,
                                description = permission.description,
                                resource = permission.resource,
                                action = permission.action,
                                isSystem = permission.isSystem
                            )
                        },
                        isSystem = role.isSystem
                    )
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(
                        data = roleResponses,
                        meta = mapOf(
                            "page" to roles.page,
                            "size" to roles.size,
                            "total" to roles.total,
                            "pages" to roles.pages
                        )
                    )
                )
            }
            
            // 获取单个角色
            get("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少角色ID")
                
                val role = roleService.getRole(UUID.fromString(id))
                
                val roleResponse = RoleResponse(
                    id = role.id.toString(),
                    name = role.name,
                    description = role.description,
                    permissions = role.permissions.map { permission ->
                        com.kace.auth.api.response.PermissionResponse(
                            id = permission.id.toString(),
                            name = permission.name,
                            description = permission.description,
                            resource = permission.resource,
                            action = permission.action,
                            isSystem = permission.isSystem
                        )
                    },
                    isSystem = role.isSystem
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(roleResponse)
                )
            }
            
            // 创建角色
            post {
                val request = call.receive<com.kace.auth.api.request.CreateRoleRequest>()
                
                val role = roleService.createRole(
                    name = request.name,
                    description = request.description,
                    permissionIds = request.permissionIds?.map { UUID.fromString(it) }?.toSet() ?: emptySet()
                )
                
                val roleResponse = RoleResponse(
                    id = role.id.toString(),
                    name = role.name,
                    description = role.description,
                    permissions = role.permissions.map { permission ->
                        com.kace.auth.api.response.PermissionResponse(
                            id = permission.id.toString(),
                            name = permission.name,
                            description = permission.description,
                            resource = permission.resource,
                            action = permission.action,
                            isSystem = permission.isSystem
                        )
                    },
                    isSystem = role.isSystem
                )
                
                call.respond(
                    HttpStatusCode.Created,
                    ResponseDto.success(roleResponse)
                )
            }
            
            // 更新角色
            put("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少角色ID")
                val request = call.receive<com.kace.auth.api.request.UpdateRoleRequest>()
                
                val role = roleService.updateRole(
                    id = UUID.fromString(id),
                    name = request.name,
                    description = request.description,
                    permissionIds = request.permissionIds?.map { UUID.fromString(it) }?.toSet() ?: emptySet()
                )
                
                val roleResponse = RoleResponse(
                    id = role.id.toString(),
                    name = role.name,
                    description = role.description,
                    permissions = role.permissions.map { permission ->
                        com.kace.auth.api.response.PermissionResponse(
                            id = permission.id.toString(),
                            name = permission.name,
                            description = permission.description,
                            resource = permission.resource,
                            action = permission.action,
                            isSystem = permission.isSystem
                        )
                    },
                    isSystem = role.isSystem
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(roleResponse)
                )
            }
            
            // 删除角色
            delete("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少角色ID")
                
                val success = roleService.deleteRole(UUID.fromString(id))
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(mapOf("success" to success))
                )
            }
            
            // 为角色添加权限
            post("/{roleId}/permissions/{permissionId}") {
                val roleId = call.parameters["roleId"] ?: throw BadRequestException("缺少角色ID")
                val permissionId = call.parameters["permissionId"] ?: throw BadRequestException("缺少权限ID")
                
                val role = roleService.addPermissionToRole(
                    roleId = UUID.fromString(roleId),
                    permissionId = UUID.fromString(permissionId)
                )
                
                val roleResponse = RoleResponse(
                    id = role.id.toString(),
                    name = role.name,
                    description = role.description,
                    permissions = role.permissions.map { permission ->
                        com.kace.auth.api.response.PermissionResponse(
                            id = permission.id.toString(),
                            name = permission.name,
                            description = permission.description,
                            resource = permission.resource,
                            action = permission.action,
                            isSystem = permission.isSystem
                        )
                    },
                    isSystem = role.isSystem
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(roleResponse)
                )
            }
            
            // 从角色中移除权限
            delete("/{roleId}/permissions/{permissionId}") {
                val roleId = call.parameters["roleId"] ?: throw BadRequestException("缺少角色ID")
                val permissionId = call.parameters["permissionId"] ?: throw BadRequestException("缺少权限ID")
                
                val role = roleService.removePermissionFromRole(
                    roleId = UUID.fromString(roleId),
                    permissionId = UUID.fromString(permissionId)
                )
                
                val roleResponse = RoleResponse(
                    id = role.id.toString(),
                    name = role.name,
                    description = role.description,
                    permissions = role.permissions.map { permission ->
                        com.kace.auth.api.response.PermissionResponse(
                            id = permission.id.toString(),
                            name = permission.name,
                            description = permission.description,
                            resource = permission.resource,
                            action = permission.action,
                            isSystem = permission.isSystem
                        )
                    },
                    isSystem = role.isSystem
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(roleResponse)
                )
            }
        }
    }
} 