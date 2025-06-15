package com.kace.content.api.response

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ContentVersionResponse(
    val id: UUID,
    val contentId: UUID,
    val title: String,
    val description: String? = null,
    val body: String? = null,
    val status: String,
    val metadata: Map<String, String>? = null,
    val version: Int,
    val createdAt: LocalDateTime,
    val createdBy: UUID? = null
) 