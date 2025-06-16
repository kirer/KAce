package com.kace.notification.domain.service

import com.kace.notification.domain.model.NotificationPreference
import com.kace.notification.domain.model.NotificationType
import java.util.UUID

/**
 * 通知偏好服务接口
 */
interface NotificationPreferenceService {
    
    /**
     * 创建通知偏好
     */
    fun createPreference(
        userId: UUID,
        eventType: String,
        channels: Set<NotificationType>,
        enabled: Boolean = true,
        quietHoursStart: Int? = null,
        quietHoursEnd: Int? = null
    ): NotificationPreference
    
    /**
     * 更新通知偏好
     */
    fun updatePreference(
        id: UUID,
        channels: Set<NotificationType>? = null,
        enabled: Boolean? = null,
        quietHoursStart: Int? = null,
        quietHoursEnd: Int? = null
    ): NotificationPreference?
    
    /**
     * 获取通知偏好
     */
    fun getPreference(id: UUID): NotificationPreference?
    
    /**
     * 获取用户的所有通知偏好
     */
    fun getUserPreferences(userId: UUID): List<NotificationPreference>
    
    /**
     * 获取用户特定事件类型的通知偏好
     */
    fun getUserPreferenceForEventType(userId: UUID, eventType: String): NotificationPreference?
    
    /**
     * 启用通知偏好
     */
    fun enablePreference(id: UUID): Boolean
    
    /**
     * 禁用通知偏好
     */
    fun disablePreference(id: UUID): Boolean
    
    /**
     * 启用用户的所有通知偏好
     */
    fun enableAllUserPreferences(userId: UUID): Int
    
    /**
     * 禁用用户的所有通知偏好
     */
    fun disableAllUserPreferences(userId: UUID): Int
    
    /**
     * 删除通知偏好
     */
    fun deletePreference(id: UUID): Boolean
    
    /**
     * 删除用户的所有通知偏好
     */
    fun deleteAllUserPreferences(userId: UUID): Int
    
    /**
     * 检查用户是否启用了特定事件类型的通知
     */
    fun isEventTypeEnabled(userId: UUID, eventType: String): Boolean
    
    /**
     * 检查当前是否在用户设置的免打扰时段
     */
    fun isQuietHoursActive(userId: UUID): Boolean
    
    /**
     * 获取用户可接收特定事件类型通知的渠道
     */
    fun getEnabledChannelsForEventType(userId: UUID, eventType: String): Set<NotificationType>
} 