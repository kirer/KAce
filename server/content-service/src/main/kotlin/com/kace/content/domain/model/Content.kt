package com.kace.content.domain.model

/**
 * 内容领域模型
 */
data class Content(
    val id: String,
    val title: String,
    val description: String? = null,
    val status: ContentStatus = ContentStatus.DRAFT
)

/**
 * 内容状态
 */
enum class ContentStatus {
    DRAFT,      // 草稿
    PUBLISHED,  // 已发布
    ARCHIVED    // 已归档
} 