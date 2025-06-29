package com.kace.auth.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import com.kace.auth.api.request.AuthRequest
import com.kace.auth.api.request.ChangePasswordRequest
import com.kace.auth.api.request.RefreshTokenRequest
import com.kace.auth.api.request.RegisterRequest
import com.kace.auth.api.request.ResetPasswordRequest
import com.kace.auth.api.response.AuthResponse
import com.kace.auth.domain.service.AuthService
import com.kace.common.model.dto.ResponseDto

/**
 * 认证控制器
 */
class AuthController {
    private val authService by inject<AuthService>()
    
    /**
     * 配置认证路由
     */
    fun Route.authRoutes() {
        route("/api/auth") {
            // 登录
            post("/login") {
                val request = call.receive<AuthRequest>()
                val result = authService.login(request.username, request.password)
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(
                        AuthResponse(
                            userId = result.user.id.toString(),
                            username = result.user.username,
                            accessToken = result.accessToken,
                            refreshToken = result.refreshToken,
                            expiresIn = result.expiresIn
                        )
                    )
                )
            }
            
            // 注册
            post("/register") {
                val request = call.receive<RegisterRequest>()
                val result = authService.register(
                    username = request.username,
                    email = request.email,
                    password = request.password,
                    firstName = request.firstName,
                    lastName = request.lastName
                )
                
                call.respond(
                    HttpStatusCode.Created,
                    ResponseDto.success(
                        AuthResponse(
                            userId = result.user.id.toString(),
                            username = result.user.username,
                            accessToken = result.accessToken,
                            refreshToken = result.refreshToken,
                            expiresIn = result.expiresIn
                        )
                    )
                )
            }
            
            // 刷新令牌
            post("/refresh-token") {
                val request = call.receive<RefreshTokenRequest>()
                val result = authService.refreshToken(request.refreshToken)
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(
                        AuthResponse(
                            userId = result.user.id.toString(),
                            username = result.user.username,
                            accessToken = result.accessToken,
                            refreshToken = result.refreshToken,
                            expiresIn = result.expiresIn
                        )
                    )
                )
            }
            
            // 修改密码
            post("/change-password") {
                val request = call.receive<ChangePasswordRequest>()
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                    ?: throw IllegalArgumentException("缺少认证令牌")
                
                val user = authService.getUserFromToken(token)
                    ?: throw IllegalArgumentException("无效的认证令牌")
                
                val success = authService.changePassword(
                    userId = user.id.toString(),
                    oldPassword = request.oldPassword,
                    newPassword = request.newPassword
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(mapOf("success" to success))
                )
            }
            
            // 忘记密码
            post("/forgot-password") {
                val email = call.receive<Map<String, String>>()["email"]
                    ?: throw IllegalArgumentException("缺少邮箱参数")
                
                val success = authService.forgotPassword(email)
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(mapOf("success" to success))
                )
            }
            
            // 重置密码
            post("/reset-password") {
                val request = call.receive<ResetPasswordRequest>()
                val success = authService.resetPassword(
                    resetToken = request.resetToken,
                    newPassword = request.newPassword
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(mapOf("success" to success))
                )
            }
            
            // 退出登录
            post("/logout") {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                    ?: throw IllegalArgumentException("缺少认证令牌")
                
                val success = authService.logout(token)
                
                call.respond(
                    HttpStatusCode.OK,
                    ResponseDto.success(mapOf("success" to success))
                )
            }
        }
    }
}
