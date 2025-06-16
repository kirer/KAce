package com.kace.media.domain.repository

import com.kace.media.domain.model.MediaPermission
import com.kace.media.domain.model.MediaPermissionType
import com.kace.media.domain.model.PermissionGranteeType
import java.util.UUID

/**
 * 媒体权限仓库接口
 */
interface MediaPermissionRepository {
    /**
     * 创建媒体权限
     */
    suspend fun create(permission: MediaPermission): MediaPermission
    
    /**
     * 更新媒体权限
     */
    suspend fun update(permission: MediaPermission): MediaPermission
    
    /**
     * 根据ID查找权限
     */
    suspend fun findById(id: UUID): MediaPermission?
    
    /**
     * 根据媒体ID查找权限列表
     */
    suspend fun findByMediaId(mediaId: UUID): List<MediaPermission>
    
    /**
     * 根据文件夹ID查找权限列表
     */
    suspend fun findByFolderId(folderId: UUID): List<MediaPermission>
    
    /**
     * 检查授权对象是否有指定媒体的指定权限
     */
    suspend fun hasMediaPermission(
        mediaId: UUID, 
        permissionType: MediaPermissionType, 
        granteeType: PermissionGranteeType, 
        granteeId: UUID?
    ): Boolean
    
    /**
     * 检查授权对象是否有指定文件夹的指定权限
     */
    suspend fun hasFolderPermission(
        folderId: UUID, 
        permissionType: MediaPermissionType, 
        granteeType: PermissionGranteeType, 
        granteeId: UUID?
    ): Boolean
    
    /**
     * 删除权限
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 删除媒体的所有权限
     */
    suspend fun deleteAllForMedia(mediaId: UUID): Boolean
    
    /**
     * 删除文件夹的所有权限
     */
    suspend fun deleteAllForFolder(folderId: UUID): Boolean
    
    /**
     * 为文件夹的所有媒体文件应用权限
     */
    suspend fun applyFolderPermissionsToMedia(folderId: UUID, recursive: Boolean = false): Int
} 