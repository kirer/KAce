package com.kace.media.api.response

import com.kace.media.domain.model.MediaPermission
import kotlinx.serialization.Serializable

/**
 * 媒体权限响应
 */
@Serializable
data class MediaPermissionResponse(
    val id: String,
    val mediaId: String?,
    val folderId: String?,
    val permissionType: String,
    val granteeType: String,
    val granteeId: String?,
    val isInherited: Boolean,
    val expiresAt: Long?,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * 权限检查响应
 */
@Serializable
data class PermissionCheckResponse(
    val hasPermission: Boolean
)

/**
 * 将领域模型转换为响应模型
 */
fun MediaPermission.toResponse(): MediaPermissionResponse {
    return MediaPermissionResponse(
        id = id.toString(),
        mediaId = mediaId?.toString(),
        folderId = folderId?.toString(),
        permissionType = permissionType.name,
        granteeType = granteeType.name,
        granteeId = granteeId?.toString(),
        isInherited = isInherited,
        expiresAt = expiresAt?.toEpochMilli(),
        createdBy = createdBy.toString(),
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}

/**
 * 将权限列表转换为响应列表
 */
fun List<MediaPermission>.toResponse(): List<MediaPermissionResponse> {
    return map { it.toResponse() }
} 