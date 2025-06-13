package com.kace.user.api.controller

import com.kace.common.model.dto.ResponseDto
import com.kace.user.api.request.*
import com.kace.user.api.response.UserProfileResponse
import com.kace.user.api.response.UserResponse
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

/**
 * 用户控制器
 */
fun Route.userController() {
    val userService by inject<UserService>()
    
    route("/users") {
        // 获取用户列表
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
            val status = call.request.queryParameters["status"]?.let { UserStatus.valueOf(it) }
            val query = call.request.queryParameters["q"]
            
            val users = userService.getUsers(page, size, status, query)
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(users))
        }
        
        // 创建用户
        post {
            val request = call.receive<CreateUserRequest>()
            
            // 创建用户
            val user = userService.createUser(
                com.kace.user.domain.model.User(
                    id = "",  // 由服务生成
                    username = request.username,
                    email = request.email,
                    firstName = request.firstName,
                    lastName = request.lastName,
                    avatarUrl = request.avatarUrl,
                    status = UserStatus.ACTIVE,
                    roles = listOf(),
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
            )
            
            // 转换为响应
            val response = UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                avatarUrl = user.avatarUrl,
                status = user.status.name,
                roles = user.roles,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
            
            call.respond(HttpStatusCode.Created, ResponseDto.success(response))
        }
        
        // 用户详情
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseDto.error("缺少用户ID"))
            
            val user = userService.getUserById(id) ?: return@get call.respond(HttpStatusCode.NotFound, ResponseDto.error("用户不存在"))
            
            // 转换为响应
            val response = UserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                avatarUrl = user.avatarUrl,
                status = user.status.name,
                roles = user.roles,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(response))
        }
        
        // 更新用户
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseDto.error("缺少用户ID"))
            val request = call.receive<UpdateUserRequest>()
            
            // 获取当前用户
            val currentUser = userService.getUserById(id) ?: return@put call.respond(HttpStatusCode.NotFound, ResponseDto.error("用户不存在"))
            
            // 更新用户
            val updatedUser = userService.updateUser(
                currentUser.copy(
                    firstName = request.firstName ?: currentUser.firstName,
                    lastName = request.lastName ?: currentUser.lastName,
                    avatarUrl = request.avatarUrl ?: currentUser.avatarUrl,
                    updatedAt = Clock.System.now()
                )
            )
            
            // 转换为响应
            val response = UserResponse(
                id = updatedUser.id,
                username = updatedUser.username,
                email = updatedUser.email,
                firstName = updatedUser.firstName,
                lastName = updatedUser.lastName,
                avatarUrl = updatedUser.avatarUrl,
                status = updatedUser.status.name,
                roles = updatedUser.roles,
                createdAt = updatedUser.createdAt,
                updatedAt = updatedUser.updatedAt
            )
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(response))
        }
        
        // 更新用户状态
        patch("/{id}/status") {
            val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.BadRequest, ResponseDto.error("缺少用户ID"))
            val request = call.receive<UpdateUserStatusRequest>()
            
            val status = try {
                UserStatus.valueOf(request.status)
            } catch (e: IllegalArgumentException) {
                return@patch call.respond(HttpStatusCode.BadRequest, ResponseDto.error("无效的用户状态"))
            }
            
            val success = userService.updateUserStatus(id, status)
            if (!success) {
                call.respond(HttpStatusCode.NotFound, ResponseDto.error("用户不存在"))
                return@patch
            }
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(null))
        }
        
        // 删除用户
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseDto.error("缺少用户ID"))
            
            val success = userService.deleteUser(id)
            if (!success) {
                call.respond(HttpStatusCode.NotFound, ResponseDto.error("用户不存在"))
                return@delete
            }
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(null))
        }
        
        // 用户资料
        route("/{id}/profile") {
            // 获取用户资料
            get {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseDto.error("缺少用户ID"))
                
                val profile = userService.getUserProfile(id) ?: return@get call.respond(HttpStatusCode.NotFound, ResponseDto.error("用户资料不存在"))
                
                // 转换为响应
                val response = UserProfileResponse(
                    id = profile.id,
                    userId = profile.userId,
                    bio = profile.bio,
                    phoneNumber = profile.phoneNumber,
                    birthDate = profile.birthDate,
                    gender = profile.gender?.name,
                    address = profile.address?.let {
                        com.kace.user.api.response.AddressResponse(
                            street = it.street,
                            city = it.city,
                            state = it.state,
                            country = it.country,
                            postalCode = it.postalCode
                        )
                    },
                    preferences = profile.preferences?.let {
                        com.kace.user.api.response.UserPreferencesResponse(
                            language = it.language,
                            theme = it.theme,
                            emailNotifications = it.emailNotifications,
                            pushNotifications = it.pushNotifications,
                            twoFactorAuth = it.twoFactorAuth
                        )
                    },
                    createdAt = profile.createdAt,
                    updatedAt = profile.updatedAt
                )
                
                call.respond(HttpStatusCode.OK, ResponseDto.success(response))
            }
            
            // 更新用户资料
            put {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, ResponseDto.error("缺少用户ID"))
                val request = call.receive<UpdateUserProfileRequest>()
                
                // 获取当前用户资料
                val currentProfile = userService.getUserProfile(id)
                
                // 创建或更新用户资料
                val profile = userService.saveUserProfile(
                    com.kace.user.domain.model.UserProfile(
                        id = currentProfile?.id ?: "",  // 由服务生成
                        userId = id,
                        bio = request.bio ?: currentProfile?.bio,
                        phoneNumber = request.phoneNumber ?: currentProfile?.phoneNumber,
                        birthDate = request.birthDate ?: currentProfile?.birthDate,
                        gender = request.gender?.let { com.kace.user.domain.model.Gender.valueOf(it) } ?: currentProfile?.gender,
                        address = request.address?.let {
                            com.kace.user.domain.model.Address(
                                street = it.street,
                                city = it.city,
                                state = it.state,
                                country = it.country,
                                postalCode = it.postalCode
                            )
                        } ?: currentProfile?.address,
                        preferences = request.preferences?.let {
                            com.kace.user.domain.model.UserPreferences(
                                language = it.language ?: currentProfile?.preferences?.language ?: "zh-CN",
                                theme = it.theme ?: currentProfile?.preferences?.theme ?: "light",
                                emailNotifications = it.emailNotifications ?: currentProfile?.preferences?.emailNotifications ?: true,
                                pushNotifications = it.pushNotifications ?: currentProfile?.preferences?.pushNotifications ?: true,
                                twoFactorAuth = it.twoFactorAuth ?: currentProfile?.preferences?.twoFactorAuth ?: false
                            )
                        } ?: currentProfile?.preferences,
                        createdAt = currentProfile?.createdAt ?: Clock.System.now(),
                        updatedAt = Clock.System.now()
                    )
                )
                
                // 转换为响应
                val response = UserProfileResponse(
                    id = profile.id,
                    userId = profile.userId,
                    bio = profile.bio,
                    phoneNumber = profile.phoneNumber,
                    birthDate = profile.birthDate,
                    gender = profile.gender?.name,
                    address = profile.address?.let {
                        com.kace.user.api.response.AddressResponse(
                            street = it.street,
                            city = it.city,
                            state = it.state,
                            country = it.country,
                            postalCode = it.postalCode
                        )
                    },
                    preferences = profile.preferences?.let {
                        com.kace.user.api.response.UserPreferencesResponse(
                            language = it.language,
                            theme = it.theme,
                            emailNotifications = it.emailNotifications,
                            pushNotifications = it.pushNotifications,
                            twoFactorAuth = it.twoFactorAuth
                        )
                    },
                    createdAt = profile.createdAt,
                    updatedAt = profile.updatedAt
                )
                
                call.respond(HttpStatusCode.OK, ResponseDto.success(response))
            }
        }
        
        // 更改密码
        post("/{id}/change-password") {
            val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, ResponseDto.error("缺少用户ID"))
            val request = call.receive<ChangePasswordRequest>()
            
            // 验证新密码和确认密码是否一致
            if (request.newPassword != request.confirmPassword) {
                call.respond(HttpStatusCode.BadRequest, ResponseDto.error("新密码和确认密码不一致"))
                return@post
            }
            
            // 验证当前密码
            val user = userService.getUserById(id) ?: return@post call.respond(HttpStatusCode.NotFound, ResponseDto.error("用户不存在"))
            val validCredentials = userService.validateCredentials(user.username, request.currentPassword)
            if (validCredentials == null) {
                call.respond(HttpStatusCode.BadRequest, ResponseDto.error("当前密码不正确"))
                return@post
            }
            
            // 更改密码逻辑应该在认证服务中实现
            // 这里只是示例，实际实现可能需要调用认证服务的API
            
            call.respond(HttpStatusCode.OK, ResponseDto.success(null))
        }
    }
} 