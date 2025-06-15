package com.kace.user.api.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * 创建用户请求
 */
@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val avatarUrl: String? = null
)

/**
 * 更新用户请求
 */
@Serializable
data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null
)

/**
 * 更新用户状态请求
 */
@Serializable
data class UpdateUserStatusRequest(
    val status: String
)

/**
 * 更新用户资料请求
 */
@Serializable
data class UpdateUserProfileRequest(
    val bio: String? = null,
    val phoneNumber: String? = null,
    val birthDate: LocalDate? = null,
    val gender: String? = null,
    val address: AddressRequest? = null,
    val preferences: UserPreferencesRequest? = null
)

/**
 * 地址请求
 */
@Serializable
data class AddressRequest(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
)

/**
 * 用户偏好设置请求
 */
@Serializable
data class UserPreferencesRequest(
    val language: String? = null,
    val theme: String? = null,
    val emailNotifications: Boolean? = null,
    val pushNotifications: Boolean? = null,
    val twoFactorAuth: Boolean? = null
)

/**
 * 更改密码请求
 */
@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
) 