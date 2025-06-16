package com.kace.user.api.request

import kotlinx.serialization.Serializable

/**
 * 登录请求
 */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false
)

/**
 * 令牌刷新请求
 */
@Serializable
data class TokenRefreshRequest(
    val token: String
)

/**
 * 修改密码请求
 */
@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

/**
 * 重置密码请求
 */
@Serializable
data class ResetPasswordRequest(
    val email: String
)

/**
 * 完成重置密码请求
 */
@Serializable
data class CompleteResetPasswordRequest(
    val token: String,
    val newPassword: String,
    val confirmPassword: String
) 