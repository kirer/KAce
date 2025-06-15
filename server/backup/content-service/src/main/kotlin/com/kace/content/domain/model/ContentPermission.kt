package com.kace.content.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 内容权限类型
 */
enum class ContentPermissionType {
    /**
     * 查看权限
     */
    VIEW,
    
    /**
     * 编辑权限
     */
    EDIT,
    
    /**
     * 删除权限
     */
    DELETE,
    
    /**
     * 发布权限
     */
    PUBLISH,
    
    /**
     * 管理权限
     */
    MANAGE
}

/**
 * 内容权限主体类型
 */
enum class ContentPermissionSubjectType {
    /**
     * 用户
     */
    USER,
    
    /**
     * 角色
     */
    ROLE,
    
    /**
     * 团队
     */
    TEAM,
    
    /**
     * 所有人
     */
    PUBLIC
}

/**
 * 内容权限
 */
data class ContentPermission(
    /**
     * 权限ID
     */
    val id: UUID = UUID.randomUUID(),
    
    /**
     * 内容ID
     */
    val contentId: UUID,
    
    /**
     * 内容类型ID
     */
    val contentTypeId: UUID? = null,
    
    /**
     * 权限类型
     */
    val permissionType: ContentPermissionType,
    
    /**
     * 主体类型
     */
    val subjectType: ContentPermissionSubjectType,
    
    /**
     * 主体ID
     */
    val subjectId: UUID? = null,
    
    /**
     * 创建者ID
     */
    val createdBy: UUID,
    
    /**
     * 创建时间
     */
    val createdAt: Long = Instant.now().toEpochMilli(),
    
    /**
     * 更新时间
     */
    val updatedAt: Long = createdAt
) {
    companion object {
        /**
         * 创建内容权限
         */
        fun create(
            contentId: UUID,
            contentTypeId: UUID? = null,
            permissionType: ContentPermissionType,
            subjectType: ContentPermissionSubjectType,
            subjectId: UUID? = null,
            createdBy: UUID
        ): ContentPermission {
            // 验证主体类型和ID的一致性
            if (subjectType != ContentPermissionSubjectType.PUBLIC && subjectId == null) {
                throw IllegalArgumentException("非公共权限必须指定主体ID")
            }
            
            if (subjectType == ContentPermissionSubjectType.PUBLIC && subjectId != null) {
                throw IllegalArgumentException("公共权限不应指定主体ID")
            }
            
            return ContentPermission(
                contentId = contentId,
                contentTypeId = contentTypeId,
                permissionType = permissionType,
                subjectType = subjectType,
                subjectId = subjectId,
                createdBy = createdBy
            )
        }
    }
} 