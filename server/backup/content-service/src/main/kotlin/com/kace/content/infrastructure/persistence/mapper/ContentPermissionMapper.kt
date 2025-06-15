package com.kace.content.infrastructure.persistence.mapper

import com.kace.content.domain.model.ContentPermission
import com.kace.content.domain.model.ContentPermissionType
import com.kace.content.domain.model.ContentPermissionSubjectType
import com.kace.content.infrastructure.persistence.entity.ContentPermissionEntity

/**
 * 将领域模型转换为实体
 */
fun ContentPermission.toEntity(): ContentPermissionEntity {
    val entity = ContentPermissionEntity.new(id) {
        contentId = this@toEntity.contentId
        contentTypeId = this@toEntity.contentTypeId
        permissionType = this@toEntity.permissionType.name
        subjectType = this@toEntity.subjectType.name
        subjectId = this@toEntity.subjectId
        createdBy = this@toEntity.createdBy
        createdAt = this@toEntity.createdAt
        updatedAt = this@toEntity.updatedAt
    }
    return entity
}

/**
 * 将实体转换为领域模型
 */
fun ContentPermissionEntity.toContentPermission(): ContentPermission {
    return ContentPermission(
        id = this.id.value,
        contentId = this.contentId!!,
        contentTypeId = this.contentTypeId,
        permissionType = ContentPermissionType.valueOf(this.permissionType),
        subjectType = ContentPermissionSubjectType.valueOf(this.subjectType),
        subjectId = this.subjectId,
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
} 