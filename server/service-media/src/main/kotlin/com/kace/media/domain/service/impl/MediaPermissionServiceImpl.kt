package com.kace.media.domain.service.impl

import com.kace.media.domain.model.MediaPermission
import com.kace.media.domain.model.MediaPermissionType
import com.kace.media.domain.model.PermissionGranteeType
import com.kace.media.domain.repository.MediaPermissionRepository
import com.kace.media.domain.repository.MediaRepository
import com.kace.media.domain.service.MediaPermissionService
import java.time.Instant
import java.util.UUID

/**
 * 媒体权限服务实现类
 */
class MediaPermissionServiceImpl(
    private val mediaPermissionRepository: MediaPermissionRepository,
    private val mediaRepository: MediaRepository
) : MediaPermissionService {

    /**
     * 创建媒体权限
     */
    override suspend fun createPermission(permission: MediaPermission): MediaPermission {
        return mediaPermissionRepository.create(permission)
    }

    /**
     * 更新媒体权限
     */
    override suspend fun updatePermission(permission: MediaPermission): MediaPermission {
        return mediaPermissionRepository.update(permission)
    }

    /**
     * 获取媒体权限列表
     */
    override suspend fun getMediaPermissions(mediaId: UUID): List<MediaPermission> {
        return mediaPermissionRepository.findByMediaId(mediaId)
    }

    /**
     * 获取文件夹权限列表
     */
    override suspend fun getFolderPermissions(folderId: UUID): List<MediaPermission> {
        return mediaPermissionRepository.findByFolderId(folderId)
    }

    /**
     * 删除权限
     */
    override suspend fun deletePermission(id: UUID): Boolean {
        return mediaPermissionRepository.delete(id)
    }

    /**
     * 检查用户是否有指定媒体的指定权限
     */
    override suspend fun userHasMediaPermission(mediaId: UUID, userId: UUID, permissionType: MediaPermissionType): Boolean {
        // 首先检查用户直接权限
        if (mediaPermissionRepository.hasMediaPermission(mediaId, permissionType, PermissionGranteeType.USER, userId)) {
            return true
        }
        
        // 然后检查公开权限
        if (mediaPermissionRepository.hasMediaPermission(mediaId, permissionType, PermissionGranteeType.PUBLIC, null)) {
            return true
        }
        
        // 可以扩展检查用户的角色和组织权限
        return false
    }

    /**
     * 检查用户是否有指定文件夹的指定权限
     */
    override suspend fun userHasFolderPermission(folderId: UUID, userId: UUID, permissionType: MediaPermissionType): Boolean {
        // 首先检查用户直接权限
        if (mediaPermissionRepository.hasFolderPermission(folderId, permissionType, PermissionGranteeType.USER, userId)) {
            return true
        }
        
        // 然后检查公开权限
        if (mediaPermissionRepository.hasFolderPermission(folderId, permissionType, PermissionGranteeType.PUBLIC, null)) {
            return true
        }
        
        // 可以扩展检查用户的角色和组织权限
        return false
    }

    /**
     * 授予用户媒体权限
     */
    override suspend fun grantUserMediaPermission(
        mediaId: UUID, 
        userId: UUID, 
        permissionType: MediaPermissionType,
        expiresAt: Long?
    ): MediaPermission {
        val now = Instant.now()
        val expiration = expiresAt?.let { Instant.ofEpochMilli(it) }
        
        return createPermission(
            MediaPermission(
                id = UUID.randomUUID(),
                mediaId = mediaId,
                folderId = null,
                permissionType = permissionType,
                granteeType = PermissionGranteeType.USER,
                granteeId = userId,
                isInherited = false,
                expiresAt = expiration,
                createdBy = userId,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    /**
     * 授予用户文件夹权限
     */
    override suspend fun grantUserFolderPermission(
        folderId: UUID,
        userId: UUID,
        permissionType: MediaPermissionType,
        expiresAt: Long?,
        applyToChildren: Boolean
    ): MediaPermission {
        val now = Instant.now()
        val expiration = expiresAt?.let { Instant.ofEpochMilli(it) }
        
        val permission = createPermission(
            MediaPermission(
                id = UUID.randomUUID(),
                mediaId = null,
                folderId = folderId,
                permissionType = permissionType,
                granteeType = PermissionGranteeType.USER,
                granteeId = userId,
                isInherited = false,
                expiresAt = expiration,
                createdBy = userId,
                createdAt = now,
                updatedAt = now
            )
        )
        
        if (applyToChildren) {
            mediaPermissionRepository.applyFolderPermissionsToMedia(folderId, true)
        }
        
        return permission
    }

    /**
     * 授予角色媒体权限
     */
    override suspend fun grantRoleMediaPermission(mediaId: UUID, roleId: UUID, permissionType: MediaPermissionType): MediaPermission {
        val now = Instant.now()
        
        return createPermission(
            MediaPermission(
                id = UUID.randomUUID(),
                mediaId = mediaId,
                folderId = null,
                permissionType = permissionType,
                granteeType = PermissionGranteeType.ROLE,
                granteeId = roleId,
                isInherited = false,
                expiresAt = null,
                createdBy = UUID.randomUUID(),  // 这里应该是当前用户ID，暂时使用随机UUID
                createdAt = now,
                updatedAt = now
            )
        )
    }

    /**
     * 授予角色文件夹权限
     */
    override suspend fun grantRoleFolderPermission(
        folderId: UUID,
        roleId: UUID,
        permissionType: MediaPermissionType,
        applyToChildren: Boolean
    ): MediaPermission {
        val now = Instant.now()
        
        val permission = createPermission(
            MediaPermission(
                id = UUID.randomUUID(),
                mediaId = null,
                folderId = folderId,
                permissionType = permissionType,
                granteeType = PermissionGranteeType.ROLE,
                granteeId = roleId,
                isInherited = false,
                expiresAt = null,
                createdBy = UUID.randomUUID(),  // 这里应该是当前用户ID，暂时使用随机UUID
                createdAt = now,
                updatedAt = now
            )
        )
        
        if (applyToChildren) {
            mediaPermissionRepository.applyFolderPermissionsToMedia(folderId, true)
        }
        
        return permission
    }

    /**
     * 授予组织媒体权限
     */
    override suspend fun grantOrganizationMediaPermission(
        mediaId: UUID,
        organizationId: UUID,
        permissionType: MediaPermissionType
    ): MediaPermission {
        val now = Instant.now()
        
        return createPermission(
            MediaPermission(
                id = UUID.randomUUID(),
                mediaId = mediaId,
                folderId = null,
                permissionType = permissionType,
                granteeType = PermissionGranteeType.ORGANIZATION,
                granteeId = organizationId,
                isInherited = false,
                expiresAt = null,
                createdBy = UUID.randomUUID(),  // 这里应该是当前用户ID，暂时使用随机UUID
                createdAt = now,
                updatedAt = now
            )
        )
    }

    /**
     * 授予组织文件夹权限
     */
    override suspend fun grantOrganizationFolderPermission(
        folderId: UUID,
        organizationId: UUID,
        permissionType: MediaPermissionType,
        applyToChildren: Boolean
    ): MediaPermission {
        val now = Instant.now()
        
        val permission = createPermission(
            MediaPermission(
                id = UUID.randomUUID(),
                mediaId = null,
                folderId = folderId,
                permissionType = permissionType,
                granteeType = PermissionGranteeType.ORGANIZATION,
                granteeId = organizationId,
                isInherited = false,
                expiresAt = null,
                createdBy = UUID.randomUUID(),  // 这里应该是当前用户ID，暂时使用随机UUID
                createdAt = now,
                updatedAt = now
            )
        )
        
        if (applyToChildren) {
            mediaPermissionRepository.applyFolderPermissionsToMedia(folderId, true)
        }
        
        return permission
    }

    /**
     * 设置媒体为公开访问
     */
    override suspend fun makeMediaPublic(
        mediaId: UUID,
        permissionTypes: Set<MediaPermissionType>
    ): List<MediaPermission> {
        val now = Instant.now()
        val permissions = mutableListOf<MediaPermission>()
        
        for (permissionType in permissionTypes) {
            permissions.add(
                createPermission(
                    MediaPermission(
                        id = UUID.randomUUID(),
                        mediaId = mediaId,
                        folderId = null,
                        permissionType = permissionType,
                        granteeType = PermissionGranteeType.PUBLIC,
                        granteeId = null,
                        isInherited = false,
                        expiresAt = null,
                        createdBy = UUID.randomUUID(),  // 这里应该是当前用户ID，暂时使用随机UUID
                        createdAt = now,
                        updatedAt = now
                    )
                )
            )
        }
        
        return permissions
    }

    /**
     * 设置文件夹为公开访问
     */
    override suspend fun makeFolderPublic(
        folderId: UUID,
        permissionTypes: Set<MediaPermissionType>,
        applyToChildren: Boolean
    ): List<MediaPermission> {
        val now = Instant.now()
        val permissions = mutableListOf<MediaPermission>()
        
        for (permissionType in permissionTypes) {
            permissions.add(
                createPermission(
                    MediaPermission(
                        id = UUID.randomUUID(),
                        mediaId = null,
                        folderId = folderId,
                        permissionType = permissionType,
                        granteeType = PermissionGranteeType.PUBLIC,
                        granteeId = null,
                        isInherited = false,
                        expiresAt = null,
                        createdBy = UUID.randomUUID(),  // 这里应该是当前用户ID，暂时使用随机UUID
                        createdAt = now,
                        updatedAt = now
                    )
                )
            )
        }
        
        if (applyToChildren) {
            mediaPermissionRepository.applyFolderPermissionsToMedia(folderId, true)
        }
        
        return permissions
    }

    /**
     * 撤销媒体的公开访问
     */
    override suspend fun makeMediaPrivate(mediaId: UUID): Boolean {
        // 查找所有PUBLIC权限并删除
        val publicPermissions = mediaPermissionRepository.findByMediaId(mediaId)
            .filter { it.granteeType == PermissionGranteeType.PUBLIC }
        
        return publicPermissions.all { mediaPermissionRepository.delete(it.id) }
    }

    /**
     * 撤销文件夹的公开访问
     */
    override suspend fun makeFolderPrivate(folderId: UUID, applyToChildren: Boolean): Boolean {
        // 查找所有PUBLIC权限并删除
        val publicPermissions = mediaPermissionRepository.findByFolderId(folderId)
            .filter { it.granteeType == PermissionGranteeType.PUBLIC }
        
        val result = publicPermissions.all { mediaPermissionRepository.delete(it.id) }
        
        if (applyToChildren) {
            mediaPermissionRepository.applyFolderPermissionsToMedia(folderId, true)
        }
        
        return result
    }

    /**
     * 应用父文件夹权限到子文件夹和媒体
     */
    override suspend fun inheritPermissions(folderId: UUID, recursive: Boolean): Int {
        return mediaPermissionRepository.applyFolderPermissionsToMedia(folderId, recursive)
    }
} 