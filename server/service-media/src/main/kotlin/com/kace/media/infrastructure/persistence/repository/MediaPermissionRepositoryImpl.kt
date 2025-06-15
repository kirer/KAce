package com.kace.media.infrastructure.persistence.repository

import com.kace.media.domain.model.MediaPermission
import com.kace.media.domain.model.MediaPermissionType
import com.kace.media.domain.model.PermissionGranteeType
import com.kace.media.domain.repository.MediaPermissionRepository
import com.kace.media.infrastructure.persistence.entity.MediaPermissionEntity
import com.kace.media.infrastructure.persistence.entity.MediaPermissions
import com.kace.media.infrastructure.persistence.entity.MediaTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.util.UUID

/**
 * 媒体权限仓库实现类
 */
class MediaPermissionRepositoryImpl : MediaPermissionRepository {

    /**
     * 创建媒体权限
     */
    override suspend fun create(permission: MediaPermission): MediaPermission = newSuspendedTransaction(Dispatchers.IO) {
        val entity = MediaPermissionEntity.new {
            this.mediaId = permission.mediaId
            this.folderId = permission.folderId
            this.permissionType = permission.permissionType
            this.granteeType = permission.granteeType
            this.granteeId = permission.granteeId
            this.isInherited = permission.isInherited
            this.expiresAt = permission.expiresAt
            this.createdBy = permission.createdBy
            this.createdAt = permission.createdAt
            this.updatedAt = permission.updatedAt
        }
        toModel(entity)
    }

    /**
     * 更新媒体权限
     */
    override suspend fun update(permission: MediaPermission): MediaPermission = newSuspendedTransaction(Dispatchers.IO) {
        val entity = MediaPermissionEntity.findById(permission.id)
            ?: throw IllegalArgumentException("权限不存在：${permission.id}")
            
        entity.apply {
            this.mediaId = permission.mediaId
            this.folderId = permission.folderId
            this.permissionType = permission.permissionType
            this.granteeType = permission.granteeType
            this.granteeId = permission.granteeId
            this.isInherited = permission.isInherited
            this.expiresAt = permission.expiresAt
            this.updatedAt = permission.updatedAt
        }
        
        toModel(entity)
    }

    /**
     * 根据ID查找权限
     */
    override suspend fun findById(id: UUID): MediaPermission? = newSuspendedTransaction(Dispatchers.IO) {
        MediaPermissionEntity.findById(id)?.let { toModel(it) }
    }

    /**
     * 根据媒体ID查找权限列表
     */
    override suspend fun findByMediaId(mediaId: UUID): List<MediaPermission> = newSuspendedTransaction(Dispatchers.IO) {
        MediaPermissionEntity.find { 
            MediaPermissions.mediaId eq mediaId 
        }.map { toModel(it) }
    }

    /**
     * 根据文件夹ID查找权限列表
     */
    override suspend fun findByFolderId(folderId: UUID): List<MediaPermission> = newSuspendedTransaction(Dispatchers.IO) {
        MediaPermissionEntity.find { 
            MediaPermissions.folderId eq folderId 
        }.map { toModel(it) }
    }

    /**
     * 检查授权对象是否有指定媒体的指定权限
     */
    override suspend fun hasMediaPermission(
        mediaId: UUID,
        permissionType: MediaPermissionType,
        granteeType: PermissionGranteeType,
        granteeId: UUID?
    ): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val now = Instant.now()
        
        // 检查是否有明确的权限或ALL权限
        val hasDirectPermission = MediaPermissionEntity.find {
            (MediaPermissions.mediaId eq mediaId) and
                ((MediaPermissions.permissionType eq permissionType) or (MediaPermissions.permissionType eq MediaPermissionType.ALL)) and
                (MediaPermissions.granteeType eq granteeType) and
                ((MediaPermissions.granteeId eq granteeId) or (granteeType eq PermissionGranteeType.PUBLIC)) and
                ((MediaPermissions.expiresAt greater now) or (MediaPermissions.expiresAt.isNull()))
        }.count() > 0

        if (hasDirectPermission) {
            return@newSuspendedTransaction true
        }
        
        // 查找媒体的文件夹ID
        val media = MediaTable.select(MediaTable.folderId)
            .where { MediaTable.id eq mediaId }
            .firstOrNull()
        
        val folderId = media?.get(MediaTable.folderId) ?: return@newSuspendedTransaction false
        
        // 检查是否有文件夹权限
        hasFolderPermission(folderId, permissionType, granteeType, granteeId)
    }

    /**
     * 检查授权对象是否有指定文件夹的指定权限
     */
    override suspend fun hasFolderPermission(
        folderId: UUID,
        permissionType: MediaPermissionType,
        granteeType: PermissionGranteeType,
        granteeId: UUID?
    ): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val now = Instant.now()
        
        MediaPermissionEntity.find {
            (MediaPermissions.folderId eq folderId) and
                ((MediaPermissions.permissionType eq permissionType) or (MediaPermissions.permissionType eq MediaPermissionType.ALL)) and
                (MediaPermissions.granteeType eq granteeType) and
                ((MediaPermissions.granteeId eq granteeId) or (granteeType eq PermissionGranteeType.PUBLIC)) and
                ((MediaPermissions.expiresAt greater now) or (MediaPermissions.expiresAt.isNull()))
        }.count() > 0
    }

    /**
     * 删除权限
     */
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val entity = MediaPermissionEntity.findById(id) ?: return@newSuspendedTransaction false
        entity.delete()
        true
    }

    /**
     * 删除媒体的所有权限
     */
    override suspend fun deleteAllForMedia(mediaId: UUID): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val count = MediaPermissionEntity.find { 
            MediaPermissions.mediaId eq mediaId 
        }.count()
        
        MediaPermissionEntity.find { 
            MediaPermissions.mediaId eq mediaId 
        }.forEach { it.delete() }
        
        count > 0
    }

    /**
     * 删除文件夹的所有权限
     */
    override suspend fun deleteAllForFolder(folderId: UUID): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val count = MediaPermissionEntity.find { 
            MediaPermissions.folderId eq folderId 
        }.count()
        
        MediaPermissionEntity.find { 
            MediaPermissions.folderId eq folderId 
        }.forEach { it.delete() }
        
        count > 0
    }

    /**
     * 为文件夹的所有媒体文件应用权限
     */
    override suspend fun applyFolderPermissionsToMedia(folderId: UUID, recursive: Boolean): Int = newSuspendedTransaction(Dispatchers.IO) {
        // 待实现：复杂的权限继承逻辑，涉及递归查询子文件夹及其媒体文件
        // 这里需要多个SQL操作，包括查询并创建新权限记录
        0 // 暂时返回0，表示没有应用任何权限
    }

    /**
     * 将实体转换为模型
     */
    private fun toModel(entity: MediaPermissionEntity): MediaPermission {
        return MediaPermission(
            id = entity.id.value,
            mediaId = entity.mediaId,
            folderId = entity.folderId,
            permissionType = entity.permissionType,
            granteeType = entity.granteeType,
            granteeId = entity.granteeId,
            isInherited = entity.isInherited,
            expiresAt = entity.expiresAt,
            createdBy = entity.createdBy,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
} 