package com.kace.auth.domain.model

import java.time.Instant
import java.util.*

/**
 * 角色领域模型
 */
data class Role(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val permissions: Set<Permission> = emptySet(),
    val isSystem: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    companion object {
        const val ADMIN_ROLE = "ADMIN"
        const val USER_ROLE = "USER"
        const val GUEST_ROLE = "GUEST"
    }
    
    /**
     * 检查角色是否有指定权限
     */
    fun hasPermission(permissionName: String): Boolean {
        return permissions.any { it.name == permissionName }
    }
    
    /**
     * 检查是否是管理员角色
     */
    fun isAdmin(): Boolean {
        return name == ADMIN_ROLE
    }
} 