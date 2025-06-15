package com.kace.notification.domain.repository

import com.kace.notification.domain.model.NotificationChannel
import com.kace.notification.domain.model.NotificationType
import java.util.UUID

/**
 * 通知渠道仓库接口
 */
interface NotificationChannelRepository {
    
    /**
     * 创建通知渠道
     */
    fun create(channel: NotificationChannel): NotificationChannel
    
    /**
     * 更新通知渠道
     */
    fun update(channel: NotificationChannel): NotificationChannel
    
    /**
     * 根据ID查找通知渠道
     */
    fun findById(id: UUID): NotificationChannel?
    
    /**
     * 根据名称查找通知渠道
     */
    fun findByName(name: String): NotificationChannel?
    
    /**
     * 根据类型查找通知渠道
     */
    fun findByType(type: NotificationType): List<NotificationChannel>
    
    /**
     * 查找默认通知渠道
     */
    fun findDefaultByType(type: NotificationType): NotificationChannel?
    
    /**
     * 查找所有活跃的通知渠道
     */
    fun findAllActive(): List<NotificationChannel>
    
    /**
     * 查找所有通知渠道
     */
    fun findAll(): List<NotificationChannel>
    
    /**
     * 设置为默认通知渠道
     */
    fun setAsDefault(id: UUID): Boolean
    
    /**
     * 启用通知渠道
     */
    fun activate(id: UUID): Boolean
    
    /**
     * 禁用通知渠道
     */
    fun deactivate(id: UUID): Boolean
    
    /**
     * 删除通知渠道
     */
    fun deleteById(id: UUID): Boolean
} 