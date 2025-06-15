package com.kace.content.api.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryRequest(
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val slug: String
)

@Serializable
data class UpdateCategoryRequest(
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val slug: String
) 