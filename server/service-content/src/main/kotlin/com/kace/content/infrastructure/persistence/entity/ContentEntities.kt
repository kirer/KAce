package com.kace.content.infrastructure.persistence.entity

import com.kace.content.domain.model.ContentStatus
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.UUID

/**
 * 内容表
 */
object Contents : UUIDTable("contents") {
    val contentTypeId = reference("content_type_id", ContentTypes)
    val title = varchar("title", 255)
    val slug = varchar("slug", 255).uniqueIndex()
    val status = enumerationByName("status", 20, ContentStatus::class)
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val publishedAt = timestamp("published_at").nullable()
    val version = integer("version").default(1)
    val languageCode = varchar("language_code", 10)
}

/**
 * 内容字段表
 */
object ContentFields : UUIDTable("content_fields") {
    val contentId = reference("content_id", Contents)
    val fieldCode = varchar("field_code", 50)
    val fieldValue = text("field_value")
}

/**
 * 内容版本表
 */
object ContentVersions : UUIDTable("content_versions") {
    val contentId = reference("content_id", Contents)
    val version = integer("version")
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val comment = text("comment").nullable()
    
    init {
        uniqueIndex("content_version_unique_idx", contentId, version)
    }
}

/**
 * 内容版本字段表
 */
object ContentVersionFields : UUIDTable("content_version_fields") {
    val contentVersionId = reference("content_version_id", ContentVersions)
    val fieldCode = varchar("field_code", 50)
    val fieldValue = text("field_value")
}

/**
 * 内容实体
 */
class ContentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentEntity>(Contents)
    
    var contentType by ContentTypeEntity referencedOn Contents.contentTypeId
    var title by Contents.title
    var slug by Contents.slug
    var status by Contents.status
    var createdBy by Contents.createdBy
    var createdAt by Contents.createdAt
    var updatedAt by Contents.updatedAt
    var publishedAt by Contents.publishedAt
    var version by Contents.version
    var languageCode by Contents.languageCode
    
    val fields by ContentFieldEntity referrersOn ContentFields.contentId
    val versions by ContentVersionEntity referrersOn ContentVersions.contentId
}

/**
 * 内容字段实体
 */
class ContentFieldEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentFieldEntity>(ContentFields)
    
    var content by ContentEntity referencedOn ContentFields.contentId
    var fieldCode by ContentFields.fieldCode
    var fieldValue by ContentFields.fieldValue
}

/**
 * 内容版本实体
 */
class ContentVersionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentVersionEntity>(ContentVersions)
    
    var content by ContentEntity referencedOn ContentVersions.contentId
    var version by ContentVersions.version
    var createdBy by ContentVersions.createdBy
    var createdAt by ContentVersions.createdAt
    var comment by ContentVersions.comment
    
    val fields by ContentVersionFieldEntity referrersOn ContentVersionFields.contentVersionId
}

/**
 * 内容版本字段实体
 */
class ContentVersionFieldEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentVersionFieldEntity>(ContentVersionFields)
    
    var contentVersion by ContentVersionEntity referencedOn ContentVersionFields.contentVersionId
    var fieldCode by ContentVersionFields.fieldCode
    var fieldValue by ContentVersionFields.fieldValue
} 