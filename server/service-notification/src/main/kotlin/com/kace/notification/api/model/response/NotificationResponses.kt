package com.kace.notification.api.model.response

import com.kace.notification.domain.model.Notification
import kotlinx.serialization.Serializable

/**
 * 通知响应
 */
@Serializable
data class NotificationResponse(
    val id: String,
    val type: String,
    val recipientId: String,
    val title: String,
    val content: String,
    val metadata: Map<String, String>?,
    val status: String,
    val priority: String,
    val createdAt: Long,
    val updatedAt: Long,
    val sentAt: Long?,
    val deliveredAt: Long?,
    val readAt: Long?
)

/**
 * 通知列表响应
 */
@Serializable
data class NotificationListResponse(
    val notifications: List<NotificationResponse>,
    val total: Long,
    val page: Int,
    val pageSize: Int
)

/**
 * 通知计数响应
 */
@Serializable
data class NotificationCountResponse(
    val total: Long,
    val unread: Long
)

/**
 * 通知创建响应
 */
@Serializable
data class NotificationCreatedResponse(
    val id: String,
    val status: String
)

/**
 * 批量通知创建响应
 */
@Serializable
data class BatchNotificationCreatedResponse(
    val notifications: List<NotificationCreatedResponse>,
    val successCount: Int,
    val failureCount: Int
)

/**
 * 通知操作响应
 */
@Serializable
data class NotificationActionResponse(
    val success: Boolean,
    val message: String? = null,
    val affectedCount: Int = 0
)

/**
 * 将领域模型转换为响应模型
 */
fun Notification.toResponse(): NotificationResponse {
    return NotificationResponse(
        id = id.toString(),
        type = type.name,
        recipientId = recipientId.toString(),
        title = title,
        content = content,
        metadata = metadata?.mapValues { it.value.toString() },
        status = status.name,
        priority = priority.name,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        sentAt = sentAt?.toEpochMilli(),
        deliveredAt = deliveredAt?.toEpochMilli(),
        readAt = readAt?.toEpochMilli()
    )
} 