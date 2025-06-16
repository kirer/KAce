package com.kace.notification.domain.repository

import com.kace.notification.domain.model.Notification
import com.kace.notification.domain.model.NotificationStatus
import com.kace.notification.domain.model.NotificationType
import java.time.Instant
import java.util.UUID

/**
 * 通知仓库接口
 */
interface NotificationRepository {
    
    /**
     * 创建通知
     */
    fun create(notification: Notification): Notification
    
    /**
     * 更新通知
     */
    fun update(notification: Notification): Notification
    
    /**
     * 根据ID查找通知
     */
    fun findById(id: UUID): Notification?
    
    /**
     * 根据接收者ID查找通知
     */
    fun findByRecipientId(recipientId: UUID, limit: Int, offset: Int): List<Notification>
    
    /**
     * 根据状态查找通知
     */
    fun findByStatus(status: NotificationStatus, limit: Int, offset: Int): List<Notification>
    
    /**
     * 根据类型查找通知
     */
    fun findByType(type: NotificationType, limit: Int, offset: Int): List<Notification>
    
    /**
     * 根据接收者ID和状态查找通知
     */
    fun findByRecipientIdAndStatus(recipientId: UUID, status: NotificationStatus, limit: Int, offset: Int): List<Notification>
    
    /**
     * 根据接收者ID和类型查找通知
     */
    fun findByRecipientIdAndType(recipientId: UUID, type: NotificationType, limit: Int, offset: Int): List<Notification>
    
    /**
     * 根据时间范围查找通知
     */
    fun findByTimeRange(startTime: Instant, endTime: Instant, limit: Int, offset: Int): List<Notification>
    
    /**
     * 更新通知状态
     */
    fun updateStatus(id: UUID, status: NotificationStatus, timestamp: Instant): Boolean
    
    /**
     * 标记通知为已读
     */
    fun markAsRead(id: UUID): Boolean
    
    /**
     * 批量标记通知为已读
     */
    fun markAsRead(ids: List<UUID>): Int
    
    /**
     * 标记接收者的所有通知为已读
     */
    fun markAllAsRead(recipientId: UUID): Int
    
    /**
     * 删除通知
     */
    fun deleteById(id: UUID): Boolean
    
    /**
     * 删除接收者的所有通知
     */
    fun deleteByRecipientId(recipientId: UUID): Int
    
    /**
     * 删除过期通知
     */
    fun deleteOlderThan(timestamp: Instant): Int
    
    /**
     * 统计接收者的未读通知数量
     */
    fun countUnreadByRecipientId(recipientId: UUID): Long
} 