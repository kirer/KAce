package com.kace.user.api.controller

import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.model.dto.PageDto
import com.kace.common.model.dto.ResponseDto
import com.kace.user.api.request.CreateUserRequest
import com.kace.user.api.request.UpdateUserRequest
import com.kace.user.api.response.UserResponse
import com.kace.user.domain.model.User
import com.kace.user.domain.model.UserStatus
import com.kace.user.domain.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import org.koin.ktor.ext.inject
import java.util.*

/**
 * 用户控制器
 */
fun Route.userController() {
    val userService by inject<UserService>()
    
    route("/users") {
        // 获取用户列表
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            val status = call.request.queryParameters["status"]?.let { UserStatus.valueOf(it) }
            val query = call.request.queryParameters["query"]
            
            val usersPage = userService.findAll(page, size, status, query)
            val responseDto = PageDto(
                content = usersPage.content.map { it.toResponse() },
                pageNumber = usersPage.pageNumber,
                pageSize = usersPage.pageSize,
                totalElements = usersPage.totalElements,
                totalPages = usersPage.totalPages
            )
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(responseDto))
        }
        
        // 创建用户
        post {
            val request = call.receive<CreateUserRequest>()
            
            // 验证请求
            request.validate()
            
            val user = User(
                id = UUID.randomUUID().toString(),
                username = request.username,
                email = request.email,
                firstName = request.firstName,
                lastName = request.lastName,
                avatarUrl = request.avatarUrl,
                status = UserStatus.ACTIVE,
                roles = emptyList(),
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
            
            val createdUser = userService.createUser(user)
            call.respond(HttpStatusCode.Created, ResponseDto.success(createdUser.toResponse()))
        }
        
        // 根据ID获取用户
        get("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            
            val user = userService.findById(id) ?: throw NotFoundException("用户不存在")
            call.respond(HttpStatusCode.OK, ResponseDto.success(user.toResponse()))
        }
        
        // 更新用户
        put("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            val request = call.receive<UpdateUserRequest>()
            
            // 验证请求
            request.validate()
            
            val user = userService.findById(id) ?: throw NotFoundException("用户不存在")
            
            val updatedUser = user.copy(
                username = request.username ?: user.username,
                email = request.email ?: user.email,
                firstName = request.firstName ?: user.firstName,
                lastName = request.lastName ?: user.lastName,
                avatarUrl = request.avatarUrl ?: user.avatarUrl,
                status = request.status?.let { UserStatus.valueOf(it) } ?: user.status,
                updatedAt = Clock.System.now()
            )
            
            val result = userService.updateUser(updatedUser)
            call.respond(HttpStatusCode.OK, ResponseDto.success(result.toResponse()))
        }
        
        // 删除用户
        delete("/{id}") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            
            val success = userService.deleteUser(id)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                throw NotFoundException("用户不存在")
            }
        }
        
        // 获取用户资料
        get("/{id}/profile") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            
            val profile = userService.findProfileByUserId(id) ?: throw NotFoundException("用户资料不存在")
            call.respond(HttpStatusCode.OK, ResponseDto.success(profile))
        }
        
        // 更新用户资料
        put("/{id}/profile") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            
            val profile = call.receive<Map<String, Any>>()
            val updatedProfile = userService.updateProfile(id, profile)
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(updatedProfile))
        }
        
        // 获取用户角色
        get("/{id}/roles") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            
            val roles = userService.getUserRoles(id)
            call.respond(HttpStatusCode.OK, ResponseDto.success(roles))
        }
        
        // 添加用户角色
        post("/{id}/roles") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            val roleIds = call.receive<List<String>>()
            
            val roles = userService.addRolesToUser(id, roleIds)
            call.respond(HttpStatusCode.OK, ResponseDto.success(roles))
        }
        
        // 移除用户角色
        delete("/{id}/roles/{roleId}") {
            val id = call.parameters["id"] ?: throw BadRequestException("用户ID不能为空")
            val roleId = call.parameters["roleId"] ?: throw BadRequestException("角色ID不能为空")
            
            val success = userService.removeRoleFromUser(id, roleId)
            if (success) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                throw NotFoundException("用户或角色不存在")
            }
        }
    }
}

/**
 * 将用户模型转换为响应对象
 */
private fun User.toResponse(): UserResponse {
    return UserResponse(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatarUrl = avatarUrl,
        status = status.name,
        roles = roles,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * 验证创建用户请求
 */
private fun CreateUserRequest.validate() {
    if (username.isBlank()) throw BadRequestException("用户名不能为空")
    if (email.isBlank()) throw BadRequestException("邮箱不能为空")
    if (firstName.isBlank()) throw BadRequestException("名字不能为空")
    if (lastName.isBlank()) throw BadRequestException("姓氏不能为空")
}

/**
 * 验证更新用户请求
 */
private fun UpdateUserRequest.validate() {
    username?.let { if (it.isBlank()) throw BadRequestException("用户名不能为空") }
    email?.let { if (it.isBlank()) throw BadRequestException("邮箱不能为空") }
    firstName?.let { if (it.isBlank()) throw BadRequestException("名字不能为空") }
    lastName?.let { if (it.isBlank()) throw BadRequestException("姓氏不能为空") }
    status?.let {
        try {
            UserStatus.valueOf(it)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("无效的用户状态")
        }
    }
} 