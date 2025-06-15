package com.kace.content.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * 标签
 */
@Serializable
data class Tag(
    val id: UUID,
    val name: String,
    val slug: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun create(name: String, slug: String): Tag {
            val now = Instant.now().toEpochMilli()
            return Tag(
                id = UUID.randomUUID(),
                name = name,
                slug = slug,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    fun update(name: String? = null, slug: String? = null): Tag {
        return this.copy(
            name = name ?: this.name,
            slug = slug ?: this.slug,
            updatedAt = Instant.now().toEpochMilli()
        )
    }
}

/**
 * 内容标签关联
 */
@Serializable
data class ContentTag(
    val contentId: UUID,
    val tagId: UUID
) {
    companion object {
        fun create(contentId: UUID, tagId: UUID): ContentTag {
            return ContentTag(
                contentId = contentId,
                tagId = tagId
            )
        }
    }
} 