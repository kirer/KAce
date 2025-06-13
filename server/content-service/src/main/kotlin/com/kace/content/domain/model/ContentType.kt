package com.kace.content.domain.model

/**
 * 内容类型领域模型
 */
data class ContentType(
    val id: String,
    val name: String,
    val description: String? = null
) 