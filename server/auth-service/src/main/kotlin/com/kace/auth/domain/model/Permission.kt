package com.kace.auth.domain.model

import java.time.Instant
import java.util.*

/**
 * 权限领域模型
 */
data class Permission(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val resource: String,
    val action: String,
    val isSystem: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    companion object {
        // 用户资源权限
        const val USER_CREATE = "user:create"
        const val USER_READ = "user:read"
        const val USER_UPDATE = "user:update"
        const val USER_DELETE = "user:delete"
        
        // 角色资源权限
        const val ROLE_CREATE = "role:create"
        const val ROLE_READ = "role:read"
        const val ROLE_UPDATE = "role:update"
        const val ROLE_DELETE = "role:delete"
        
        // 权限资源权限
        const val PERMISSION_CREATE = "permission:create"
        const val PERMISSION_READ = "permission:read"
        const val PERMISSION_UPDATE = "permission:update"
        const val PERMISSION_DELETE = "permission:delete"
        
        // 系统管理权限
        const val SYSTEM_ADMIN = "system:admin"
    }
    
    /**
     * 检查权限是否匹配指定的资源和操作
     */
    fun matches(resource: String, action: String): Boolean {
        return this.resource == resource && this.action == action
    }
    
    /**
     * 生成权限名称
     */
    fun generateName(): String {
        return "$resource:$action"
    }
} 