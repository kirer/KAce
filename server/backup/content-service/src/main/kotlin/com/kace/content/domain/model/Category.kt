package com.kace.content.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * 分类
 */
@Serializable
data class Category(
    val id: UUID,
    val name: String,
    val description: String?,
    val parentId: UUID?,
    val slug: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun create(name: String, description: String?, parentId: UUID?, slug: String): Category {
            val now = Instant.now().toEpochMilli()
            return Category(
                id = UUID.randomUUID(),
                name = name,
                description = description,
                parentId = parentId,
                slug = slug,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    fun update(name: String? = null, description: String? = null, parentId: UUID? = this.parentId, slug: String? = null): Category {
        return this.copy(
            name = name ?: this.name,
            description = description ?: this.description,
            parentId = parentId,
            slug = slug ?: this.slug,
            updatedAt = Instant.now().toEpochMilli()
        )
    }
}

/**
 * 内容分类关联
 */
@Serializable
data class ContentCategory(
    val contentId: UUID,
    val categoryId: UUID
) {
    companion object {
        fun create(contentId: UUID, categoryId: UUID): ContentCategory {
            return ContentCategory(
                contentId = contentId,
                categoryId = categoryId
            )
        }
    }
} 