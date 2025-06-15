package com.kace.user.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * 用户资料领域模型
 */
@Serializable
data class UserProfile(
    val id: String,
    val userId: String,
    val bio: String? = null,
    val phoneNumber: String? = null,
    val birthDate: LocalDate? = null,
    val gender: Gender? = null,
    val address: Address? = null,
    val preferences: UserPreferences? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 性别
 */
@Serializable
enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY
}

/**
 * 地址
 */
@Serializable
data class Address(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
)

/**
 * 用户偏好设置
 */
@Serializable
data class UserPreferences(
    val language: String = "zh-CN",
    val theme: String = "light",
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val twoFactorAuth: Boolean = false
) 