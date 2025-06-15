package com.kace.content.api.response

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ContentRelationResponse(
    val id: UUID,
    val sourceId: UUID,
    val targetId: UUID,
    val relationType: String,
    val metadata: Map<String, String>? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 