package com.kace.analytics.infrastructure.persistence.entity

import com.kace.analytics.domain.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.json.jsonb
import java.time.Instant
import java.util.UUID

/**
 * 报表表定义
 */
object Reports : UUIDTable("reports") {
    val name = varchar("name", 100)
    val description = varchar("description", 500).nullable()
    val parameters = jsonb<ReportParameters>("parameters", stringify = true)
    val scheduleEnabled = bool("schedule_enabled").default(false)
    val scheduleCronExpression = varchar("schedule_cron_expression", 50).nullable()
    val scheduleRecipients = text("schedule_recipients").nullable()
    val scheduleFormat = varchar("schedule_format", 20).default(ReportFormat.PDF.name)
    val visualizations = jsonb<List<Visualization>>("visualizations", stringify = true).default(listOf())
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
    var parameters by Reports.parameters
    var scheduleEnabled by Reports.scheduleEnabled
    var scheduleCronExpression by Reports.scheduleCronExpression
    var scheduleRecipients by Reports.scheduleRecipients
    var scheduleFormat by Reports.scheduleFormat
    var visualizations by Reports.visualizations
    var createdBy by Reports.createdBy
    var createdAt by Reports.createdAt
    var updatedAt by Reports.updatedAt

    /**
     * 转换为领域模型
     */
    fun toReport(): Report {
        val schedule = if (scheduleEnabled) {
            ReportSchedule(
                enabled = scheduleEnabled,
                cronExpression = scheduleCronExpression,
                recipients = scheduleRecipients?.split(",") ?: emptyList(),
                format = try {
                    ReportFormat.valueOf(scheduleFormat)
                } catch (e: IllegalArgumentException) {
                    ReportFormat.PDF
                }
            )
        } else null

        return Report(
            id = id.value,
            name = name,
            description = description,
            schedule = schedule,
            parameters = parameters,
            visualizations = visualizations,
            createdBy = UUID.fromString(createdBy),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    /**
     * 设置报表计划
     */
    fun setSchedule(schedule: ReportSchedule?) {
        scheduleEnabled = schedule?.enabled ?: false
        scheduleCronExpression = schedule?.cronExpression
        scheduleRecipients = schedule?.recipients?.joinToString(",")
        scheduleFormat = schedule?.format?.name ?: ReportFormat.PDF.name
    }
} 