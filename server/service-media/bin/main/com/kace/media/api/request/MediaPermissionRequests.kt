package com.kace.media.api.request

import com.kace.media.domain.model.MediaPermissionType
import com.kace.media.domain.model.PermissionGranteeType
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 创建媒体权限请求
 */
@Serializable
data class CreateMediaPermissionRequest(
    val mediaId: String? = null,
    val folderId: String? = null,
    val permissionType: String,
    val granteeType: String,
    val granteeId: String? = null,
    val expiresAtEpochMillis: Long? = null
)

/**
 * 授予用户媒体权限请求
 */
@Serializable
data class GrantUserPermissionRequest(
    val userId: String,
    val permissionType: String,
    val expiresAtEpochMillis: Long? = null
)

/**
 * 授予角色媒体权限请求
 */
@Serializable
data class GrantRolePermissionRequest(
    val roleId: String,
    val permissionType: String
)

/**
 * 授予组织媒体权限请求
 */
@Serializable
data class GrantOrganizationPermissionRequest(
    val organizationId: String,
    val permissionType: String
)

/**
 * 设置公开权限请求
 */
@Serializable
data class MakePublicRequest(
    val permissionTypes: List<String> = listOf("VIEW"),
    val applyToChildren: Boolean = false
)

/**
 * 检查权限请求
 */
@Serializable
data class CheckPermissionRequest(
    val userId: String,
    val permissionType: String
) 