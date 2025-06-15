package com.kace.media.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 媒体标签领域模型
 */
data class MediaTag(
    val id: UUID,
    val name: String,
    val slug: String,
    val createdBy: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
) 