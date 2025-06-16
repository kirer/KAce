package com.kace.notification.infrastructure.persistence.repository

import com.kace.notification.domain.model.NotificationTemplate
import com.kace.notification.domain.model.TemplateType
import com.kace.notification.domain.repository.NotificationTemplateRepository
import com.kace.notification.infrastructure.persistence.entity.NotificationTemplateEntities
import com.kace.notification.infrastructure.persistence.entity.NotificationTemplateEntity
import com.kace.notification.infrastructure.persistence.entity.toEntity
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * 通知模板仓库实现类
 */
class NotificationTemplateRepositoryImpl : NotificationTemplateRepository {
    
    override fun create(template: NotificationTemplate): NotificationTemplate = transaction {
        template.toEntity().toDomain()
    }
    
    override fun update(template: NotificationTemplate): NotificationTemplate = transaction {
        val entity = NotificationTemplateEntity[template.id]
        entity.name = template.name
        entity.description = template.description
        entity.type = template.type
        entity.subject = template.subject
        entity.content = template.content
        entity.variables = template.variables
        entity.isActive = template.isActive
        entity.updatedAt = template.updatedAt
        entity.toDomain()
    }
    
    override fun findById(id: UUID): NotificationTemplate? = transaction {
        NotificationTemplateEntity.findById(id)?.toDomain()
    }
    
    override fun findByName(name: String): NotificationTemplate? = transaction {
        NotificationTemplateEntity.find { NotificationTemplateEntities.name eq name }
            .firstOrNull()?.toDomain()
    }
    
    override fun findByType(type: TemplateType, limit: Int, offset: Int): List<NotificationTemplate> = transaction {
        NotificationTemplateEntity.find { NotificationTemplateEntities.type eq type }
            .orderBy(NotificationTemplateEntities.name to SortOrder.ASC)
            .limit(limit, offset.toLong())
            .map { it.toDomain() }
    }
    
    override fun findAllActive(limit: Int, offset: Int): List<NotificationTemplate> = transaction {
        NotificationTemplateEntity.find { NotificationTemplateEntities.isActive eq true }
            .orderBy(NotificationTemplateEntities.name to SortOrder.ASC)
            .limit(limit, offset.toLong())
            .map { it.toDomain() }
    }
    
    override fun findAll(limit: Int, offset: Int): List<NotificationTemplate> = transaction {
        NotificationTemplateEntity.all()
            .orderBy(NotificationTemplateEntities.name to SortOrder.ASC)
            .limit(limit, offset.toLong())
            .map { it.toDomain() }
    }
    
    override fun activate(id: UUID): Boolean = transaction {
        val entity = NotificationTemplateEntity.findById(id) ?: return@transaction false
        entity.isActive = true
        true
    }
    
    override fun deactivate(id: UUID): Boolean = transaction {
        val entity = NotificationTemplateEntity.findById(id) ?: return@transaction false
        entity.isActive = false
        true
    }
    
    override fun deleteById(id: UUID): Boolean = transaction {
        NotificationTemplateEntity.findById(id)?.delete() != null
    }
    
    override fun count(): Long = transaction {
        NotificationTemplateEntity.count()
    }
    
    override fun countByType(type: TemplateType): Long = transaction {
        NotificationTemplateEntity.find { NotificationTemplateEntities.type eq type }.count()
    }
} 