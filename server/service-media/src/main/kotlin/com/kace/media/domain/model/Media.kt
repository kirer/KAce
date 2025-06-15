package com.kace.media.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 媒体类型枚举
 */
enum class MediaType {
    IMAGE, VIDEO, DOCUMENT, AUDIO, OTHER
}

/**
 * 媒体状态枚举
 */
enum class MediaStatus {
    UPLOADING, PROCESSING, READY, FAILED, DELETED
}

/**
 * 媒体实体领域模型
 */
data class Media(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val type: MediaType,
    val mimeType: String,
    val size: Long,
    val path: String,
    val url: String,
    val thumbnailUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Long? = null,
    val status: MediaStatus,
    val metadata: Map<String, Any>? = null,
    val folderId: UUID? = null,
    val tags: List<String> = emptyList(),
    val createdBy: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) 