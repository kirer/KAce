package com.kace.notification.api.model.response

import com.kace.notification.domain.model.NotificationTemplate
import kotlinx.serialization.Serializable

/**
 * 通知模板响应
 */
@Serializable
data class TemplateResponse(
    val id: String,
    val name: String,
    val description: String?,
    val type: String,
    val subject: String?,
    val content: String,
    val variables: List<String>,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * 通知模板列表响应
 */
@Serializable
data class TemplateListResponse(
    val templates: List<TemplateResponse>,
    val total: Long,
    val page: Int,
    val pageSize: Int
)

/**
 * 模板渲染响应
 */
@Serializable
data class TemplateRenderResponse(
    val renderedContent: String
)

/**
 * 模板操作响应
 */
@Serializable
data class TemplateActionResponse(
    val success: Boolean,
    val message: String? = null
)

/**
 * 将领域模型转换为响应模型
 */
fun NotificationTemplate.toResponse(): TemplateResponse {
    return TemplateResponse(
        id = id.toString(),
        name = name,
        description = description,
        type = type.name,
        subject = subject,
        content = content,
        variables = variables,
        isActive = isActive,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
} 