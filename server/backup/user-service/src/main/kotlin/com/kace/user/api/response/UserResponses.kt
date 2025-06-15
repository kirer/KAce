package com.kace.user.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 用户响应
 */
@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String? = null,
    val status: String,
    val roles: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 用户资料响应
 */
@Serializable
data class UserProfileResponse(
    val id: String,
    val userId: String,
    val bio: String? = null,
    val phoneNumber: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val address: AddressResponse? = null,
    val preferences: UserPreferencesResponse? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 地址响应
 */
@Serializable
data class AddressResponse(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
)

/**
 * 用户偏好设置响应
 */
@Serializable
data class UserPreferencesResponse(
    val language: String,
    val theme: String,
    val emailNotifications: Boolean,
    val pushNotifications: Boolean,
    val twoFactorAuth: Boolean
)

/**
 * 角色响应
 */
@Serializable
data class RoleResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val isSystem: Boolean,
    val permissions: List<PermissionResponse>? = null,
    val createdAt: Instant,
    val updatedAt: Instant
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
    val isSystem: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 组织响应
 */
@Serializable
data class OrganizationResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) 