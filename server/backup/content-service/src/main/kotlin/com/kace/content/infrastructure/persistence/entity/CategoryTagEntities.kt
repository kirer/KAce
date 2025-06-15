package com.kace.content.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID

/**
 * 分类表
 */
object Categories : UUIDTable("categories") {
    val name = varchar("name", 100)
    val slug = varchar("slug", 100).uniqueIndex()
    val description = text("description").nullable()
    val parentId = uuid("parent_id").nullable()
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 标签表
 */
object Tags : UUIDTable("tags") {
    val name = varchar("name", 100)
    val slug = varchar("slug", 100).uniqueIndex()
    val description = text("description").nullable()
    val createdBy = uuid("created_by")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 内容分类关联表
 */
object ContentCategories : UUIDTable("content_categories") {
    val contentId = reference("content_id", Contents, onDelete = ReferenceOption.CASCADE)
    val categoryId = reference("category_id", Categories, onDelete = ReferenceOption.CASCADE)
    
    init {
        uniqueIndex("content_category_unique_idx", contentId, categoryId)
    }
}

/**
 * 内容标签关联表
 */
object ContentTags : UUIDTable("content_tags") {
    val contentId = reference("content_id", Contents, onDelete = ReferenceOption.CASCADE)
    val tagId = reference("tag_id", Tags, onDelete = ReferenceOption.CASCADE)
    
    init {
        uniqueIndex("content_tag_unique_idx", contentId, tagId)
    }
}

/**
 * 分类实体
 */
class CategoryEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryEntity>(Categories)
    
    var name by Categories.name
    var slug by Categories.slug
    var description by Categories.description
    var parentId by Categories.parentId
    var createdBy by Categories.createdBy
    var createdAt by Categories.createdAt
    var updatedAt by Categories.updatedAt
    
    val contents by ContentEntity via ContentCategories
}

/**
 * 标签实体
 */
class TagEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TagEntity>(Tags)
    
    var name by Tags.name
    var slug by Tags.slug
    var description by Tags.description
    var createdBy by Tags.createdBy
    var createdAt by Tags.createdAt
    var updatedAt by Tags.updatedAt
    
    val contents by ContentEntity via ContentTags
} 