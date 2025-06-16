package com.kace.notification.domain.service

import com.kace.notification.domain.model.Notification
import com.kace.notification.domain.model.NotificationPriority
import com.kace.notification.domain.model.NotificationStatus
import com.kace.notification.domain.model.NotificationType
import java.time.Instant
import java.util.UUID

/**
 * 通知服务接口
 */
interface NotificationService {
    
    /**
     * 创建通知
     */
    fun createNotification(
        recipientId: UUID,
        type: NotificationType,
        title: String,
        content: String,
        metadata: Map<String, Any>? = null,
        priority: NotificationPriority = NotificationPriority.NORMAL
    ): Notification
    
    /**
     * 发送通知
     */
    fun sendNotification(notification: Notification): Boolean
    
    /**
     * 发送通知（根据ID）
     */
    fun sendNotification(notificationId: UUID): Boolean
    
    /**
     * 批量发送通知
     */
    fun sendNotifications(notifications: List<Notification>): Map<UUID, Boolean>
    
    /**
     * 根据模板创建并发送通知
     */
    fun createAndSendFromTemplate(
        templateName: String,
        recipientId: UUID,
        variables: Map<String, Any>,
        priority: NotificationPriority = NotificationPriority.NORMAL
    ): Notification?
    
    /**
     * 获取通知
     */
    fun getNotification(id: UUID): Notification?
    
    /**
     * 获取用户的通知列表
     */
    fun getUserNotifications(
        userId: UUID,
        limit: Int = 20,
        offset: Int = 0,
        type: NotificationType? = null,
        status: NotificationStatus? = null
    ): List<Notification>
    
    /**
     * 标记通知为已读
     */
    fun markAsRead(id: UUID): Boolean
    
    /**
     * 批量标记通知为已读
     */
    fun markAsRead(ids: List<UUID>): Int
    
    /**
     * 标记用户所有通知为已读
     */
    fun markAllAsRead(userId: UUID): Int
    
    /**
     * 获取用户未读通知数量
     */
    fun getUnreadCount(userId: UUID): Long
    
    /**
     * 删除通知
     */
    fun deleteNotification(id: UUID): Boolean
    
    /**
     * 批量删除通知
     */
    fun deleteNotifications(ids: List<UUID>): Int
    
    /**
     * 删除用户所有通知
     */
    fun deleteAllUserNotifications(userId: UUID): Int
    
    /**
     * 清理过期通知
     */
    fun cleanupExpiredNotifications(olderThan: Instant): Int
}