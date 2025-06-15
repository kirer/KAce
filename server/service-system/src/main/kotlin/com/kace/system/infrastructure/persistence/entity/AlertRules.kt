package com.kace.system.infrastructure.persistence.entity

import com.kace.system.domain.model.AlertLevel
import com.kace.system.domain.model.AlertRule
import com.kace.system.domain.model.AlertRuleOperator
import com.kace.system.domain.model.AlertRuleType
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

/**
 * 告警规则数据库表
 */
object AlertRules : LongIdTable("alert_rules") {
    val name = varchar("name", 255)
    val metricName = varchar("metric_name", 255)
    val type = enumeration("type", AlertRuleType::class)
    val operator = enumeration("operator", AlertRuleOperator::class)
    val threshold = double("threshold")
    val level = enumeration("level", AlertLevel::class)
    val enabled = bool("enabled").default(true)
    val servicePattern = varchar("service_pattern", 255).default("*")
    val consecutiveDataPoints = integer("consecutive_data_points").default(1)
    val message = text("message").default("")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    /**
     * 将数据库结果行转换为领域模型
     *
     * @param row 数据库结果行
     * @return 告警规则领域模型
     */
    fun toDomain(row: ResultRow): AlertRule = AlertRule(
        id = row[id].value,
        name = row[name],
        metricName = row[metricName],
        type = row[type],
        operator = row[operator],
        threshold = row[threshold],
        level = row[level],
        enabled = row[enabled],
        servicePattern = row[servicePattern],
        consecutiveDataPoints = row[consecutiveDataPoints],
        message = row[message],
        createdAt = row[createdAt].toKotlinInstant(),
        updatedAt = row[updatedAt].toKotlinInstant()
    )
}