package com.kace.content.api.request

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateCommentRequest(
    val contentId: UUID,
    val content: String,
    val parentId: UUID? = null,
    val metadata: Map<String, String>? = null
)

@Serializable
data class UpdateCommentRequest(
    val content: String
)

@Serializable
data class UpdateCommentStatusRequest(
    val status: String
)

@Serializable
data class BatchUpdateCommentStatusRequest(
    val ids: List<UUID>,
    val status: String
) 