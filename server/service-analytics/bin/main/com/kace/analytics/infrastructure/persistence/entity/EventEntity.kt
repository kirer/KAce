package com.kace.analytics.infrastructure.persistence.entity

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.model.DeviceInfo
import com.kace.analytics.domain.model.DeviceType
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

/**
 * 事件表定义
 */
object EventTable : IdTable<String>("events") {
    override val id = varchar("id", 36).entityId()
    val type = varchar("type", 50)
    val name = varchar("name", 100)
    val userId = varchar("user_id", 36).nullable()
    val sessionId = varchar("session_id", 50).nullable()
    val properties = text("properties")
    val timestamp = timestamp("timestamp")
    val appVersion = varchar("app_version", 20).nullable()
    val deviceInfo = text("device_info").nullable()
    val source = varchar("source", 50).nullable()
    
    override val primaryKey = PrimaryKey(id)
}

/**
 * 事件实体
 */
class EventEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, EventEntity>(EventTable)
    
    var type by EventTable.type
    var name by EventTable.name
    var userId by EventTable.userId
    var sessionId by EventTable.sessionId
    var properties by EventTable.properties
    var timestamp by EventTable.timestamp
    var appVersion by EventTable.appVersion
    var deviceInfo by EventTable.deviceInfo
    var source by EventTable.source
    
    /**
     * 将实体转换为领域模型
     */
    fun toModel(): Event {
        val propertiesMap = try {
            Json.decodeFromString<Map<String, String>>(properties)
        } catch (e: Exception) {
            emptyMap()
        }
        
        val deviceInfoObj = try {
            deviceInfo?.let { Json.decodeFromString<DeviceInfo>(it) }
        } catch (e: Exception) {
            null
        }
        
        return Event(
            id = id.value,
            type = type,
            name = name,
            userId = userId,
            sessionId = sessionId,
            properties = propertiesMap,
            timestamp = timestamp,
            appVersion = appVersion,
            deviceInfo = deviceInfoObj,
            source = source
        )
    }
} 