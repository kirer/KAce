package com.kace.content.infrastructure.persistence.entity

import com.kace.content.domain.model.ContentComment
import io.jsonwebtoken.lang.Maps
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.jsonb
import java.util.*

/**
 * 内容评论表
 */
object ContentComments : UUIDTable("content_comments") {
    val contentId = reference("content_id", Contents)
    val userId = uuid("user_id")
    val parentId = uuid("parent_id").nullable()
    val content = text("content")
    val status = varchar("status", 20)
    val metadata = jsonb("metadata", Maps.of(String::class.java, String::class.java).javaClass).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

/**
 * 内容评论实体
 */
class ContentCommentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentCommentEntity>(ContentComments)
    
    var contentId by ContentComments.contentId
    var userId by ContentComments.userId
    var parentId by ContentComments.parentId
    var content by ContentComments.content
    var status by ContentComments.status
    var metadata by ContentComments.metadata
    var createdAt by ContentComments.createdAt
    var updatedAt by ContentComments.updatedAt
    
    /**
     * 转换为领域模型
     */
    fun toDomain(): ContentComment {
        return ContentComment(
            id = id.value,
            contentId = contentId.value,
            userId = userId,
            parentId = parentId,
            content = content,
            status = status,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 