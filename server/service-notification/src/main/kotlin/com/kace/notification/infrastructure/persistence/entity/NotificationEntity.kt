package com.kace.notification.infrastructure.persistence.entity

import com.kace.notification.domain.model.Notification
import com.kace.notification.domain.model.NotificationPriority
import com.kace.notification.domain.model.NotificationStatus
import com.kace.notification.domain.model.NotificationType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb
import java.time.Instant
import java.util.UUID

/**
 * 通知数据库表
 */
object NotificationEntities : UUIDTable("notifications") {
    val type = enumerationByName("type", 20, NotificationType::class)
    val recipientId = uuid("recipient_id")
    val title = varchar("title", 255)
    val content = text("content")
    val metadata = jsonb("metadata", defaultValue = "{}")
    val status = enumerationByName("status", 20, NotificationStatus::class)
    val priority = enumerationByName("priority", 20, NotificationPriority::class)
    val sentAt = timestamp("sent_at").nullable()
    val deliveredAt = timestamp("delivered_at").nullable()
    val readAt = timestamp("read_at").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 通知实体类
 */
class NotificationEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<NotificationEntity>(NotificationEntities)
    
    var type by NotificationEntities.type
    var recipientId by NotificationEntities.recipientId
    var title by NotificationEntities.title
    var content by NotificationEntities.content
    var metadata by NotificationEntities.metadata
    var status by NotificationEntities.status
    var priority by NotificationEntities.priority
    var sentAt by NotificationEntities.sentAt
    var deliveredAt by NotificationEntities.deliveredAt
    var readAt by NotificationEntities.readAt
    var createdAt by NotificationEntities.createdAt
    var updatedAt by NotificationEntities.updatedAt
    
    /**
     * 转换为领域模型
     */
    fun toDomain(): Notification = Notification(
        id = id.value,
        type = type,
        recipientId = recipientId,
        title = title,
        content = content,
        metadata = metadata as? Map<String, Any>,
        status = status,
        priority = priority,
        sentAt = sentAt,
        deliveredAt = deliveredAt,
        readAt = readAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * 扩展函数：领域模型转换为实体
 */
fun Notification.toEntity(): NotificationEntity = NotificationEntity.new(id) {
    this.type = this@toEntity.type
    this.recipientId = this@toEntity.recipientId
    this.title = this@toEntity.title
    this.content = this@toEntity.content
    this.metadata = this@toEntity.metadata as? Map<String, Any> ?: emptyMap()
    this.status = this@toEntity.status
    this.priority = this@toEntity.priority
    this.sentAt = this@toEntity.sentAt
    this.deliveredAt = this@toEntity.deliveredAt
    this.readAt = this@toEntity.readAt
    this.createdAt = this@toEntity.createdAt
    this.updatedAt = this@toEntity.updatedAt
} 