package com.kace.content.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 内容关联
 */
data class ContentRelation(
    /**
     * 关联ID
     */
    val id: UUID = UUID.randomUUID(),
    
    /**
     * 源内容ID
     */
    val sourceContentId: UUID,
    
    /**
     * 目标内容ID
     */
    val targetContentId: UUID,
    
    /**
     * 关联类型
     */
    val type: ContentRelationType,
    
    /**
     * 元数据
     */
    val metadata: Map<String, String> = emptyMap(),
    
    /**
     * 创建者ID
     */
    val createdBy: UUID,
    
    /**
     * 创建时间
     */
    val createdAt: Long = Instant.now().toEpochMilli(),
    
    /**
     * 更新时间
     */
    val updatedAt: Long = createdAt
) {
    companion object {
        /**
         * 创建内容关联
         */
        fun create(
            sourceContentId: UUID,
            targetContentId: UUID,
            type: ContentRelationType,
            metadata: Map<String, String> = emptyMap(),
            createdBy: UUID
        ): ContentRelation {
            return ContentRelation(
                sourceContentId = sourceContentId,
                targetContentId = targetContentId,
                type = type,
                metadata = metadata,
                createdBy = createdBy
            )
        }
    }
    
    /**
     * 更新内容关联
     */
    fun update(
        type: ContentRelationType? = null,
        metadata: Map<String, String>? = null
    ): ContentRelation {
        return copy(
            type = type ?: this.type,
            metadata = metadata ?: this.metadata,
            updatedAt = Instant.now().toEpochMilli()
        )
    }
} 