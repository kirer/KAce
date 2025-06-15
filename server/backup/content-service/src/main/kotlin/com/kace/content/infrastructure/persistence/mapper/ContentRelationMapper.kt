package com.kace.content.infrastructure.persistence.mapper

import com.kace.content.domain.model.ContentRelation
import com.kace.content.domain.model.ContentRelationType
import com.kace.content.infrastructure.persistence.entity.ContentRelationEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 将领域模型转换为实体
 */
fun ContentRelation.toEntity(): ContentRelationEntity {
    val entity = ContentRelationEntity.new(id) {
        sourceContentId = this@toEntity.sourceContentId
        targetContentId = this@toEntity.targetContentId
        type = this@toEntity.type.name
        metadata = Json.encodeToString(this@toEntity.metadata)
        createdBy = this@toEntity.createdBy
        createdAt = this@toEntity.createdAt
        updatedAt = this@toEntity.updatedAt
    }
    return entity
}

/**
 * 将实体转换为领域模型
 */
fun ContentRelationEntity.toContentRelation(): ContentRelation {
    val metadata: Map<String, String> = try {
        Json.decodeFromString(this.metadata)
    } catch (e: Exception) {
        emptyMap()
    }
    
    return ContentRelation(
        id = this.id.value,
        sourceContentId = this.sourceContentId,
        targetContentId = this.targetContentId,
        type = ContentRelationType.valueOf(this.type),
        metadata = metadata,
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
} 