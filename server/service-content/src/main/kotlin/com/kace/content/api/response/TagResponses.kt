package com.kace.content.api.response

import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    val id: String,
    val name: String,
    val slug: String,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String
)

@Serializable
data class TagListResponse(
    val items: List<TagResponse>,
    val total: Int,
    val page: Int,
    val size: Int
) 