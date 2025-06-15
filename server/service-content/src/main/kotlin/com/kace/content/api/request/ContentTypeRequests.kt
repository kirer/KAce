package com.kace.content.api.request

import com.kace.content.domain.model.FieldType
import com.kace.content.domain.model.Validation
import kotlinx.serialization.Serializable

/**
 * 创建内容类型请求
 */
@Serializable
data class CreateContentTypeRequest(
    val name: String,
    val description: String? = null,
    val fields: List<ContentFieldRequest>
)

/**
 * 更新内容类型请求
 */
@Serializable
data class UpdateContentTypeRequest(
    val name: String? = null,
    val description: String? = null,
    val fields: List<ContentFieldRequest>? = null
)

/**
 * 内容字段请求
 */
@Serializable
data class ContentFieldRequest(
    val name: String,
    val type: FieldType,
    val required: Boolean = false,
    val defaultValue: String? = null,
    val validations: List<Validation> = emptyList()
) 