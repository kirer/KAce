package com.kace.auth.domain.model

import java.time.Instant
import java.util.*

/**
 * 用户领域模型
 */
data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val email: String,
    val passwordHash: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val active: Boolean = true,
    val verified: Boolean = false,
    val roles: Set<Role> = emptySet(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val lastLoginAt: Instant? = null
) {
    /**
     * 获取用户全名
     */
    fun fullName(): String {
        return when {
            firstName != null && lastName != null -> "$firstName $lastName"
            firstName != null -> firstName
            lastName != null -> lastName
            else -> username
        }
    }
    
    /**
     * 检查用户是否有指定角色
     */
    fun hasRole(roleName: String): Boolean {
        return roles.any { it.name == roleName }
    }
    
    /**
     * 检查用户是否有指定权限
     */
    fun hasPermission(permissionName: String): Boolean {
        return roles.any { role -> role.permissions.any { it.name == permissionName } }
    }
    
    /**
     * 获取用户所有权限
     */
    fun getAllPermissions(): Set<Permission> {
        return roles.flatMap { it.permissions }.toSet()
    }
} 