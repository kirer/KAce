package com.kace.media.api.request

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 创建媒体请求
 */
@Serializable
data class CreateMediaRequest(
    val name: String,
    val description: String? = null,
    val folderId: String? = null,
    val tags: List<String> = emptyList()
)

/**
 * 更新媒体请求
 */
@Serializable
data class UpdateMediaRequest(
    val name: String? = null,
    val description: String? = null,
    val folderId: String? = null,
    val tags: List<String>? = null
)

/**
 * 媒体搜索请求
 */
@Serializable
data class MediaSearchRequest(
    val query: String? = null,
    val type: String? = null,
    val folderId: String? = null,
    val tags: List<String>? = null,
    val page: Int = 1,
    val size: Int = 20,
    val sortBy: String = "createdAt",
    val sortDirection: String = "desc"
)

/**
 * 添加媒体标签请求
 */
@Serializable
data class AddMediaTagRequest(
    val tag: String
)

/**
 * 批量更新媒体请求
 */
@Serializable
data class BatchUpdateMediaRequest(
    val ids: List<String>,
    val update: UpdateMediaRequest
)

/**
 * 批量删除媒体请求
 */
@Serializable
data class BatchDeleteMediaRequest(
    val ids: List<String>
)

/**
 * 图片处理请求
 */
@Serializable
data class ImageProcessingRequest(
    val width: Int? = null,
    val height: Int? = null,
    val crop: Boolean = false,
    val quality: Int = 80,
    val format: String? = null
) 