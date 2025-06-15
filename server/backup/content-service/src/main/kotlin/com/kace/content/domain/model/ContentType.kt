package com.kace.content.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * 内容类型
 */
@Serializable
data class ContentType(
    val id: UUID,
    val name: String,
    val description: String?,
    val fields: List<ContentField>,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun create(name: String, description: String?, fields: List<ContentField>): ContentType {
            val now = Instant.now().toEpochMilli()
            return ContentType(
                id = UUID.randomUUID(),
                name = name,
                description = description,
                fields = fields,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    fun update(name: String? = null, description: String? = null, fields: List<ContentField>? = null): ContentType {
        return this.copy(
            name = name ?: this.name,
            description = description ?: this.description,
            fields = fields ?: this.fields,
            updatedAt = Instant.now().toEpochMilli()
        )
    }
}

/**
 * 内容字段
 */
@Serializable
data class ContentField(
    val id: UUID,
    val name: String,
    val type: FieldType,
    val required: Boolean,
    val defaultValue: String?,
    val validations: List<Validation>
) {
    companion object {
        fun create(name: String, type: FieldType, required: Boolean = false, defaultValue: String? = null, validations: List<Validation> = emptyList()): ContentField {
            return ContentField(
                id = UUID.randomUUID(),
                name = name,
                type = type,
                required = required,
                defaultValue = defaultValue,
                validations = validations
            )
        }
    }
}

/**
 * 字段类型
 */
@Serializable
enum class FieldType {
    TEXT,
    RICH_TEXT,
    NUMBER,
    DATE,
    BOOLEAN,
    MEDIA,
    REFERENCE,
    JSON
}

/**
 * 字段验证
 */
@Serializable
data class Validation(
    val type: ValidationType,
    val params: Map<String, String>
)

/**
 * 验证类型
 */
@Serializable
enum class ValidationType {
    REGEX,
    MIN_LENGTH,
    MAX_LENGTH,
    MIN_VALUE,
    MAX_VALUE,
    REQUIRED,
    EMAIL,
    URL
} 