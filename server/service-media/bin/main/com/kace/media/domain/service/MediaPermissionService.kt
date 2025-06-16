package com.kace.media.domain.service

import com.kace.media.domain.model.MediaPermission
import com.kace.media.domain.model.MediaPermissionType
import com.kace.media.domain.model.PermissionGranteeType
import java.util.UUID

/**
 * 媒体权限服务接口
 */
interface MediaPermissionService {
    /**
     * 创建媒体权限
     */
    suspend fun createPermission(permission: MediaPermission): MediaPermission
    
    /**
     * 更新媒体权限
     */
    suspend fun updatePermission(permission: MediaPermission): MediaPermission
    
    /**
     * 获取媒体权限列表
     */
    suspend fun getMediaPermissions(mediaId: UUID): List<MediaPermission>
    
    /**
     * 获取文件夹权限列表
     */
    suspend fun getFolderPermissions(folderId: UUID): List<MediaPermission>
    
    /**
     * 删除权限
     */
    suspend fun deletePermission(id: UUID): Boolean
    
    /**
     * 检查用户是否有指定媒体的指定权限
     */
    suspend fun userHasMediaPermission(mediaId: UUID, userId: UUID, permissionType: MediaPermissionType): Boolean
    
    /**
     * 检查用户是否有指定文件夹的指定权限
     */
    suspend fun userHasFolderPermission(folderId: UUID, userId: UUID, permissionType: MediaPermissionType): Boolean
    
    /**
     * 授予用户媒体权限
     */
    suspend fun grantUserMediaPermission(mediaId: UUID, userId: UUID, permissionType: MediaPermissionType, expiresAt: Long? = null): MediaPermission
    
    /**
     * 授予用户文件夹权限
     */
    suspend fun grantUserFolderPermission(folderId: UUID, userId: UUID, permissionType: MediaPermissionType, expiresAt: Long? = null, applyToChildren: Boolean = false): MediaPermission
    
    /**
     * 授予角色媒体权限
     */
    suspend fun grantRoleMediaPermission(mediaId: UUID, roleId: UUID, permissionType: MediaPermissionType): MediaPermission
    
    /**
     * 授予角色文件夹权限
     */
    suspend fun grantRoleFolderPermission(folderId: UUID, roleId: UUID, permissionType: MediaPermissionType, applyToChildren: Boolean = false): MediaPermission
    
    /**
     * 授予组织媒体权限
     */
    suspend fun grantOrganizationMediaPermission(mediaId: UUID, organizationId: UUID, permissionType: MediaPermissionType): MediaPermission
    
    /**
     * 授予组织文件夹权限
     */
    suspend fun grantOrganizationFolderPermission(folderId: UUID, organizationId: UUID, permissionType: MediaPermissionType, applyToChildren: Boolean = false): MediaPermission
    
    /**
     * 设置媒体为公开访问
     */
    suspend fun makeMediaPublic(mediaId: UUID, permissionTypes: Set<MediaPermissionType> = setOf(MediaPermissionType.VIEW)): List<MediaPermission>
    
    /**
     * 设置文件夹为公开访问
     */
    suspend fun makeFolderPublic(folderId: UUID, permissionTypes: Set<MediaPermissionType> = setOf(MediaPermissionType.VIEW), applyToChildren: Boolean = false): List<MediaPermission>
    
    /**
     * 撤销媒体的公开访问
     */
    suspend fun makeMediaPrivate(mediaId: UUID): Boolean
    
    /**
     * 撤销文件夹的公开访问
     */
    suspend fun makeFolderPrivate(folderId: UUID, applyToChildren: Boolean = false): Boolean
    
    /**
     * 应用父文件夹权限到子文件夹和媒体
     */
    suspend fun inheritPermissions(folderId: UUID, recursive: Boolean = false): Int
} 