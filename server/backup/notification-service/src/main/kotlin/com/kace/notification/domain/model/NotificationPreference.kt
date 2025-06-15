package com.kace.notification.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 通知偏好领域模型
 */
data class NotificationPreference(
    val id: UUID,                                    // 偏好ID
    val userId: UUID,                                // 用户ID
    val eventType: String,                           // 事件类型
    val channels: Set<NotificationType>,             // 启用的通知渠道
    val enabled: Boolean = true,                     // 是否启用此类通知
    val quietHoursStart: Int? = null,                // 免打扰时段开始（小时，0-23）
    val quietHoursEnd: Int? = null,                  // 免打扰时段结束（小时，0-23）
    val createdAt: Instant,                          // 创建时间
    val updatedAt: Instant                           // 更新时间
) 