package com.kace.notification.api.model.response

import com.kace.notification.domain.model.NotificationPreference
import kotlinx.serialization.Serializable

/**
 * 通知偏好响应
 */
@Serializable
data class PreferenceResponse(
    val id: String,
    val userId: String,
    val eventType: String,
    val channels: List<String>,
    val enabled: Boolean,
    val quietHoursStart: Int?,
    val quietHoursEnd: Int?,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * 通知偏好列表响应
 */
@Serializable
data class PreferenceListResponse(
    val preferences: List<PreferenceResponse>
)

/**
 * 通知偏好操作响应
 */
@Serializable
data class PreferenceActionResponse(
    val success: Boolean,
    val message: String? = null,
    val affectedCount: Int = 0
)

/**
 * 将领域模型转换为响应模型
 */
fun NotificationPreference.toResponse(): PreferenceResponse {
    return PreferenceResponse(
        id = id.toString(),
        userId = userId.toString(),
        eventType = eventType,
        channels = channels.map { it.name },
        enabled = enabled,
        quietHoursStart = quietHoursStart,
        quietHoursEnd = quietHoursEnd,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
} 