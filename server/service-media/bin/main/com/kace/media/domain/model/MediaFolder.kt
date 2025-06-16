package com.kace.media.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 媒体文件夹领域模型
 */
data class MediaFolder(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val parentId: UUID? = null,
    val path: String,
    val createdBy: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) 