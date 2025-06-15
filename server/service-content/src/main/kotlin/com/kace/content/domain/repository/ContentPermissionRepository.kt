package com.kace.content.domain.repository

import com.kace.content.domain.model.ContentPermission
import com.kace.content.domain.model.ContentPermissionType
import com.kace.content.domain.model.ContentPermissionSubjectType
import java.util.UUID

/**
 * 内容权限仓库接口
 */
interface ContentPermissionRepository {
    /**
     * 创建权限
     */
    suspend fun create(permission: ContentPermission): ContentPermission
    
    /**
     * 删除权限
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 获取权限
     */
    suspend fun getById(id: UUID): ContentPermission?
    
    /**
     * 获取内容的所有权限
     */
    suspend fun getByContent(contentId: UUID): List<ContentPermission>
    
    /**
     * 获取内容类型的所有权限
     */
    suspend fun getByContentType(contentTypeId: UUID): List<ContentPermission>
    
    /**
     * 获取主体的所有权限
     */
    suspend fun getBySubject(
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null
    ): List<ContentPermission>
    
    /**
     * 获取内容的特定权限
     */
    suspend fun getByContentAndPermissionType(
        contentId: UUID,
        permissionType: ContentPermissionType
    ): List<ContentPermission>
    
    /**
     * 获取内容类型的特定权限
     */
    suspend fun getByContentTypeAndPermissionType(
        contentTypeId: UUID,
        permissionType: ContentPermissionType
    ): List<ContentPermission>
    
    /**
     * 检查主体是否有内容的特定权限
     */
    suspend fun hasPermission(
        contentId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null
    ): Boolean
    
    /**
     * 检查主体是否有内容类型的特定权限
     */
    suspend fun hasContentTypePermission(
        contentTypeId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null
    ): Boolean
    
    /**
     * 删除内容的所有权限
     */
    suspend fun deleteByContent(contentId: UUID): Boolean
    
    /**
     * 删除内容类型的所有权限
     */
    suspend fun deleteByContentType(contentTypeId: UUID): Boolean
    
    /**
     * 删除主体的所有权限
     */
    suspend fun deleteBySubject(
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID? = null
    ): Boolean
} 