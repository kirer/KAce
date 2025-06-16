package com.kace.media.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 媒体处理任务类型枚举
 */
enum class MediaProcessingTaskType {
    THUMBNAIL_GENERATION,
    IMAGE_RESIZE,
    VIDEO_TRANSCODING,
    METADATA_EXTRACTION
}

/**
 * 媒体处理任务状态枚举
 */
enum class MediaProcessingTaskStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}

/**
 * 媒体处理任务领域模型
 */
data class MediaProcessingTask(
    val id: UUID,
    val mediaId: UUID,
    val type: MediaProcessingTaskType,
    val status: MediaProcessingTaskStatus,
    val parameters: Map<String, Any>? = null,
    val result: Map<String, Any>? = null,
    val errorMessage: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val completedAt: Instant? = null
) 