package com.kace.content.infrastructure.persistence.entity

import com.kace.content.domain.model.ContentFeedback
import io.jsonwebtoken.lang.Maps
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.jsonb
import java.util.*

/**
 * 内容反馈表
 */
object ContentFeedbacks : UUIDTable("content_feedbacks") {
    val contentId = reference("content_id", Contents)
    val userId = uuid("user_id")
    val type = varchar("type", 20)
    val value = integer("value")
    val metadata = jsonb("metadata", Maps.of(String::class.java, String::class.java).javaClass).nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    
    // 添加唯一约束，确保每个用户对每个内容的每种类型的反馈只有一条记录
    init {
        uniqueIndex(contentId, userId, type)
    }
}

/**
 * 内容反馈实体
 */
class ContentFeedbackEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ContentFeedbackEntity>(ContentFeedbacks)
    
    var contentId by ContentFeedbacks.contentId
    var userId by ContentFeedbacks.userId
    var type by ContentFeedbacks.type
    var value by ContentFeedbacks.value
    var metadata by ContentFeedbacks.metadata
    var createdAt by ContentFeedbacks.createdAt
    var updatedAt by ContentFeedbacks.updatedAt
    
    /**
     * 转换为领域模型
     */
    fun toDomain(): ContentFeedback {
        return ContentFeedback(
            id = id.value,
            contentId = contentId.value,
            userId = userId,
            type = type,
            value = value,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 