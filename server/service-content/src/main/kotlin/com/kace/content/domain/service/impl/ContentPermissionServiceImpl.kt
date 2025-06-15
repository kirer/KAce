package com.kace.content.domain.service.impl

import com.kace.content.domain.model.ContentPermission
import com.kace.content.domain.model.ContentPermissionSubjectType
import com.kace.content.domain.model.ContentPermissionType
import com.kace.content.domain.repository.ContentPermissionRepository
import com.kace.content.domain.service.ContentPermissionService
import java.util.UUID

/**
 * 内容权限服务实现
 */
class ContentPermissionServiceImpl(
    private val contentPermissionRepository: ContentPermissionRepository
) : ContentPermissionService {
    /**
     * 授予内容权限
     */
    override suspend fun grantPermission(
        contentId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?,
        grantedBy: UUID
    ): ContentPermission {
        val permission = ContentPermission.create(
            contentId = contentId,
            permissionType = permissionType,
            subjectType = subjectType,
            subjectId = subjectId,
            createdBy = grantedBy
        )
        
        return contentPermissionRepository.create(permission)
    }

    /**
     * 授予内容类型权限
     */
    override suspend fun grantContentTypePermission(
        contentTypeId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?,
        grantedBy: UUID
    ): ContentPermission {
        val permission = ContentPermission.create(
            contentId = UUID.randomUUID(), // 临时ID，不会被使用
            contentTypeId = contentTypeId,
            permissionType = permissionType,
            subjectType = subjectType,
            subjectId = subjectId,
            createdBy = grantedBy
        )
        
        return contentPermissionRepository.create(permission)
    }

    /**
     * 撤销内容权限
     */
    override suspend fun revokePermission(
        contentId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?
    ): Boolean {
        val permissions = contentPermissionRepository.getByContentAndPermissionType(contentId, permissionType)
            .filter { it.subjectType == subjectType && it.subjectId == subjectId }
        
        if (permissions.isEmpty()) {
            return false
        }
        
        var success = true
        for (permission in permissions) {
            val result = contentPermissionRepository.delete(permission.id)
            if (!result) {
                success = false
            }
        }
        
        return success
    }

    /**
     * 撤销内容类型权限
     */
    override suspend fun revokeContentTypePermission(
        contentTypeId: UUID,
        permissionType: ContentPermissionType,
        subjectType: ContentPermissionSubjectType,
        subjectId: UUID?
    ): Boolean {
        val permissions = contentPermissionRepository.getByContentTypeAndPermissionType(contentTypeId, permissionType)
            .filter { it.subjectType == subjectType && it.subjectId == subjectId }
        
        if (permissions.isEmpty()) {
            return false
        }
        
        var success = true
        for (permission in permissions) {
            val result = contentPermissionRepository.delete(permission.id)
            if (!result) {
                success = false
            }
        }
        
        return success
    }

    /**
     * 检查用户是否有内容的特定权限
     */
    override suspend fun checkUserPermission(
        userId: UUID,
        contentId: UUID,
        permissionType: ContentPermissionType
    ): Boolean {
        // 检查用户特定权限
        val hasUserPermission = contentPermissionRepository.hasPermission(
            contentId = contentId,
            permissionType = permissionType,
            subjectType = ContentPermissionSubjectType.USER,
            subjectId = userId
        )
        
        if (hasUserPermission) {
            return true
        }
        
        // 检查公共权限
        return contentPermissionRepository.hasPermission(
            contentId = contentId,
            permissionType = permissionType,
            subjectType = ContentPermissionSubjectType.PUBLIC
        )
        
        // 注意：这里可以扩展检查用户所属的角色和团队权限
    }

    /**
     * 检查用户是否有内容类型的特定权限
     */
    override suspend fun checkUserContentTypePermission(
        userId: UUID,
        contentTypeId: UUID,
        permissionType: ContentPermissionType
    ): Boolean {
        // 检查用户特定权限
        val hasUserPermission = contentPermissionRepository.hasContentTypePermission(
            contentTypeId = contentTypeId,
            permissionType = permissionType,
            subjectType = ContentPermissionSubjectType.USER,
            subjectId = userId
        )
        
        if (hasUserPermission) {
            return true
        }
        
        // 检查公共权限
        return contentPermissionRepository.hasContentTypePermission(
            contentTypeId = contentTypeId,
            permissionType = permissionType,
            subjectType = ContentPermissionSubjectType.PUBLIC
        )
        
        // 注意：这里可以扩展检查用户所属的角色和团队权限
    }

    /**
     * 获取内容的所有权限
     */
    override suspend fun getContentPermissions(contentId: UUID): List<ContentPermission> {
        return contentPermissionRepository.getByContent(contentId)
    }

    /**
     * 获取内容类型的所有权限
     */
    override suspend fun getContentTypePermissions(contentTypeId: UUID): List<ContentPermission> {
        return contentPermissionRepository.getByContentType(contentTypeId)
    }

    /**
     * 获取用户的所有内容权限
     */
    override suspend fun getUserContentPermissions(userId: UUID): List<ContentPermission> {
        // 获取用户特定权限
        val userPermissions = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.USER,
            subjectId = userId
        ).filter { it.contentId != null }
        
        // 获取公共权限
        val publicPermissions = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.PUBLIC
        ).filter { it.contentId != null }
        
        // 合并权限
        return userPermissions + publicPermissions
        
        // 注意：这里可以扩展获取用户所属的角色和团队权限
    }

    /**
     * 获取用户的所有内容类型权限
     */
    override suspend fun getUserContentTypePermissions(userId: UUID): List<ContentPermission> {
        // 获取用户特定权限
        val userPermissions = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.USER,
            subjectId = userId
        ).filter { it.contentTypeId != null }
        
        // 获取公共权限
        val publicPermissions = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.PUBLIC
        ).filter { it.contentTypeId != null }
        
        // 合并权限
        return userPermissions + publicPermissions
        
        // 注意：这里可以扩展获取用户所属的角色和团队权限
    }

    /**
     * 获取用户可访问的内容ID列表
     */
    override suspend fun getUserAccessibleContentIds(
        userId: UUID,
        permissionType: ContentPermissionType
    ): List<UUID> {
        // 获取用户特定权限的内容ID
        val userPermissionContentIds = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.USER,
            subjectId = userId
        )
            .filter { it.permissionType == permissionType && it.contentId != null }
            .mapNotNull { it.contentId }
        
        // 获取公共权限的内容ID
        val publicPermissionContentIds = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.PUBLIC
        )
            .filter { it.permissionType == permissionType && it.contentId != null }
            .mapNotNull { it.contentId }
        
        // 合并并去重
        return (userPermissionContentIds + publicPermissionContentIds).distinct()
        
        // 注意：这里可以扩展获取用户所属的角色和团队权限
    }

    /**
     * 获取用户可访问的内容类型ID列表
     */
    override suspend fun getUserAccessibleContentTypeIds(
        userId: UUID,
        permissionType: ContentPermissionType
    ): List<UUID> {
        // 获取用户特定权限的内容类型ID
        val userPermissionContentTypeIds = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.USER,
            subjectId = userId
        )
            .filter { it.permissionType == permissionType && it.contentTypeId != null }
            .mapNotNull { it.contentTypeId }
        
        // 获取公共权限的内容类型ID
        val publicPermissionContentTypeIds = contentPermissionRepository.getBySubject(
            subjectType = ContentPermissionSubjectType.PUBLIC
        )
            .filter { it.permissionType == permissionType && it.contentTypeId != null }
            .mapNotNull { it.contentTypeId }
        
        // 合并并去重
        return (userPermissionContentTypeIds + publicPermissionContentTypeIds).distinct()
        
        // 注意：这里可以扩展获取用户所属的角色和团队权限
    }

    /**
     * 设置内容的默认权限
     */
    override suspend fun setDefaultContentPermissions(
        contentId: UUID,
        isPublic: Boolean,
        createdBy: UUID
    ): List<ContentPermission> {
        val permissions = mutableListOf<ContentPermission>()
        
        // 如果是公开内容，添加公共查看权限
        if (isPublic) {
            val viewPermission = ContentPermission.create(
                contentId = contentId,
                permissionType = ContentPermissionType.VIEW,
                subjectType = ContentPermissionSubjectType.PUBLIC,
                createdBy = createdBy
            )
            permissions.add(contentPermissionRepository.create(viewPermission))
        }
        
        // 为创建者添加所有权限
        val permissionTypes = ContentPermissionType.values()
        for (permissionType in permissionTypes) {
            val permission = ContentPermission.create(
                contentId = contentId,
                permissionType = permissionType,
                subjectType = ContentPermissionSubjectType.USER,
                subjectId = createdBy,
                createdBy = createdBy
            )
            permissions.add(contentPermissionRepository.create(permission))
        }
        
        return permissions
    }
} 