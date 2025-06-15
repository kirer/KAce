package com.kace.notification.domain.service

import com.kace.notification.domain.model.ChannelProvider
import com.kace.notification.domain.model.NotificationChannel
import com.kace.notification.domain.model.NotificationType
import java.util.UUID

/**
 * 通知渠道服务接口
 */
interface NotificationChannelService {
    
    /**
     * 创建通知渠道
     */
    fun createChannel(
        name: String,
        type: NotificationType,
        provider: ChannelProvider,
        config: Map<String, String>,
        isDefault: Boolean = false,
        isActive: Boolean = true
    ): NotificationChannel
    
    /**
     * 更新通知渠道
     */
    fun updateChannel(
        id: UUID,
        name: String? = null,
        config: Map<String, String>? = null,
        isDefault: Boolean? = null,
        isActive: Boolean? = null
    ): NotificationChannel?
    
    /**
     * 获取通知渠道
     */
    fun getChannel(id: UUID): NotificationChannel?
    
    /**
     * 根据名称获取通知渠道
     */
    fun getChannelByName(name: String): NotificationChannel?
    
    /**
     * 获取特定类型的所有通知渠道
     */
    fun getChannelsByType(type: NotificationType): List<NotificationChannel>
    
    /**
     * 获取特定类型的默认通知渠道
     */
    fun getDefaultChannelForType(type: NotificationType): NotificationChannel?
    
    /**
     * 获取所有活跃的通知渠道
     */
    fun getAllActiveChannels(): List<NotificationChannel>
    
    /**
     * 获取所有通知渠道
     */
    fun getAllChannels(): List<NotificationChannel>
    
    /**
     * 设置为默认通知渠道
     */
    fun setAsDefault(id: UUID): Boolean
    
    /**
     * 启用通知渠道
     */
    fun activateChannel(id: UUID): Boolean
    
    /**
     * 禁用通知渠道
     */
    fun deactivateChannel(id: UUID): Boolean
    
    /**
     * 删除通知渠道
     */
    fun deleteChannel(id: UUID): Boolean
    
    /**
     * 测试通知渠道
     */
    fun testChannel(id: UUID, testPayload: String? = null): Boolean
    
    /**
     * 验证渠道配置
     */
    fun validateChannelConfig(type: NotificationType, provider: ChannelProvider, config: Map<String, String>): Boolean
} 