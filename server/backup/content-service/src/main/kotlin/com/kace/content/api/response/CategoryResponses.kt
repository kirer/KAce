package com.kace.content.api.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: String,
    val name: String,
    val description: String?,
    val parentId: String?,
    val slug: String,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String
)

@Serializable
data class CategoryListResponse(
    val items: List<CategoryResponse>,
    val total: Int,
    val page: Int,
    val size: Int
)

@Serializable
data class CategoryTreeNode(
    val id: String,
    val name: String,
    val description: String?,
    val slug: String,
    val children: List<CategoryTreeNode>
)

@Serializable
data class CategoryTreeResponse(
    val items: List<CategoryTreeNode>
) 