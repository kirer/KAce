package com.kace.user.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 组织领域模型
 */
@Serializable
data class Organization(
    val id: String,
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val status: OrganizationStatus = OrganizationStatus.ACTIVE,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 组织状态
 */
@Serializable
enum class OrganizationStatus {
    ACTIVE,      // 活跃
    INACTIVE,    // 未激活
    SUSPENDED,   // 已暂停
    DELETED      // 已删除
}

/**
 * 组织成员领域模型
 */
@Serializable
data class OrganizationMember(
    val id: String,
    val organizationId: String,
    val userId: String,
    val role: OrganizationRole,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 组织角色
 */
@Serializable
enum class OrganizationRole {
    OWNER,       // 所有者
    ADMIN,       // 管理员
    MEMBER,      // 成员
    GUEST        // 访客
} 