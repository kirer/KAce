package com.kace.media.infrastructure.persistence.entity

import com.kace.media.domain.model.MediaPermissionType
import com.kace.media.domain.model.PermissionGranteeType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID

/**
 * 媒体权限数据库表定义
 */
object MediaPermissions : UUIDTable("media_permissions") {
    val mediaId = uuid("media_id").references(
        Medias.id, onDelete = ReferenceOption.CASCADE
    ).nullable()
    val folderId = uuid("folder_id").references(
        MediaFolders.id, onDelete = ReferenceOption.CASCADE
    ).nullable()
    val permissionType = enumerationByName("permission_type", 20, MediaPermissionType::class)
    val granteeType = enumerationByName("grantee_type", 20, PermissionGranteeType::class)
    val granteeId = uuid("grantee_id").nullable()
    val isInherited = bool("is_inherited").default(false)
    val expiresAt = timestamp("expires_at").nullable()
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    init {
        // 确保媒体ID和文件夹ID至少有一个不为null
        check { (mediaId.isNotNull() or folderId.isNotNull()) }
        // 不能同时设置媒体ID和文件夹ID
        check { not(mediaId.isNotNull() and folderId.isNotNull()) }
        // 检查PUBLIC权限的granteeId必须为null
        check { (granteeType neq PermissionGranteeType.PUBLIC) or (granteeId.isNull()) }
        // 检查非PUBLIC权限的granteeId必须不为null
        check { (granteeType eq PermissionGranteeType.PUBLIC) or (granteeId.isNotNull()) }
    }
}

/**
 * 媒体权限数据库实体
 */
class MediaPermissionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MediaPermissionEntity>(MediaPermissions)
    
    var mediaId by MediaPermissions.mediaId
    var folderId by MediaPermissions.folderId
    var permissionType by MediaPermissions.permissionType
    var granteeType by MediaPermissions.granteeType
    var granteeId by MediaPermissions.granteeId
    var isInherited by MediaPermissions.isInherited
    var expiresAt by MediaPermissions.expiresAt
    var createdBy by MediaPermissions.createdBy
    var createdAt by MediaPermissions.createdAt
    var updatedAt by MediaPermissions.updatedAt
} 