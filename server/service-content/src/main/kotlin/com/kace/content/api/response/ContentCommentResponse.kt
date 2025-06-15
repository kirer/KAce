package com.kace.content.api.response

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ContentCommentResponse(
    val id: UUID,
    val contentId: UUID,
    val userId: UUID,
    val parentId: UUID? = null,
    val content: String,
    val status: String,
    val metadata: Map<String, String>? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val replyCount: Long = 0,
    val userInfo: UserInfo? = null
)

@Serializable
data class ContentCommentDetailResponse(
    val id: UUID,
    val contentId: UUID,
    val userId: UUID,
    val parentId: UUID? = null,
    val content: String,
    val status: String,
    val metadata: Map<String, String>? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val replies: List<ContentCommentResponse>? = null,
    val userInfo: UserInfo? = null,
    val contentInfo: ContentInfo? = null
)

@Serializable
data class UserInfo(
    val id: UUID,
    val username: String,
    val avatar: String? = null
)

@Serializable
data class ContentInfo(
    val id: UUID,
    val title: String,
    val type: String
) 