package com.kace.notification.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 模板类型枚举
 */
enum class TemplateType {
    EMAIL_HTML,     // HTML格式的电子邮件
    EMAIL_TEXT,     // 纯文本格式的电子邮件
    PUSH,           // 推送通知
    SMS,            // 短信
    IN_APP          // 应用内通知
}

/**
 * 通知模板领域模型
 */
data class NotificationTemplate(
    val id: UUID,                       // 模板ID
    val name: String,                   // 模板名称
    val description: String? = null,    // 模板描述
    val type: TemplateType,             // 模板类型
    val subject: String? = null,        // 主题（用于邮件）
    val content: String,                // 模板内容
    val variables: List<String>,        // 模板变量列表
    val isActive: Boolean = true,       // 是否激活
    val createdAt: Instant,             // 创建时间
    val updatedAt: Instant              // 更新时间
) 