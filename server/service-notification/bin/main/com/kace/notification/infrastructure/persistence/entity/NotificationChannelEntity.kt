package com.kace.notification.infrastructure.persistence.entity

import com.kace.notification.domain.model.ChannelProvider
import com.kace.notification.domain.model.NotificationChannel
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
 * 通知渠道数据库表
 */
object NotificationChannelEntities : UUIDTable("notification_channels") {
    val name = varchar("name", 100).uniqueIndex()
    val type = enumerationByName("type", 20, NotificationType::class)
    val provider = enumerationByName("provider", 20, ChannelProvider::class)
    val config = jsonb("config", defaultValue = "{}")
    val isDefault = bool("is_default").default(false)
    val isActive = bool("is_active").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 通知渠道实体类
 */
class NotificationChannelEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<NotificationChannelEntity>(NotificationChannelEntities)
    
    var name by NotificationChannelEntities.name
    var type by NotificationChannelEntities.type
    var provider by NotificationChannelEntities.provider
    var config by NotificationChannelEntities.config
    var isDefault by NotificationChannelEntities.isDefault
    var isActive by NotificationChannelEntities.isActive
    var createdAt by NotificationChannelEntities.createdAt
    var updatedAt by NotificationChannelEntities.updatedAt
    
    /**
     * 转换为领域模型
     */
    fun toDomain(): NotificationChannel = NotificationChannel(
        id = id.value,
        name = name,
        type = type,
        provider = provider,
        config = (config as? Map<*, *>)?.entries?.associate { 
            (it.key as? String ?: it.key.toString()) to (it.value as? String ?: it.value.toString())
        } ?: emptyMap(),
        isDefault = isDefault,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * 扩展函数：领域模型转换为实体
 */
fun NotificationChannel.toEntity(): NotificationChannelEntity = NotificationChannelEntity.new(id) {
    this.name = this@toEntity.name
    this.type = this@toEntity.type
    this.provider = this@toEntity.provider
    this.config = this@toEntity.config
    this.isDefault = this@toEntity.isDefault
    this.isActive = this@toEntity.isActive
    this.createdAt = this@toEntity.createdAt
    this.updatedAt = this@toEntity.updatedAt
} 