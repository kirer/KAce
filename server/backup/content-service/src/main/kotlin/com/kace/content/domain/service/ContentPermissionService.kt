package com.kace.content.domain.service

import com.kace.content.domain.model.ContentPermission
import com.kace.content.domain.model.ContentPermissionType
import com.kace.content.domain.model.ContentPermissionSubjectType
import java.util.UUID

/**
 * 内容权限服务接口
 */
interface ContentPermissionService {
    /**
     * 授予内容权限
     */
    suspend fun grantPermission(
        contentId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null,
        grantedBy: UUID
    ): ContentPermission
    
    /**
     * 授予内容类型权限
     */
    suspend fun grantContentTypePermission(
        contentTypeId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null,
        grantedBy: UUID
    ): ContentPermission
    
    /**
     * 撤销内容权限
     */
    suspend fun revokePermission(
        contentId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null
    ): Boolean
    
    /**
     * 撤销内容类型权限
     */
    suspend fun revokeContentTypePermission(
        contentTypeId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null
    ): Boolean
    
    /**
     * 检查用户是否有内容的特定权限
     */
    suspend fun checkUserPermission(
        userId: UUID,
        contentId: UUID,
        permissionType: ContentPermissionType
    ): Boolean
    
    /**
     * 检查用户是否有内容类型的特定权限
     */
    suspend fun checkUserContentTypePermission(
        userId: UUID,
        contentTypeId: UUID,
        permissionType: ContentPermissionType
    ): Boolean
    
    /**
     * 获取内容的所有权限
     */
    suspend fun getContentPermissions(contentId: UUID): List<ContentPermission>
    
    /**
     * 获取内容类型的所有权限
     */
    suspend fun getContentTypePermissions(contentTypeId: UUID): List<ContentPermission>
    
    /**
     * 获取用户的所有内容权限
     */
    suspend fun getUserContentPermissions(userId: UUID): List<ContentPermission>
    
    /**
     * 获取用户的所有内容类型权限
     */
    suspend fun getUserContentTypePermissions(userId: UUID): List<ContentPermission>
    
    /**
     * 获取用户可访问的内容ID列表
     */
    suspend fun getUserAccessibleContentIds(
        userId: UUID,
        permissionType: ContentPermissionType
    ): List<UUID>
    
    /**
     * 获取用户可访问的内容类型ID列表
     */
    suspend fun getUserAccessibleContentTypeIds(
        userId: UUID,
        permissionType: ContentPermissionType
    ): List<UUID>
    
    /**
     * 设置内容的默认权限
     */
    suspend fun setDefaultContentPermissions(
        contentId: UUID,
        isPublic: Boolean,
        createdBy: UUID
    ): List<ContentPermission>
} 