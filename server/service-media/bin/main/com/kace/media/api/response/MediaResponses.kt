package com.kace.media.api.response

import com.kace.media.domain.model.Media
import com.kace.media.domain.model.MediaFolder
import com.kace.media.domain.model.MediaProcessingTask
import com.kace.media.domain.model.MediaStatus
import com.kace.media.domain.model.MediaType
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * 媒体响应
 */
@Serializable
data class MediaResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val type: String,
    val mimeType: String,
    val size: Long,
    val url: String,
    val thumbnailUrl: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Long? = null,
    val status: String,
    val metadata: Map<String, String>? = null,
    val folderId: String? = null,
    val folderName: String? = null,
    val tags: List<String> = emptyList(),
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * 媒体文件夹响应
 */
@Serializable
data class MediaFolderResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val parentName: String? = null,
    val path: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val mediaCount: Int = 0,
    val subFolderCount: Int = 0
)

/**
 * 媒体路径响应
 */
@Serializable
data class MediaPathResponse(
    val folders: List<MediaFolderResponse>
)

/**
 * 媒体处理任务响应
 */
@Serializable
data class MediaProcessingTaskResponse(
    val id: String,
    val mediaId: String,
    val type: String,
    val status: String,
    val parameters: Map<String, String>? = null,
    val result: Map<String, String>? = null,
    val errorMessage: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null
)

/**
 * 媒体统计响应
 */
@Serializable
data class MediaStatsResponse(
    val totalMedia: Int,
    val totalSize: Long,
    val byType: Map<String, Int>,
    val byStatus: Map<String, Int>
)

/**
 * 媒体上传URL响应
 */
@Serializable
data class MediaUploadUrlResponse(
    val uploadUrl: String,
    val formFields: Map<String, String>? = null,
    val mediaId: String
)

/**
 * 将领域模型转换为响应模型
 */
fun Media.toResponse(): MediaResponse {
    return MediaResponse(
        id = id.toString(),
        name = name,
        description = description,
        type = type.name,
        mimeType = mimeType,
        size = size,
        url = url,
        thumbnailUrl = thumbnailUrl,
        width = width,
        height = height,
        duration = duration,
        status = status.name,
        metadata = metadata?.mapValues { it.value.toString() },
        folderId = folderId?.toString(),
        folderName = null,  // 需要在服务层填充
        tags = tags,
        createdBy = createdBy.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}

/**
 * 将领域模型转换为响应模型
 */
fun MediaFolder.toResponse(mediaCount: Int = 0, subFolderCount: Int = 0): MediaFolderResponse {
    return MediaFolderResponse(
        id = id.toString(),
        name = name,
        description = description,
        parentId = parentId?.toString(),
        parentName = null,  // 需要在服务层填充
        path = path,
        createdBy = createdBy.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        mediaCount = mediaCount,
        subFolderCount = subFolderCount
    )
}

/**
 * 将领域模型转换为响应模型
 */
fun MediaProcessingTask.toResponse(): MediaProcessingTaskResponse {
    return MediaProcessingTaskResponse(
        id = id.toString(),
        mediaId = mediaId.toString(),
        type = type.name,
        status = status.name,
        parameters = parameters?.mapValues { it.value.toString() },
        result = result?.mapValues { it.value.toString() },
        errorMessage = errorMessage,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        completedAt = completedAt?.toString()
    )
} 