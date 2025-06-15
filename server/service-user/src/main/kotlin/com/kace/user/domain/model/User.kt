package com.kace.user.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 用户领域模型
 */
@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String? = null,
    val status: UserStatus = UserStatus.ACTIVE,
    val roles: List<String> = listOf(),
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 用户状态
 */
@Serializable
enum class UserStatus {
    ACTIVE,      // 活跃
    INACTIVE,    // 未激活
    SUSPENDED,   // 已暂停
    DELETED      // 已删除
} 