package com.kace.notification.api.model.request

import kotlinx.serialization.Serializable

/**
 * 创建通知偏好请求
 */
@Serializable
data class CreatePreferenceRequest(
    val eventType: String,
    val channels: List<String>,
    val enabled: Boolean = true,
    val quietHoursStart: Int? = null,
    val quietHoursEnd: Int? = null
)

/**
 * 更新通知偏好请求
 */
@Serializable
data class UpdatePreferenceRequest(
    val channels: List<String>? = null,
    val enabled: Boolean? = null,
    val quietHoursStart: Int? = null,
    val quietHoursEnd: Int? = null
)

/**
 * 批量创建通知偏好请求
 */
@Serializable
data class BatchCreatePreferenceRequest(
    val preferences: List<CreatePreferenceRequest>
) 