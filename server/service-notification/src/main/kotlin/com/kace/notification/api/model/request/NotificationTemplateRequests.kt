package com.kace.notification.api.model.request

import kotlinx.serialization.Serializable

/**
 * 创建通知模板请求
 */
@Serializable
data class CreateTemplateRequest(
    val name: String,
    val type: String,
    val content: String,
    val subject: String? = null,
    val description: String? = null,
    val variables: List<String> = emptyList(),
    val isActive: Boolean = true
)

/**
 * 更新通知模板请求
 */
@Serializable
data class UpdateTemplateRequest(
    val name: String? = null,
    val type: String? = null,
    val content: String? = null,
    val subject: String? = null,
    val description: String? = null,
    val variables: List<String>? = null,
    val isActive: Boolean? = null
)

/**
 * 渲染模板请求
 */
@Serializable
data class RenderTemplateRequest(
    val variables: Map<String, String>
) 