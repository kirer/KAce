package com.kace.notification.domain.repository

import com.kace.notification.domain.model.NotificationPreference
import java.util.UUID

/**
 * 通知偏好仓库接口
 */
interface NotificationPreferenceRepository {
    
    /**
     * 创建通知偏好
     */
    fun create(preference: NotificationPreference): NotificationPreference
    
    /**
     * 更新通知偏好
     */
    fun update(preference: NotificationPreference): NotificationPreference
    
    /**
     * 根据ID查找通知偏好
     */
    fun findById(id: UUID): NotificationPreference?
    
    /**
     * 根据用户ID查找所有通知偏好
     */
    fun findByUserId(userId: UUID): List<NotificationPreference>
    
    /**
     * 根据用户ID和事件类型查找通知偏好
     */
    fun findByUserIdAndEventType(userId: UUID, eventType: String): NotificationPreference?
    
    /**
     * 启用通知偏好
     */
    fun enable(id: UUID): Boolean
    
    /**
     * 禁用通知偏好
     */
    fun disable(id: UUID): Boolean
    
    /**
     * 批量启用用户的通知偏好
     */
    fun enableAllForUser(userId: UUID): Int
    
    /**
     * 批量禁用用户的通知偏好
     */
    fun disableAllForUser(userId: UUID): Int
    
    /**
     * 删除通知偏好
     */
    fun deleteById(id: UUID): Boolean
    
    /**
     * 删除用户的所有通知偏好
     */
    fun deleteByUserId(userId: UUID): Int
    
    /**
     * 检查用户是否启用了特定事件类型的通知
     */
    fun isEventTypeEnabled(userId: UUID, eventType: String): Boolean
} 