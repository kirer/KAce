package com.kace.content.api.response

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ContentResponse(
    val id: String,
    val contentTypeId: String,
    val title: String,
    val slug: String,
    val status: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String?,
    val version: Int,
    val languageCode: String,
    val fields: Map<String, @Serializable(with = com.kace.content.api.request.AnySerializer::class) Any>
)

@Serializable
data class ContentListResponse(
    val items: List<ContentResponse>,
    val total: Int,
    val page: Int,
    val size: Int
)

@Serializable
data class ContentVersionResponse(
    val id: String,
    val contentId: String,
    val version: Int,
    val fieldsJson: String,
    val createdBy: String,
    val createdAt: String,
    val comment: String?
)

@Serializable
data class ContentVersionListResponse(
    val items: List<ContentVersionResponse>
) 