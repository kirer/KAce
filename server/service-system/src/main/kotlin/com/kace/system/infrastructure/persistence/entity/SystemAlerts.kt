package com.kace.system.infrastructure.persistence.entity

import com.kace.system.domain.model.AlertLevel
import com.kace.system.domain.model.SystemAlert
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

/**
 * 系统告警数据库表
 */
object SystemAlerts : LongIdTable("system_alerts") {
    val name = varchar("name", 255)
    val level = enumeration("level", AlertLevel::class)
    val message = text("message")
    val serviceId = varchar("service_id", 100)
    val metricName = varchar("metric_name", 255)
    val threshold = double("threshold")
    val currentValue = double("current_value")
    val timestamp = timestamp("timestamp")
    val acknowledged = bool("acknowledged").default(false)
    val resolvedAt = timestampWithTimeZone("resolved_at").nullable()
    
    /**
     * 将数据库结果行转换为领域模型
     *
     * @param row 数据库结果行
     * @return 系统告警领域模型
     */
    fun toDomain(row: ResultRow): SystemAlert = SystemAlert(
        id = row[id].value,
        name = row[name],
        level = row[level],
        message = row[message],
        serviceId = row[serviceId],
        metricName = row[metricName],
        threshold = row[threshold],
        currentValue = row[currentValue],
        timestamp = row[timestamp].toKotlinInstant(),
        acknowledged = row[acknowledged],
        resolvedAt = row[resolvedAt]?.toKotlinInstant()
    )
}