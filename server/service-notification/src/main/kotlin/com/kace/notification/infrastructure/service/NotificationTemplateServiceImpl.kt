package com.kace.notification.infrastructure.service

import com.kace.notification.domain.model.NotificationTemplate
import com.kace.notification.domain.model.TemplateType
import com.kace.notification.domain.repository.NotificationTemplateRepository
import com.kace.notification.domain.service.NotificationTemplateService
import com.kace.notification.infrastructure.template.TemplateEngine
import java.time.Instant
import java.util.UUID

/**
 * 通知模板服务实现类
 */
class NotificationTemplateServiceImpl(
    private val templateRepository: NotificationTemplateRepository,
    private val templateEngine: TemplateEngine
) : NotificationTemplateService {
    
    override fun createTemplate(
        name: String,
        type: TemplateType,
        content: String,
        subject: String?,
        description: String?,
        variables: List<String>,
        isActive: Boolean
    ): NotificationTemplate {
        val now = Instant.now()
        val template = NotificationTemplate(
            id = UUID.randomUUID(),
            name = name,
            type = type,
            content = content,
            subject = subject,
            description = description,
            variables = variables,
            isActive = isActive,
            createdAt = now,
            updatedAt = now
        )
        
        return templateRepository.create(template)
    }
    
    override fun updateTemplate(
        id: UUID,
        name: String?,
        type: TemplateType?,
        content: String?,
        subject: String?,
        description: String?,
        variables: List<String>?,
        isActive: Boolean?
    ): NotificationTemplate? {
        val existingTemplate = templateRepository.findById(id) ?: return null
        
        val updatedTemplate = existingTemplate.copy(
            name = name ?: existingTemplate.name,
            type = type ?: existingTemplate.type,
            content = content ?: existingTemplate.content,
            subject = subject ?: existingTemplate.subject,
            description = description ?: existingTemplate.description,
            variables = variables ?: existingTemplate.variables,
            isActive = isActive ?: existingTemplate.isActive,
            updatedAt = Instant.now()
        )
        
        return templateRepository.update(updatedTemplate)
    }
    
    override fun getTemplate(id: UUID): NotificationTemplate? {
        return templateRepository.findById(id)
    }
    
    override fun getTemplateByName(name: String): NotificationTemplate? {
        return templateRepository.findByName(name)
    }
    
    override fun getTemplatesByType(type: TemplateType, limit: Int, offset: Int): List<NotificationTemplate> {
        return templateRepository.findByType(type, limit, offset)
    }
    
    override fun getAllActiveTemplates(limit: Int, offset: Int): List<NotificationTemplate> {
        return templateRepository.findAllActive(limit, offset)
    }
    
    override fun getAllTemplates(limit: Int, offset: Int): List<NotificationTemplate> {
        return templateRepository.findAll(limit, offset)
    }
    
    override fun activateTemplate(id: UUID): Boolean {
        return templateRepository.activate(id)
    }
    
    override fun deactivateTemplate(id: UUID): Boolean {
        return templateRepository.deactivate(id)
    }
    
    override fun deleteTemplate(id: UUID): Boolean {
        return templateRepository.deleteById(id)
    }
    
    override fun renderTemplate(templateId: UUID, variables: Map<String, Any>): String? {
        val template = templateRepository.findById(templateId) ?: return null
        return templateEngine.render(template.content, variables)
    }
    
    override fun renderTemplateByName(templateName: String, variables: Map<String, Any>): String? {
        val template = templateRepository.findByName(templateName) ?: return null
        return templateEngine.render(template.content, variables)
    }
    
    override fun validateTemplateVariables(templateId: UUID, variables: Map<String, Any>): Boolean {
        val template = templateRepository.findById(templateId) ?: return false
        
        // 检查模板所需的所有变量是否都提供了
        return template.variables.all { requiredVar ->
            variables.containsKey(requiredVar)
        }
    }
} 