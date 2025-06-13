package com.kace.user.api.controller

import com.kace.common.exception.EntityNotFoundException
import com.kace.user.api.request.AssignRoleRequest
import com.kace.user.api.request.RoleCreateRequest
import com.kace.user.api.request.RoleUpdateRequest
import com.kace.user.api.response.PageResponse
import com.kace.user.api.response.RoleResponse
import com.kace.user.api.response.UserIdResponse
import com.kace.user.domain.model.Role
import com.kace.user.domain.model.RoleCreateRequest as DomainRoleCreateRequest
import com.kace.user.domain.model.RoleUpdateRequest as DomainRoleUpdateRequest
import com.kace.user.domain.service.RoleService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

/**
 * 角色控制器
 */
fun Route.roleRoutes() {
    val roleService by inject<RoleService>()
    val logger = LoggerFactory.getLogger("RoleController")
    
    route("/api/roles") {
        // 获取所有角色，支持分页
        get {
            try {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                val organizationId = call.request.queryParameters["organizationId"]
                val query = call.request.queryParameters["query"]
                
                val roles = roleService.getRoles(page, size, organizationId, query)
                val response = PageResponse(
                    content = roles.content.map { RoleResponse.fromModel(it) },
                    page = roles.page,
                    size = roles.size,
                    totalElements = roles.totalElements,
                    totalPages = roles.totalPages
                )
                
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("获取角色列表失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "获取角色列表失败: ${e.message}"))
            }
        }
        
        // 创建角色
        post {
            try {
                val request = call.receive<RoleCreateRequest>()
                val userId = call.principal<com.kace.common.security.UserPrincipal>()?.id
                    ?: throw IllegalStateException("用户未认证")
                
                val domainRequest = DomainRoleCreateRequest(
                    name = request.name,
                    description = request.description,
                    permissions = request.permissions,
                    organizationId = request.organizationId
                )
                
                val role = roleService.createRole(domainRequest)
                call.respond(HttpStatusCode.Created, RoleResponse.fromModel(role))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                logger.error("创建角色失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "创建角色失败: ${e.message}"))
            }
        }
        
        // 根据ID获取角色
        get("/{id}") {
            try {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "角色ID不能为空"))
                
                val role = roleService.getRole(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "角色不存在: $id"))
                
                call.respond(HttpStatusCode.OK, RoleResponse.fromModel(role))
            } catch (e: Exception) {
                logger.error("获取角色失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "获取角色失败: ${e.message}"))
            }
        }
        
        // 更新角色
        put("/{id}") {
            try {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "角色ID不能为空"))
                val request = call.receive<RoleUpdateRequest>()
                val userId = call.principal<com.kace.common.security.UserPrincipal>()?.id
                    ?: throw IllegalStateException("用户未认证")
                
                val domainRequest = DomainRoleUpdateRequest(
                    name = request.name,
                    description = request.description,
                    permissions = request.permissions
                )
                
                val role = roleService.updateRole(id, domainRequest)
                call.respond(HttpStatusCode.OK, RoleResponse.fromModel(role))
            } catch (e: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                logger.error("更新角色失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "更新角色失败: ${e.message}"))
            }
        }
        
        // 删除角色
        delete("/{id}") {
            try {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "角色ID不能为空"))
                
                val success = roleService.deleteRole(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "角色不存在或删除失败: $id"))
                }
            } catch (e: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                logger.error("删除角色失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "删除角色失败: ${e.message}"))
            }
        }
        
        // 获取角色的所有用户
        get("/{id}/users") {
            try {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "角色ID不能为空"))
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                
                val users = roleService.getRoleUsers(id, page, size)
                val response = PageResponse(
                    content = users.content.map { UserIdResponse(it) },
                    page = users.page,
                    size = users.size,
                    totalElements = users.totalElements,
                    totalPages = users.totalPages
                )
                
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                logger.error("获取角色用户列表失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "获取角色用户列表失败: ${e.message}"))
            }
        }
        
        // 为用户分配角色
        post("/{id}/users") {
            try {
                val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "角色ID不能为空"))
                val request = call.receive<AssignRoleRequest>()
                val userId = call.principal<com.kace.common.security.UserPrincipal>()?.id
                    ?: throw IllegalStateException("用户未认证")
                
                val success = roleService.assignRoleToUser(id, request.userId, userId)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            } catch (e: EntityNotFoundException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                logger.error("分配角色失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "分配角色失败: ${e.message}"))
            }
        }
        
        // 移除用户的角色
        delete("/{id}/users/{userId}") {
            try {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "角色ID不能为空"))
                val userId = call.parameters["userId"] ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "用户ID不能为空"))
                
                val success = roleService.removeRoleFromUser(id, userId)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "用户角色不存在或移除失败"))
                }
            } catch (e: Exception) {
                logger.error("移除用户角色失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "移除用户角色失败: ${e.message}"))
            }
        }
        
        // 初始化系统角色
        post("/init") {
            try {
                val userId = call.principal<com.kace.common.security.UserPrincipal>()?.id
                    ?: throw IllegalStateException("用户未认证")
                
                val roles = roleService.initSystemRoles(userId)
                call.respond(HttpStatusCode.OK, roles.map { RoleResponse.fromModel(it) })
            } catch (e: Exception) {
                logger.error("初始化系统角色失败", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "初始化系统角色失败: ${e.message}"))
            }
        }
    }
} 