package com.kace.content.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * 内容状态
 */
@Serializable
enum class ContentStatus {
    DRAFT,      // 草稿
    REVIEW,     // 审核中
    PUBLISHED,  // 已发布
    ARCHIVED    // 已归档
}

/**
 * 内容
 */
@Serializable
data class Content(
    val id: UUID,
    val contentTypeId: UUID,
    val title: String,
    val slug: String,
    val status: ContentStatus,
    val createdBy: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val publishedAt: Long?,
    val fields: Map<String, String>,
    val version: Int,
    val languageCode: String
) {
    companion object {
        fun create(
            contentTypeId: UUID,
            title: String,
            slug: String,
            createdBy: UUID,
            fields: Map<String, String>,
            languageCode: String
        ): Content {
            val now = Instant.now().toEpochMilli()
            return Content(
                id = UUID.randomUUID(),
                contentTypeId = contentTypeId,
                title = title,
                slug = slug,
                status = ContentStatus.DRAFT,
                createdBy = createdBy,
                createdAt = now,
                updatedAt = now,
                publishedAt = null,
                fields = fields,
                version = 1,
                languageCode = languageCode
            )
        }
    }
    
    fun update(
        title: String? = null,
        slug: String? = null,
        fields: Map<String, String>? = null
    ): Content {
        return this.copy(
            title = title ?: this.title,
            slug = slug ?: this.slug,
            fields = fields ?: this.fields,
            updatedAt = Instant.now().toEpochMilli(),
            version = this.version + 1
        )
    }
    
    fun publish(): Content {
        val now = Instant.now().toEpochMilli()
        return this.copy(
            status = ContentStatus.PUBLISHED,
            publishedAt = now,
            updatedAt = now
        )
    }
    
    fun archive(): Content {
        return this.copy(
            status = ContentStatus.ARCHIVED,
            updatedAt = Instant.now().toEpochMilli()
        )
    }
    
    fun sendToReview(): Content {
        return this.copy(
            status = ContentStatus.REVIEW,
            updatedAt = Instant.now().toEpochMilli()
        )
    }
}

/**
 * 内容版本
 */
@Serializable
data class ContentVersion(
    val id: UUID,
    val contentId: UUID,
    val version: Int,
    val fields: Map<String, String>,
    val createdBy: UUID,
    val createdAt: Long,
    val comment: String?
) {
    companion object {
        fun create(
            contentId: UUID,
            version: Int,
            fields: Map<String, String>,
            createdBy: UUID,
            comment: String? = null
        ): ContentVersion {
            return ContentVersion(
                id = UUID.randomUUID(),
                contentId = contentId,
                version = version,
                fields = fields,
                createdBy = createdBy,
                createdAt = Instant.now().toEpochMilli(),
                comment = comment
            )
        }
    }
} 