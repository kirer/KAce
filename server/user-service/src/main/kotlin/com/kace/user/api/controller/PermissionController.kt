package com.kace.user.api.controller

import com.kace.common.exception.EntityNotFoundException
import com.kace.user.api.request.PermissionCreateRequest
import com.kace.user.api.request.PermissionUpdateRequest
import com.kace.user.api.response.PageResponse
import com.kace.user.api.response.PermissionResponse
import com.kace.user.domain.model.PermissionCreateRequest as DomainPermissionCreateRequest
import com.kace.user.domain.model.PermissionUpdateRequest as DomainPermissionUpdateRequest
import com.kace.user.domain.service.PermissionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

/**
 * 权限控制器
 */
fun Route.permissionRoutes() {
    val permissionService by inject<PermissionService>()
    val logger = LoggerFactory.getLogger("PermissionController")
    
    route("/api/permissions") {
        // 获取权限列表
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
            val category = call.request.queryParameters["category"]
            val query = call.request.queryParameters["query"]
            
            val permissions = permissionService.getPermissions(page, size, category, query)
            
            call.respond(
                PageResponse(
                    content = permissions.content.map { PermissionResponse.fromModel(it) },
                    page = permissions.page,
                    size = permissions.size,
                    totalElements = permissions.totalElements,
                    totalPages = permissions.totalPages
                )
            )
        }
        
        // 获取所有权限
        get("/all") {
            val permissions = permissionService.getAllPermissions()
            call.respond(permissions.map { PermissionResponse.fromModel(it) })
        }
        
        // 根据分类获取权限
        get("/category/{category}") {
            val category = call.parameters["category"] ?: throw IllegalArgumentException("分类不能为空")
            
            val permissions = permissionService.getPermissionsByCategory(category)
            call.respond(permissions.map { PermissionResponse.fromModel(it) })
        }
        
        // 创建权限
        post {
            val request = call.receive<PermissionCreateRequest>()
            val userId = call.principal<com.kace.common.security.UserPrincipal>()?.id
                ?: throw IllegalStateException("用户未认证")
            
            val domainRequest = DomainPermissionCreateRequest(
                name = request.name,
                code = request.code,
                description = request.description,
                category = request.category
            )
            
            val permission = permissionService.createPermission(domainRequest)
            call.respond(HttpStatusCode.Created, PermissionResponse.fromModel(permission))
        }
        
        // 权限详情
        get("/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("权限ID不能为空")
            
            val permission = permissionService.getPermission(id) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(PermissionResponse.fromModel(permission))
        }
        
        // 更新权限
        put("/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("权限ID不能为空")
            val request = call.receive<PermissionUpdateRequest>()
            val userId = call.principal<com.kace.common.security.UserPrincipal>()?.id
                ?: throw IllegalStateException("用户未认证")
            
            val domainRequest = DomainPermissionUpdateRequest(
                name = request.name,
                description = request.description,
                category = request.category
            )
            
            val permission = permissionService.updatePermission(id, domainRequest)
            call.respond(PermissionResponse.fromModel(permission))
        }
        
        // 删除权限
        delete("/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("权限ID不能为空")
            
            val success = permissionService.deletePermission(id)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 初始化系统权限
        post("/init") {
            val permissions = permissionService.initSystemPermissions()
            call.respond(permissions.map { PermissionResponse.fromModel(it) })
        }
    }
} 