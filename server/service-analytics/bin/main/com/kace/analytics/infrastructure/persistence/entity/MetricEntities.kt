package com.kace.analytics.infrastructure.persistence.entity

import com.kace.analytics.domain.model.Metric
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb
import java.time.Instant
import java.util.UUID

/**
 * 指标表定义
 */
object Metrics : UUIDTable("metrics") {
    val name = varchar("name", 100)
    val description = varchar("description", 255).nullable()
    val value = double("value")
    val unit = varchar("unit", 50).nullable()
    val dimensions = jsonb("dimensions", stringify = true).nullable()
    val timestamp = timestamp("timestamp")
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val updatedAt = timestamp("updated_at").clientDefault { Instant.now() }
}

/**
 * 指标实体
 */
class MetricEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MetricEntity>(Metrics)

    var name by Metrics.name
    var description by Metrics.description
    var value by Metrics.value
    var unit by Metrics.unit
    var dimensions by Metrics.dimensions
    var timestamp by Metrics.timestamp
    var createdAt by Metrics.createdAt
    var updatedAt by Metrics.updatedAt

    /**
     * 转换为领域模型
     */
    fun toMetric(): Metric {
        return Metric(
            id = id.value,
            name = name,
            description = description,
            value = value,
            unit = unit,
            dimensions = dimensions as? Map<String, String>,
            timestamp = timestamp,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 