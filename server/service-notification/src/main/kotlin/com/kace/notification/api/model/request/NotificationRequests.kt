package com.kace.notification.api.model.request

import com.kace.notification.domain.model.NotificationPriority
import com.kace.notification.domain.model.NotificationType
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 创建通知请求
 */
@Serializable
data class CreateNotificationRequest(
    val recipientId: String,
    val type: String,
    val title: String,
    val content: String,
    val metadata: Map<String, String>? = null,
    val priority: String? = null
)

/**
 * 批量创建通知请求
 */
@Serializable
data class BatchCreateNotificationRequest(
    val notifications: List<CreateNotificationRequest>
)

/**
 * 从模板创建通知请求
 */
@Serializable
data class CreateNotificationFromTemplateRequest(
    val templateName: String,
    val recipientId: String,
    val variables: Map<String, String>,
    val priority: String? = null
)

/**
 * 批量从模板创建通知请求
 */
@Serializable
data class BatchCreateNotificationFromTemplateRequest(
    val templateName: String,
    val recipients: List<RecipientVariables>,
    val priority: String? = null
)

/**
 * 接收者变量
 */
@Serializable
data class RecipientVariables(
    val recipientId: String,
    val variables: Map<String, String>
)

/**
 * 标记通知为已读请求
 */
@Serializable
data class MarkNotificationReadRequest(
    val notificationIds: List<String>
)

/**
 * 删除通知请求
 */
@Serializable
data class DeleteNotificationRequest(
    val notificationIds: List<String>
) 