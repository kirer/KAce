package com.kace.notification.infrastructure.persistence.entity

import com.kace.notification.domain.model.NotificationTemplate
import com.kace.notification.domain.model.TemplateType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb
import java.time.Instant
import java.util.UUID

/**
 * 通知模板数据库表
 */
object NotificationTemplateEntities : UUIDTable("notification_templates") {
    val name = varchar("name", 100).uniqueIndex()
    val description = text("description").nullable()
    val type = enumerationByName("type", 20, TemplateType::class)
    val subject = varchar("subject", 255).nullable()
    val content = text("content")
    val variables = jsonb("variables", defaultValue = "[]")
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 通知模板实体类
 */
class NotificationTemplateEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<NotificationTemplateEntity>(NotificationTemplateEntities)
    
    var name by NotificationTemplateEntities.name
    var description by NotificationTemplateEntities.description
    var type by NotificationTemplateEntities.type
    var subject by NotificationTemplateEntities.subject
    var content by NotificationTemplateEntities.content
    var variables by NotificationTemplateEntities.variables
    var isActive by NotificationTemplateEntities.isActive
    var createdAt by NotificationTemplateEntities.createdAt
    var updatedAt by NotificationTemplateEntities.updatedAt
    
    /**
     * 转换为领域模型
     */
    fun toDomain(): NotificationTemplate = NotificationTemplate(
        id = id.value,
        name = name,
        description = description,
        type = type,
        subject = subject,
        content = content,
        variables = (variables as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * 扩展函数：领域模型转换为实体
 */
fun NotificationTemplate.toEntity(): NotificationTemplateEntity = NotificationTemplateEntity.new(id) {
    this.name = this@toEntity.name
    this.description = this@toEntity.description
    this.type = this@toEntity.type
    this.subject = this@toEntity.subject
    this.content = this@toEntity.content
    this.variables = this@toEntity.variables
    this.isActive = this@toEntity.isActive
    this.createdAt = this@toEntity.createdAt
    this.updatedAt = this@toEntity.updatedAt
} 