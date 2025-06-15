package com.kace.content.api.model

import com.kace.content.domain.model.ContentType
import com.kace.content.domain.model.ContentTypeField
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 内容类型字段请求
 */
@Serializable
data class ContentTypeFieldRequest(
    val id: String? = null,
    val name: String,
    val code: String,
    val description: String? = null,
    val type: String,
    val isRequired: Boolean = false,
    val defaultValue: String? = null,
    val validationRules: String? = null,
    val ordering: Int = 0
) {
    fun toDomain(): ContentTypeField {
        return ContentTypeField(
            id = id?.let { UUID.fromString(it) } ?: UUID.randomUUID(),
            name = name,
            code = code,
            description = description,
            type = type,
            isRequired = isRequired,
            defaultValue = defaultValue,
            validationRules = validationRules,
            ordering = ordering
        )
    }
}

/**
 * 内容类型字段响应
 */
@Serializable
data class ContentTypeFieldResponse(
    val id: String,
    val name: String,
    val code: String,
    val description: String? = null,
    val type: String,
    val isRequired: Boolean,
    val defaultValue: String? = null,
    val validationRules: String? = null,
    val ordering: Int
) {
    companion object {
        fun fromDomain(field: ContentTypeField): ContentTypeFieldResponse {
            return ContentTypeFieldResponse(
                id = field.id.toString(),
                name = field.name,
                code = field.code,
                description = field.description,
                type = field.type,
                isRequired = field.isRequired,
                defaultValue = field.defaultValue,
                validationRules = field.validationRules,
                ordering = field.ordering
            )
        }
    }
}

/**
 * 创建内容类型请求
 */
@Serializable
data class CreateContentTypeRequest(
    val code: String,
    val name: String,
    val description: String? = null,
    val fields: List<ContentTypeFieldRequest> = emptyList()
) {
    fun toDomain(createdBy: UUID): ContentType {
        return ContentType(
            id = UUID.randomUUID(),
            code = code,
            name = name,
            description = description,
            createdBy = createdBy,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isSystem = false,
            fields = fields.map { it.toDomain() }
        )
    }
}

/**
 * 更新内容类型请求
 */
@Serializable
data class UpdateContentTypeRequest(
    val code: String,
    val name: String,
    val description: String? = null,
    val fields: List<ContentTypeFieldRequest> = emptyList()
) {
    fun toDomain(id: UUID, existingContentType: ContentType): ContentType {
        return ContentType(
            id = id,
            code = code,
            name = name,
            description = description,
            createdBy = existingContentType.createdBy,
            createdAt = existingContentType.createdAt,
            updatedAt = System.currentTimeMillis(),
            isSystem = existingContentType.isSystem,
            fields = fields.map { it.toDomain() }
        )
    }
}

/**
 * 内容类型响应
 */
@Serializable
data class ContentTypeResponse(
    val id: String,
    val code: String,
    val name: String,
    val description: String? = null,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSystem: Boolean,
    val fields: List<ContentTypeFieldResponse>
) {
    companion object {
        fun fromDomain(contentType: ContentType): ContentTypeResponse {
            return ContentTypeResponse(
                id = contentType.id.toString(),
                code = contentType.code,
                name = contentType.name,
                description = contentType.description,
                createdBy = contentType.createdBy.toString(),
                createdAt = contentType.createdAt,
                updatedAt = contentType.updatedAt,
                isSystem = contentType.isSystem,
                fields = contentType.fields.map { ContentTypeFieldResponse.fromDomain(it) }
            )
        }
    }
}

/**
 * 内容类型列表响应
 */
@Serializable
data class ContentTypeListResponse(
    val items: List<ContentTypeResponse>,
    val total: Long,
    val offset: Int,
    val limit: Int
) 