package com.kace.auth.api.controller

import com.kace.auth.api.response.UserResponse
import com.kace.auth.domain.service.UserService
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
 * 用户控制器
 */
class UserController {
    private val userService by inject<UserService>()
    
    /**
     * 配置用户路由
     */
    fun Route.userRoutes() {
        route("/api/users") {
            // 获取所有用户
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                
                val pageRequest = PageRequest(page = page, size = size)
                val users = userService.getAllUsers(pageRequest)
                
                val userResponses = users.data.map { user ->
                    UserResponse(
                        id = user.id.toString(),
                        username = user.username,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        active = user.active,
                        verified = user.verified,
                        roles = user.roles.map { role ->
                            com.kace.auth.api.response.RoleResponse(
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
                        },
                        createdAt = user.createdAt.toString(),
                        updatedAt = user.updatedAt.toString(),
                        lastLoginAt = user.lastLoginAt?.toString()
                    )
                }
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(
                        data = userResponses,
                        meta = mapOf(
                            "page" to users.page,
                            "size" to users.size,
                            "total" to users.total,
                            "pages" to users.pages
                        )
                    )
                )
            }
            
            // 获取单个用户
            get("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少用户ID")
                
                val user = userService.getUser(UUID.fromString(id))
                
                val userResponse = UserResponse(
                    id = user.id.toString(),
                    username = user.username,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    active = user.active,
                    verified = user.verified,
                    roles = user.roles.map { role ->
                        com.kace.auth.api.response.RoleResponse(
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
                    },
                    createdAt = user.createdAt.toString(),
                    updatedAt = user.updatedAt.toString(),
                    lastLoginAt = user.lastLoginAt?.toString()
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(userResponse)
                )
            }
            
            // 创建用户
            post {
                val request = call.receive<com.kace.auth.api.request.CreateUserRequest>()
                
                val user = userService.createUser(
                    username = request.username,
                    email = request.email,
                    password = request.password,
                    firstName = request.firstName,
                    lastName = request.lastName,
                    roleIds = request.roleIds?.map { UUID.fromString(it) }?.toSet() ?: emptySet()
                )
                
                val userResponse = UserResponse(
                    id = user.id.toString(),
                    username = user.username,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    active = user.active,
                    verified = user.verified,
                    roles = user.roles.map { role ->
                        com.kace.auth.api.response.RoleResponse(
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
                    },
                    createdAt = user.createdAt.toString(),
                    updatedAt = user.updatedAt.toString(),
                    lastLoginAt = user.lastLoginAt?.toString()
                )
                
                call.respond(
                    HttpStatusCode.Created,
                    ResponseDto.success(userResponse)
                )
            }
            
            // 更新用户
            put("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少用户ID")
                val request = call.receive<com.kace.auth.api.request.UpdateUserRequest>()
                
                val user = userService.updateUser(
                    id = UUID.fromString(id),
                    username = request.username,
                    email = request.email,
                    firstName = request.firstName,
                    lastName = request.lastName,
                    active = request.active,
                    verified = request.verified,
                    roleIds = request.roleIds?.map { UUID.fromString(it) }?.toSet()
                )
                
                val userResponse = UserResponse(
                    id = user.id.toString(),
                    username = user.username,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    active = user.active,
                    verified = user.verified,
                    roles = user.roles.map { role ->
                        com.kace.auth.api.response.RoleResponse(
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
                    },
                    createdAt = user.createdAt.toString(),
                    updatedAt = user.updatedAt.toString(),
                    lastLoginAt = user.lastLoginAt?.toString()
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(userResponse)
                )
            }
            
            // 删除用户
            delete("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("缺少用户ID")
                
                val success = userService.deleteUser(UUID.fromString(id))
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(mapOf("success" to success))
                )
            }
            
            // 为用户添加角色
            post("/{userId}/roles/{roleId}") {
                val userId = call.parameters["userId"] ?: throw BadRequestException("缺少用户ID")
                val roleId = call.parameters["roleId"] ?: throw BadRequestException("缺少角色ID")
                
                val user = userService.addRoleToUser(
                    userId = UUID.fromString(userId),
                    roleId = UUID.fromString(roleId)
                )
                
                val userResponse = UserResponse(
                    id = user.id.toString(),
                    username = user.username,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    active = user.active,
                    verified = user.verified,
                    roles = user.roles.map { role ->
                        com.kace.auth.api.response.RoleResponse(
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
                    },
                    createdAt = user.createdAt.toString(),
                    updatedAt = user.updatedAt.toString(),
                    lastLoginAt = user.lastLoginAt?.toString()
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(userResponse)
                )
            }
            
            // 从用户中移除角色
            delete("/{userId}/roles/{roleId}") {
                val userId = call.parameters["userId"] ?: throw BadRequestException("缺少用户ID")
                val roleId = call.parameters["roleId"] ?: throw BadRequestException("缺少角色ID")
                
                val user = userService.removeRoleFromUser(
                    userId = UUID.fromString(userId),
                    roleId = UUID.fromString(roleId)
                )
                
                val userResponse = UserResponse(
                    id = user.id.toString(),
                    username = user.username,
                    email = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    active = user.active,
                    verified = user.verified,
                    roles = user.roles.map { role ->
                        com.kace.auth.api.response.RoleResponse(
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
                    },
                    createdAt = user.createdAt.toString(),
                    updatedAt = user.updatedAt.toString(),
                    lastLoginAt = user.lastLoginAt?.toString()
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(userResponse)
                )
            }
        }
    }
} 