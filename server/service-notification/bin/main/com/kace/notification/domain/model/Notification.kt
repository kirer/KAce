package com.kace.notification.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 通知类型枚举
 */
enum class NotificationType {
    EMAIL,      // 电子邮件
    PUSH,       // 推送通知
    SMS,        // 短信
    IN_APP      // 应用内通知
}

/**
 * 通知状态枚举
 */
enum class NotificationStatus {
    PENDING,    // 待发送
    SENT,       // 已发送
    DELIVERED,  // 已送达
    READ,       // 已读
    FAILED      // 发送失败
}

/**
 * 通知优先级枚举
 */
enum class NotificationPriority {
    LOW,        // 低优先级
    NORMAL,     // 正常优先级
    HIGH,       // 高优先级
    URGENT      // 紧急优先级
}

/**
 * 通知领域模型
 */
data class Notification(
    val id: UUID,                         // 通知ID
    val type: NotificationType,           // 通知类型
    val recipientId: UUID,                // 接收者ID
    val title: String,                    // 通知标题
    val content: String,                  // 通知内容
    val metadata: Map<String, Any>? = null, // 元数据，可包含额外信息
    val status: NotificationStatus,       // 通知状态
    val priority: NotificationPriority,   // 通知优先级
    val sentAt: Instant? = null,          // 发送时间
    val deliveredAt: Instant? = null,     // 送达时间
    val readAt: Instant? = null,          // 阅读时间
    val createdAt: Instant,               // 创建时间
    val updatedAt: Instant                // 更新时间
) 