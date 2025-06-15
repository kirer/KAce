package com.kace.content.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.json.jsonb
import java.util.UUID

/**
 * 内容关联表
 */
object ContentRelationTable : UUIDTable("content_relations") {
    /**
     * 源内容ID
     */
    val sourceContentId = uuid("source_content_id").index()
    
    /**
     * 目标内容ID
     */
    val targetContentId = uuid("target_content_id").index()
    
    /**
     * 关联类型
     */
    val type = varchar("type", 50)
    
    /**
     * 元数据
     */
    val metadata = jsonb("metadata", defaultValue = "{}")
    
    /**
     * 创建者ID
     */
    val createdBy = uuid("created_by")
    
    /**
     * 创建时间
     */
    val createdAt = long("created_at")
    
    /**
     * 更新时间
     */
    val updatedAt = long("updated_at")
    
    init {
        // 创建复合索引
        index(isUnique = false, sourceContentId, type)
        index(isUnique = false, targetContentId, type)
        index(isUnique = true, sourceContentId, targetContentId, type)
    }
}

/**
 * 内容关联实体
 */
class ContentRelationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentRelationEntity>(ContentRelationTable)
    
    var sourceContentId by ContentRelationTable.sourceContentId
    var targetContentId by ContentRelationTable.targetContentId
    var type by ContentRelationTable.type
    var metadata by ContentRelationTable.metadata
    var createdBy by ContentRelationTable.createdBy
    var createdAt by ContentRelationTable.createdAt
    var updatedAt by ContentRelationTable.updatedAt
} 