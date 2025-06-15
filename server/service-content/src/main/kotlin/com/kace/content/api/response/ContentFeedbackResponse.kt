package com.kace.content.api.response

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ContentFeedbackResponse(
    val id: UUID,
    val contentId: UUID,
    val userId: UUID,
    val type: String,
    val value: Int,
    val metadata: Map<String, String>? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Serializable
data class ContentFeedbackStatsResponse(
    val contentId: UUID,
    val likes: Int = 0,
    val ratingAverage: Double? = null,
    val ratingCount: Int? = null,
    val ratingDistribution: Map<Int, Int>? = null,
    val helpfulCount: Int? = null,
    val notHelpfulCount: Int? = null,
    val reactions: Map<Int, Int>? = null
)

@Serializable
data class UserFeedbackStatusResponse(
    val contentId: UUID,
    val userId: UUID,
    val liked: Boolean? = null,
    val rating: Int? = null,
    val helpful: Int? = null,
    val reaction: Int? = null
) 