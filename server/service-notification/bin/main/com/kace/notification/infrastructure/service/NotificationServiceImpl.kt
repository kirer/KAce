package com.kace.notification.infrastructure.service

import com.kace.notification.domain.model.Notification
import com.kace.notification.domain.model.NotificationPriority
import com.kace.notification.domain.model.NotificationStatus
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.repository.NotificationRepository
import com.kace.notification.domain.service.NotificationPreferenceService
import com.kace.notification.domain.service.NotificationService
import com.kace.notification.domain.service.NotificationTemplateService
import com.kace.notification.infrastructure.messaging.NotificationSender
import java.time.Instant
import java.util.UUID

/**
 * 通知服务实现类
 */
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val templateService: NotificationTemplateService,
    private val preferenceService: NotificationPreferenceService,
    private val notificationSender: NotificationSender
) : NotificationService {
    
    override fun createNotification(
        recipientId: UUID,
        type: NotificationType,
        title: String,
        content: String,
        metadata: Map<String, Any>?,
        priority: NotificationPriority
    ): Notification {
        val now = Instant.now()
        val notification = Notification(
            id = UUID.randomUUID(),
            type = type,
            recipientId = recipientId,
            title = title,
            content = content,
            metadata = metadata,
            status = NotificationStatus.PENDING,
            priority = priority,
            createdAt = now,
            updatedAt = now
        )
        
        return notificationRepository.create(notification)
    }
    
    override fun sendNotification(notification: Notification): Boolean {
        // 检查用户是否启用了此类型的通知
        if (!preferenceService.isEventTypeEnabled(notification.recipientId, notification.type.name)) {
            return false
        }
        
        // 检查是否在免打扰时段
        if (preferenceService.isQuietHoursActive(notification.recipientId) && 
            notification.priority != NotificationPriority.URGENT) {
            return false
        }
        
        // 发送通知
        val success = notificationSender.send(notification)
        
        // 更新通知状态
        if (success) {
            notificationRepository.updateStatus(
                notification.id, 
                NotificationStatus.SENT, 
                Instant.now()
            )
        } else {
            notificationRepository.updateStatus(
                notification.id, 
                NotificationStatus.FAILED, 
                Instant.now()
            )
        }
        
        return success
    }
    
    override fun sendNotification(notificationId: UUID): Boolean {
        val notification = notificationRepository.findById(notificationId) ?: return false
        return sendNotification(notification)
    }
    
    override fun sendNotifications(notifications: List<Notification>): Map<UUID, Boolean> {
        return notifications.associate { notification ->
            notification.id to sendNotification(notification)
        }
    }
    
    override fun createAndSendFromTemplate(
        templateName: String,
        recipientId: UUID,
        variables: Map<String, Any>,
        priority: NotificationPriority
    ): Notification? {
        // 获取模板
        val template = templateService.getTemplateByName(templateName) ?: return null
        
        // 验证变量
        if (!templateService.validateTemplateVariables(template.id, variables)) {
            return null
        }
        
        // 渲染模板
        val content = templateService.renderTemplateByName(templateName, variables) ?: return null
        
        // 确定标题
        val title = when (template.type) {
            com.kace.notification.domain.model.TemplateType.EMAIL_HTML, 
            com.kace.notification.domain.model.TemplateType.EMAIL_TEXT -> template.subject ?: "通知"
            else -> variables["title"]?.toString() ?: template.name
        }
        
        // 确定通知类型
        val notificationType = when (template.type) {
            com.kace.notification.domain.model.TemplateType.EMAIL_HTML, 
            com.kace.notification.domain.model.TemplateType.EMAIL_TEXT -> NotificationType.EMAIL
            com.kace.notification.domain.model.TemplateType.PUSH -> NotificationType.PUSH
            com.kace.notification.domain.model.TemplateType.SMS -> NotificationType.SMS
            com.kace.notification.domain.model.TemplateType.IN_APP -> NotificationType.IN_APP
        }
        
        // 创建通知
        val notification = createNotification(
            recipientId = recipientId,
            type = notificationType,
            title = title,
            content = content,
            metadata = mapOf("templateId" to template.id, "templateName" to templateName),
            priority = priority
        )
        
        // 发送通知
        sendNotification(notification)
        
        return notification
    }
    
    override fun getNotification(id: UUID): Notification? {
        return notificationRepository.findById(id)
    }
    
    override fun getUserNotifications(
        userId: UUID,
        limit: Int,
        offset: Int,
        type: NotificationType?,
        status: NotificationStatus?
    ): List<Notification> {
        return when {
            type != null && status != null -> {
                // 按类型和状态过滤
                notificationRepository.findByRecipientId(userId, limit, offset)
                    .filter { it.type == type && it.status == status }
            }
            type != null -> {
                // 只按类型过滤
                notificationRepository.findByRecipientIdAndType(userId, type, limit, offset)
            }
            status != null -> {
                // 只按状态过滤
                notificationRepository.findByRecipientIdAndStatus(userId, status, limit, offset)
            }
            else -> {
                // 不过滤
                notificationRepository.findByRecipientId(userId, limit, offset)
            }
        }
    }
    
    override fun markAsRead(id: UUID): Boolean {
        return notificationRepository.markAsRead(id)
    }
    
    override fun markAsRead(ids: List<UUID>): Int {
        return notificationRepository.markAsRead(ids)
    }
    
    override fun markAllAsRead(userId: UUID): Int {
        return notificationRepository.markAllAsRead(userId)
    }
    
    override fun getUnreadCount(userId: UUID): Long {
        return notificationRepository.countUnreadByRecipientId(userId)
    }
    
    override fun deleteNotification(id: UUID): Boolean {
        return notificationRepository.deleteById(id)
    }
    
    override fun deleteNotifications(ids: List<UUID>): Int {
        var count = 0
        ids.forEach { id ->
            if (notificationRepository.deleteById(id)) {
                count++
            }
        }
        return count
    }
    
    override fun deleteAllUserNotifications(userId: UUID): Int {
        return notificationRepository.deleteByRecipientId(userId)
    }
    
    override fun cleanupExpiredNotifications(olderThan: Instant): Int {
        return notificationRepository.deleteOlderThan(olderThan)
    }
} 