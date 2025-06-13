package com.kace.media.domain.model

/**
 * 媒体领域模型
 */
data class Media(
    val id: String,
    val name: String,
    val type: MediaType,
    val url: String,
    val size: Long,
    val mimeType: String
)

/**
 * 媒体类型
 */
enum class MediaType {
    IMAGE,  // 图片
    VIDEO,  // 视频
    AUDIO,  // 音频
    DOCUMENT,  // 文档
    OTHER  // 其他
} 