package com.kace.notification.infrastructure.messaging

import com.kace.notification.domain.model.Notification
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.service.NotificationChannelService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * RabbitMQ通知发送器实现
 */
class RabbitMQNotificationSender(
    private val channelService: NotificationChannelService,
    private val rabbitMQClient: RabbitMQClient
) : NotificationSender {
    
    private val logger = LoggerFactory.getLogger(RabbitMQNotificationSender::class.java)
    
    override fun send(notification: Notification): Boolean {
        try {
            // 根据通知类型获取默认渠道
            val channel = channelService.getDefaultChannelForType(notification.type)
                ?: return logFailure(notification.id, "无可用的默认渠道")
            
            // 根据通知类型选择不同的交换机和路由键
            val (exchange, routingKey) = when (notification.type) {
                NotificationType.EMAIL -> "notification.email" to "email"
                NotificationType.PUSH -> "notification.push" to "push"
                NotificationType.SMS -> "notification.sms" to "sms"
                NotificationType.IN_APP -> "notification.inapp" to "inapp"
            }
            
            // 构造消息
            val message = mapOf(
                "id" to notification.id.toString(),
                "recipientId" to notification.recipientId.toString(),
                "type" to notification.type.name,
                "title" to notification.title,
                "content" to notification.content,
                "metadata" to (notification.metadata ?: emptyMap()),
                "priority" to notification.priority.name,
                "channelId" to channel.id.toString(),
                "channelProvider" to channel.provider.name,
                "channelConfig" to channel.config
            )
            
            // 序列化消息
            val messageJson = Json.encodeToString(message)
            
            // 发送消息
            rabbitMQClient.publish(exchange, routingKey, messageJson)
            
            logger.info("通知已发送到消息队列: id=${notification.id}, type=${notification.type}")
            return true
            
        } catch (e: Exception) {
            return logFailure(notification.id, e.message ?: "未知错误")
        }
    }
    
    private fun logFailure(notificationId: UUID, reason: String): Boolean {
        logger.error("通知发送失败: id=$notificationId, reason=$reason")
        return false
    }
} 