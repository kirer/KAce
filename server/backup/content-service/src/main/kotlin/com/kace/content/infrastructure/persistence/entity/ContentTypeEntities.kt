package com.kace.content.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.UUID

/**
 * 内容类型表
 */
object ContentTypes : UUIDTable("content_types") {
    val code = varchar("code", 50).uniqueIndex()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val isSystem = bool("is_system").default(false)
}

/**
 * 内容类型字段表
 */
object ContentTypeFields : UUIDTable("content_type_fields") {
    val contentTypeId = reference("content_type_id", ContentTypes)
    val name = varchar("name", 50)
    val code = varchar("code", 50)
    val description = text("description").nullable()
    val fieldType = varchar("field_type", 50)
    val isRequired = bool("is_required").default(false)
    val defaultValue = text("default_value").nullable()
    val validationRules = text("validation_rules").nullable()
    val ordering = integer("ordering").default(0)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    init {
        uniqueIndex("content_type_field_unique_idx", contentTypeId, code)
    }
}

/**
 * 内容类型实体
 */
class ContentTypeEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentTypeEntity>(ContentTypes)
    
    var code by ContentTypes.code
    var name by ContentTypes.name
    var description by ContentTypes.description
    var createdBy by ContentTypes.createdBy
    var createdAt by ContentTypes.createdAt
    var updatedAt by ContentTypes.updatedAt
    var isSystem by ContentTypes.isSystem
    
    val fields by ContentTypeFieldEntity referrersOn ContentTypeFields.contentTypeId
}

/**
 * 内容类型字段实体
 */
class ContentTypeFieldEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentTypeFieldEntity>(ContentTypeFields)
    
    var contentType by ContentTypeEntity referencedOn ContentTypeFields.contentTypeId
    var name by ContentTypeFields.name
    var code by ContentTypeFields.code
    var description by ContentTypeFields.description
    var fieldType by ContentTypeFields.fieldType
    var isRequired by ContentTypeFields.isRequired
    var defaultValue by ContentTypeFields.defaultValue
    var validationRules by ContentTypeFields.validationRules
    var ordering by ContentTypeFields.ordering
    var createdAt by ContentTypeFields.createdAt
    var updatedAt by ContentTypeFields.updatedAt
} 