package com.kace.media.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 媒体权限类型枚举
 */
enum class MediaPermissionType {
    VIEW,    // 查看权限
    DOWNLOAD,// 下载权限
    EDIT,    // 编辑权限
    DELETE,  // 删除权限
    SHARE,   // 分享权限
    ALL      // 所有权限
}

/**
 * 权限授权对象类型
 */
enum class PermissionGranteeType {
    USER,           // 用户
    ORGANIZATION,   // 组织
    ROLE,           // 角色
    PUBLIC          // 公开的（匿名访问）
}

/**
 * 媒体权限领域模型
 */
data class MediaPermission(
    val id: UUID,
    val mediaId: UUID?,         // 媒体ID，如果为null则表示文件夹权限
    val folderId: UUID?,        // 文件夹ID，如果为null则表示媒体权限
    val permissionType: MediaPermissionType,
    val granteeType: PermissionGranteeType,
    val granteeId: UUID?,       // 授权对象ID，对于PUBLIC为null
    val isInherited: Boolean,   // 是否从父文件夹继承的权限
    val expiresAt: Instant?,    // 权限过期时间，null表示永不过期
    val createdBy: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(mediaId != null || folderId != null) { "媒体ID和文件夹ID不能同时为null" }
        require(!(mediaId != null && folderId != null)) { "媒体ID和文件夹ID不能同时存在" }
        require(granteeId != null || granteeType == PermissionGranteeType.PUBLIC) { "非公开权限必须指定授权对象ID" }
    }
} 