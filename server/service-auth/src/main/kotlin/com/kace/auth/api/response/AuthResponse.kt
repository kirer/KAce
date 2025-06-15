package com.kace.auth.api.response

import kotlinx.serialization.Serializable
import java.util.*

/**
 * 认证响应
 */
@Serializable
data class AuthResponse(
    val userId: String,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

/**
 * 用户响应
 */
@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val active: Boolean,
    val verified: Boolean,
    val roles: List<RoleResponse> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val lastLoginAt: String? = null
)

/**
 * 角色响应
 */
@Serializable
data class RoleResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val permissions: List<PermissionResponse> = emptyList(),
    val isSystem: Boolean
)

/**
 * 权限响应
 */
@Serializable
data class PermissionResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val resource: String,
    val action: String,
    val isSystem: Boolean
) 