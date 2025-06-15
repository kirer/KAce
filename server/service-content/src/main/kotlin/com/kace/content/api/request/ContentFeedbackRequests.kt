package com.kace.content.api.request

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreateFeedbackRequest(
    val contentId: UUID,
    val type: String,
    val value: Int,
    val metadata: Map<String, String>? = null
)

@Serializable
data class UpdateFeedbackRequest(
    val value: Int,
    val metadata: Map<String, String>? = null
)

@Serializable
data class LikeContentRequest(
    val contentId: UUID,
    val isLiked: Boolean
)

@Serializable
data class RateContentRequest(
    val contentId: UUID,
    val rating: Int
)

@Serializable
data class MarkHelpfulRequest(
    val contentId: UUID,
    val isHelpful: Boolean
) 