package com.kace.analytics.infrastructure.persistence.entity

import com.kace.analytics.domain.model.Event
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb
import java.time.Instant
import java.util.UUID

/**
 * 事件表定义
 */
object Events : UUIDTable("events") {
    val type = varchar("type", 50)
    val name = varchar("name", 100)
    val userId = varchar("user_id", 36).nullable()
    val sessionId = varchar("session_id", 50).nullable()
    val properties = jsonb("properties", stringify = true)
    val timestamp = timestamp("timestamp")
    val appVersion = varchar("app_version", 20).nullable()
    val deviceInfo = jsonb("device_info", stringify = true).nullable()
    val source = varchar("source", 50).nullable()
}

/**
 * 事件实体
 */
class EventEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EventEntity>(Events)

    var type by Events.type
    var name by Events.name
    var userId by Events.userId
    var sessionId by Events.sessionId
    var properties by Events.properties
    var timestamp by Events.timestamp
    var appVersion by Events.appVersion
    var deviceInfo by Events.deviceInfo
    var source by Events.source

    /**
     * 转换为领域模型
     */
    fun toEvent(): Event {
        return Event(
            id = id.value,
            type = type,
            name = name,
            userId = userId?.let { UUID.fromString(it) },
            sessionId = sessionId,
            properties = properties as Map<String, Any>,
            timestamp = timestamp,
            appVersion = appVersion,
            deviceInfo = deviceInfo as? Map<String, Any>,
            source = source
        )
    }
} 