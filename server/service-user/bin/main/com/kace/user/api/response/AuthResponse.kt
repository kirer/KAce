package com.kace.user.api.response

import com.kace.user.domain.model.User
import kotlinx.serialization.Serializable

/**
 * 认证令牌响应
 */
@Serializable
data class TokenResponse(
    val token: String,
    val expiresIn: Long
)

/**
 * 认证用户响应
 */
@Serializable
data class AuthUserResponse(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?,
    val roles: List<String>
) {
    companion object {
        fun fromDomain(user: User): AuthUserResponse {
            return AuthUserResponse(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                avatarUrl = user.avatarUrl,
                roles = user.roles
            )
        }
    }
}

/**
 * 登录响应
 */
@Serializable
data class LoginResponse(
    val token: TokenResponse,
    val user: AuthUserResponse
) 