package com.kace.notification.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 渠道提供商枚举
 */
enum class ChannelProvider {
    SMTP,           // 邮件SMTP服务器
    SENDGRID,       // SendGrid邮件服务
    MAILGUN,        // Mailgun邮件服务
    TWILIO,         // Twilio短信服务
    FIREBASE,       // Firebase Cloud Messaging
    CUSTOM          // 自定义渠道
}

/**
 * 通知渠道领域模型
 */
data class NotificationChannel(
    val id: UUID,                         // 渠道ID
    val name: String,                     // 渠道名称
    val type: NotificationType,           // 渠道类型
    val provider: ChannelProvider,        // 渠道提供商
    val config: Map<String, String>,      // 配置参数
    val isDefault: Boolean = false,       // 是否为默认渠道
    val isActive: Boolean = true,         // 是否激活
    val createdAt: Instant,               // 创建时间
    val updatedAt: Instant                // 更新时间
) 