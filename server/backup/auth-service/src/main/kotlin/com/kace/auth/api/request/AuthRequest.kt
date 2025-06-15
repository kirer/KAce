package com.kace.auth.api.request

import kotlinx.serialization.Serializable

/**
 * 登录请求
 */
@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)

/**
 * 注册请求
 */
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

/**
 * 刷新令牌请求
 */
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * 修改密码请求
 */
@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

/**
 * 忘记密码请求
 */
@Serializable
data class ForgotPasswordRequest(
    val email: String
)

/**
 * 重置密码请求
 */
@Serializable
data class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String
) 