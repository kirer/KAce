package com.kace.notification.domain.service

import com.kace.notification.domain.model.NotificationTemplate
import com.kace.notification.domain.model.TemplateType
import java.util.UUID

/**
 * 通知模板服务接口
 */
interface NotificationTemplateService {
    
    /**
     * 创建通知模板
     */
    fun createTemplate(
        name: String,
        type: TemplateType,
        content: String,
        subject: String? = null,
        description: String? = null,
        variables: List<String> = emptyList(),
        isActive: Boolean = true
    ): NotificationTemplate
    
    /**
     * 更新通知模板
     */
    fun updateTemplate(
        id: UUID,
        name: String? = null,
        type: TemplateType? = null,
        content: String? = null,
        subject: String? = null,
        description: String? = null,
        variables: List<String>? = null,
        isActive: Boolean? = null
    ): NotificationTemplate?
    
    /**
     * 获取通知模板
     */
    fun getTemplate(id: UUID): NotificationTemplate?
    
    /**
     * 根据名称获取通知模板
     */
    fun getTemplateByName(name: String): NotificationTemplate?
    
    /**
     * 获取特定类型的所有模板
     */
    fun getTemplatesByType(type: TemplateType, limit: Int = 20, offset: Int = 0): List<NotificationTemplate>
    
    /**
     * 获取所有活跃的模板
     */
    fun getAllActiveTemplates(limit: Int = 20, offset: Int = 0): List<NotificationTemplate>
    
    /**
     * 获取所有模板
     */
    fun getAllTemplates(limit: Int = 20, offset: Int = 0): List<NotificationTemplate>
    
    /**
     * 启用模板
     */
    fun activateTemplate(id: UUID): Boolean
    
    /**
     * 禁用模板
     */
    fun deactivateTemplate(id: UUID): Boolean
    
    /**
     * 删除模板
     */
    fun deleteTemplate(id: UUID): Boolean
    
    /**
     * 渲染模板
     */
    fun renderTemplate(templateId: UUID, variables: Map<String, Any>): String?
    
    /**
     * 根据名称渲染模板
     */
    fun renderTemplateByName(templateName: String, variables: Map<String, Any>): String?
    
    /**
     * 验证模板变量
     */
    fun validateTemplateVariables(templateId: UUID, variables: Map<String, Any>): Boolean
} 