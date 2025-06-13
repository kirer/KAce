package com.kace.user.domain.model

import java.time.Instant

/**
 * 权限领域模型
 */
data class Permission(
    val id: String,
    val name: String,
    val code: String,  // 权限唯一标识符，如"user:read"
    val description: String? = null,
    val category: String,  // 权限分类，如"用户管理"
    val isSystem: Boolean = true,  // 系统权限不可删除
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 权限创建请求
 */
data class PermissionCreateRequest(
    val name: String,
    val code: String,
    val description: String? = null,
    val category: String
)

/**
 * 权限更新请求
 */
data class PermissionUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val category: String? = null
)

/**
 * 权限类别
 */
enum class PermissionCategory {
    USER_MANAGEMENT,     // 用户管理
    ROLE_MANAGEMENT,     // 角色管理
    CONTENT_MANAGEMENT,  // 内容管理
    MEDIA_MANAGEMENT,    // 媒体管理
    SYSTEM_MANAGEMENT,   // 系统管理
    ANALYTICS,           // 分析统计
    NOTIFICATION,        // 通知管理
    PLUGIN_MANAGEMENT    // 插件管理
}

/**
 * 预定义系统权限
 */
object SystemPermissions {
    // 用户管理权限
    const val USER_CREATE = "user:create"
    const val USER_READ = "user:read"
    const val USER_UPDATE = "user:update"
    const val USER_DELETE = "user:delete"
    
    // 角色管理权限
    const val ROLE_CREATE = "role:create"
    const val ROLE_READ = "role:read"
    const val ROLE_UPDATE = "role:update"
    const val ROLE_DELETE = "role:delete"
    const val ROLE_ASSIGN = "role:assign"
    
    // 权限管理权限
    const val PERMISSION_CREATE = "permission:create"
    const val PERMISSION_READ = "permission:read"
    const val PERMISSION_UPDATE = "permission:update"
    const val PERMISSION_DELETE = "permission:delete"
    
    // 组织管理权限
    const val ORGANIZATION_CREATE = "organization:create"
    const val ORGANIZATION_READ = "organization:read"
    const val ORGANIZATION_UPDATE = "organization:update"
    const val ORGANIZATION_DELETE = "organization:delete"
    
    // 系统管理权限
    const val SYSTEM_CONFIG = "system:config"
    const val SYSTEM_BACKUP = "system:backup"
    const val SYSTEM_RESTORE = "system:restore"
    const val SYSTEM_LOG = "system:log"
} 