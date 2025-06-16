package com.kace.user.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 角色领域模型
 */
@Serializable
data class Role(
    val id: String,
    val name: String,
    val description: String? = null,
    val isSystem: Boolean = false,  // 系统内置角色不可删除
    val permissions: List<String> = emptyList(),
    val organizationId: String? = null,  // null表示全局角色
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 角色创建请求
 */
data class RoleCreateRequest(
    val name: String,
    val description: String? = null,
    val permissions: List<String> = listOf(),
    val organizationId: String? = null
)

/**
 * 角色更新请求
 */
data class RoleUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val permissions: List<String>? = null
)

/**
 * 用户角色关联
 */
data class UserRole(
    val userId: String,
    val roleId: String,
    val assignedAt: Instant,
    val assignedBy: String
) 