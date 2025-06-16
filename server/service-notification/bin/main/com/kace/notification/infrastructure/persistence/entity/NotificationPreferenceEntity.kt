package com.kace.notification.infrastructure.persistence.entity

import com.kace.notification.domain.model.NotificationPreference
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
 * 通知偏好数据库表
 */
object NotificationPreferenceEntities : UUIDTable("notification_preferences") {
    val userId = uuid("user_id").index()
    val eventType = varchar("event_type", 100)
    val channels = jsonb("channels", defaultValue = "[]")
    val enabled = bool("enabled").default(true)
    val quietHoursStart = integer("quiet_hours_start").nullable()
    val quietHoursEnd = integer("quiet_hours_end").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    init {
        uniqueIndex(userId, eventType)
    }
}

/**
 * 通知偏好实体类
 */
class NotificationPreferenceEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<NotificationPreferenceEntity>(NotificationPreferenceEntities)
    
    var userId by NotificationPreferenceEntities.userId
    var eventType by NotificationPreferenceEntities.eventType
    var channels by NotificationPreferenceEntities.channels
    var enabled by NotificationPreferenceEntities.enabled
    var quietHoursStart by NotificationPreferenceEntities.quietHoursStart
    var quietHoursEnd by NotificationPreferenceEntities.quietHoursEnd
    var createdAt by NotificationPreferenceEntities.createdAt
    var updatedAt by NotificationPreferenceEntities.updatedAt
    
    /**
     * 转换为领域模型
     */
    fun toDomain(): NotificationPreference {
        val channelList = (channels as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        val channelSet = channelList.mapNotNull { 
            try { NotificationType.valueOf(it) } catch (e: Exception) { null } 
        }.toSet()
        
        return NotificationPreference(
            id = id.value,
            userId = userId,
            eventType = eventType,
            channels = channelSet,
            enabled = enabled,
            quietHoursStart = quietHoursStart,
            quietHoursEnd = quietHoursEnd,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

/**
 * 扩展函数：领域模型转换为实体
 */
fun NotificationPreference.toEntity(): NotificationPreferenceEntity = NotificationPreferenceEntity.new(id) {
    this.userId = this@toEntity.userId
    this.eventType = this@toEntity.eventType
    this.channels = this@toEntity.channels.map { it.name }
    this.enabled = this@toEntity.enabled
    this.quietHoursStart = this@toEntity.quietHoursStart
    this.quietHoursEnd = this@toEntity.quietHoursEnd
    this.createdAt = this@toEntity.createdAt
    this.updatedAt = this@toEntity.updatedAt
} 