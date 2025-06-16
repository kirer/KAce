package com.kace.user.api.controller

import com.kace.common.model.dto.ApiResponse
import com.kace.user.api.request.ChangePasswordRequest
import com.kace.user.api.request.CompleteResetPasswordRequest
import com.kace.user.api.request.LoginRequest
import com.kace.user.api.request.ResetPasswordRequest
import com.kace.user.api.request.TokenRefreshRequest
import com.kace.user.api.response.AuthUserResponse
import com.kace.user.api.response.LoginResponse
import com.kace.user.api.response.TokenResponse
import com.kace.user.domain.service.AuthenticationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.get
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 认证控制器路由
 */
fun Route.authRoutes(authService: AuthenticationService) {
    val logger = LoggerFactory.getLogger("AuthController")
    
    route("/api/v1/auth") {
        /**
         * 用户登录
         */
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                
                // 验证用户名和密码
                val token = authService.login(request.username, request.password)
                
                if (token != null) {
                    val user = authService.getUserFromToken(token)
                    
                    if (user != null) {
                        // 计算令牌有效期（默认24小时）
                        val expiresIn = if (request.rememberMe) 604800L else 86400L
                        
                        val response = LoginResponse(
                            token = TokenResponse(token, expiresIn),
                            user = AuthUserResponse.fromDomain(user)
                        )
                        
                        call.respond(ApiResponse.success(response))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to retrieve user information"))
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Invalid username or password"))
                }
            } catch (e: Exception) {
                logger.error("Login error", e)
                call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Login failed: ${e.message}"))
            }
        }
        
        /**
         * 刷新令牌
         */
        post("/refresh") {
            try {
                val request = call.receive<TokenRefreshRequest>()
                val newToken = authService.refreshToken(request.token)
                
                if (newToken != null) {
                    call.respond(ApiResponse.success(TokenResponse(newToken, 86400L)))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Invalid or expired token"))
                }
            } catch (e: Exception) {
                logger.error("Token refresh error", e)
                call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Token refresh failed: ${e.message}"))
            }
        }
        
        /**
         * 请求重置密码
         */
        post("/password/reset/request") {
            try {
                val request = call.receive<ResetPasswordRequest>()
                val success = authService.resetPassword(request.email)
                
                if (success) {
                    call.respond(ApiResponse.success("Password reset email sent"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiResponse.error("Email not found or reset failed"))
                }
            } catch (e: Exception) {
                logger.error("Password reset request error", e)
                call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Password reset request failed: ${e.message}"))
            }
        }
        
        /**
         * 验证重置密码令牌
         */
        get("/password/reset/validate") {
            try {
                val token = call.request.queryParameters["token"] ?: ""
                val valid = authService.validateResetPasswordToken(token)
                
                if (valid) {
                    call.respond(ApiResponse.success("Token is valid"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Invalid or expired token"))
                }
            } catch (e: Exception) {
                logger.error("Token validation error", e)
                call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Token validation failed: ${e.message}"))
            }
        }
        
        /**
         * 完成重置密码
         */
        post("/password/reset/complete") {
            try {
                val request = call.receive<CompleteResetPasswordRequest>()
                
                if (request.newPassword != request.confirmPassword) {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Passwords do not match"))
                    return@post
                }
                
                val success = authService.completePasswordReset(request.token, request.newPassword)
                
                if (success) {
                    call.respond(ApiResponse.success("Password reset successfully"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Password reset failed"))
                }
            } catch (e: Exception) {
                logger.error("Password reset completion error", e)
                call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Password reset failed: ${e.message}"))
            }
        }
        
        // 需要认证的路由
        authenticate("jwt") {
            /**
             * 获取当前认证用户
             */
            get("/me") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId != null) {
                        val user = authService.getUserFromToken(userId)
                        
                        if (user != null) {
                            call.respond(ApiResponse.success(AuthUserResponse.fromDomain(user)))
                        } else {
                            call.respond(HttpStatusCode.NotFound, ApiResponse.error("User not found"))
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                    }
                } catch (e: Exception) {
                    logger.error("Get current user error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Failed to retrieve user: ${e.message}"))
                }
            }
            
            /**
             * 修改密码
             */
            post("/password/change") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.subject
                    
                    if (userId != null) {
                        val request = call.receive<ChangePasswordRequest>()
                        
                        if (request.newPassword != request.confirmPassword) {
                            call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Passwords do not match"))
                            return@post
                        }
                        
                        val success = authService.changePassword(
                            UUID.fromString(userId),
                            request.oldPassword,
                            request.newPassword
                        )
                        
                        if (success) {
                            call.respond(ApiResponse.success("Password changed successfully"))
                        } else {
                            call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Incorrect old password or password change failed"))
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse.error("Unauthorized"))
                    }
                } catch (e: Exception) {
                    logger.error("Password change error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Password change failed: ${e.message}"))
                }
            }
            
            /**
             * 用户登出
             */
            post("/logout") {
                try {
                    val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    
                    if (token != null) {
                        authService.logout(token)
                        call.respond(ApiResponse.success("Logged out successfully"))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse.error("Token not provided"))
                    }
                } catch (e: Exception) {
                    logger.error("Logout error", e)
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse.error("Logout failed: ${e.message}"))
                }
            }
        }
    }
} 