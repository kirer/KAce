package com.kace.analytics.infrastructure.persistence.entity

import com.kace.analytics.domain.model.Report
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb
import java.time.Instant
import java.util.UUID

/**
 * 报表表定义
 */
object Reports : UUIDTable("reports") {
    val name = varchar("name", 100)
    val description = varchar("description", 255).nullable()
    val type = varchar("type", 50)
    val query = text("query")
    val parameters = jsonb("parameters", stringify = true).nullable()
    val schedule = varchar("schedule", 50).nullable()
    val lastRunAt = timestamp("last_run_at").nullable()
    val createdBy = varchar("created_by", 36)
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }
    val updatedAt = timestamp("updated_at").clientDefault { Instant.now() }
}

/**
 * 报表实体
 */
class ReportEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ReportEntity>(Reports)

    var name by Reports.name
    var description by Reports.description
    var type by Reports.type
    var query by Reports.query
    var parameters by Reports.parameters
    var schedule by Reports.schedule
    var lastRunAt by Reports.lastRunAt
    var createdBy by Reports.createdBy
    var createdAt by Reports.createdAt
    var updatedAt by Reports.updatedAt

    /**
     * 转换为领域模型
     */
    fun toReport(): Report {
        return Report(
            id = id.value,
            name = name,
            description = description,
            type = type,
            query = query,
            parameters = parameters as? Map<String, Any>,
            schedule = schedule,
            lastRunAt = lastRunAt,
            createdBy = UUID.fromString(createdBy),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 