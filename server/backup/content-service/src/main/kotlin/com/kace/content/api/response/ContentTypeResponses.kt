package com.kace.content.api.response

import com.kace.content.domain.model.ContentField
import com.kace.content.domain.model.ContentType
import com.kace.content.domain.model.FieldType
import com.kace.content.domain.model.Validation
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 内容类型响应
 */
@Serializable
data class ContentTypeResponse(
    val id: String,
    val name: String,
    val description: String?,
    val fields: List<ContentFieldResponse>,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromDomain(contentType: ContentType): ContentTypeResponse {
            return ContentTypeResponse(
                id = contentType.id.toString(),
                name = contentType.name,
                description = contentType.description,
                fields = contentType.fields.map { ContentFieldResponse.fromDomain(it) },
                createdAt = contentType.createdAt,
                updatedAt = contentType.updatedAt
            )
        }
    }
}

/**
 * 内容字段响应
 */
@Serializable
data class ContentFieldResponse(
    val id: String,
    val name: String,
    val type: FieldType,
    val required: Boolean,
    val defaultValue: String?,
    val validations: List<Validation>
) {
    companion object {
        fun fromDomain(field: ContentField): ContentFieldResponse {
            return ContentFieldResponse(
                id = field.id.toString(),
                name = field.name,
                type = field.type,
                required = field.required,
                defaultValue = field.defaultValue,
                validations = field.validations
            )
        }
    }
}

/**
 * 分页响应
 */
@Serializable
data class PageResponse<T>(
    val data: List<T>,
    val total: Long,
    val offset: Int,
    val limit: Int
) 