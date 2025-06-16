package com.kace.notification.domain.repository

import com.kace.notification.domain.model.NotificationTemplate
import com.kace.notification.domain.model.TemplateType
import java.util.UUID

/**
 * 通知模板仓库接口
 */
interface NotificationTemplateRepository {
    
    /**
     * 创建通知模板
     */
    fun create(template: NotificationTemplate): NotificationTemplate
    
    /**
     * 更新通知模板
     */
    fun update(template: NotificationTemplate): NotificationTemplate
    
    /**
     * 根据ID查找通知模板
     */
    fun findById(id: UUID): NotificationTemplate?
    
    /**
     * 根据名称查找通知模板
     */
    fun findByName(name: String): NotificationTemplate?
    
    /**
     * 根据类型查找通知模板
     */
    fun findByType(type: TemplateType, limit: Int, offset: Int): List<NotificationTemplate>
    
    /**
     * 查找所有活跃的通知模板
     */
    fun findAllActive(limit: Int, offset: Int): List<NotificationTemplate>
    
    /**
     * 查找所有通知模板
     */
    fun findAll(limit: Int, offset: Int): List<NotificationTemplate>
    
    /**
     * 启用通知模板
     */
    fun activate(id: UUID): Boolean
    
    /**
     * 禁用通知模板
     */
    fun deactivate(id: UUID): Boolean
    
    /**
     * 删除通知模板
     */
    fun deleteById(id: UUID): Boolean
    
    /**
     * 统计模板数量
     */
    fun count(): Long
    
    /**
     * 按类型统计模板数量
     */
    fun countByType(type: TemplateType): Long
} 