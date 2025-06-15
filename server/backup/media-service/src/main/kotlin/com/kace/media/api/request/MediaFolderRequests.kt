package com.kace.media.api.request

import kotlinx.serialization.Serializable

/**
 * 创建文件夹请求
 */
@Serializable
data class CreateFolderRequest(
    val name: String,
    val description: String? = null,
    val parentId: String? = null
)

/**
 * 更新文件夹请求
 */
@Serializable
data class UpdateFolderRequest(
    val name: String? = null,
    val description: String? = null
)

/**
 * 移动文件夹请求
 */
@Serializable
data class MoveFolderRequest(
    val newParentId: String? = null
)

/**
 * 文件夹查询请求
 */
@Serializable
data class FolderQueryRequest(
    val page: Int = 1,
    val size: Int = 20
)

/**
 * 批量删除文件夹请求
 */
@Serializable
data class BatchDeleteFolderRequest(
    val ids: List<String>,
    val recursive: Boolean = false
) 