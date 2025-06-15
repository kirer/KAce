package com.kace.system.infrastructure.persistence.entity

import com.kace.system.domain.model.MetricType
import com.kace.system.domain.model.SystemMetric
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

/**
 * 系统指标数据库表
 */
object SystemMetrics : LongIdTable("system_metrics") {
    val name = varchar("name", 255)
    val type = enumeration("type", MetricType::class)
    val value = double("value")
    val unit = varchar("unit", 50).default("")
    val serviceId = varchar("service_id", 100).default("system")
    val timestamp = timestamp("timestamp")
    val tags = jsonb("tags", String::class).nullable()
    
    /**
     * 将数据库结果行转换为领域模型
     *
     * @param row 数据库结果行
     * @return 系统指标领域模型
     */
    fun toDomain(row: ResultRow): SystemMetric = SystemMetric(
        id = row[id].value,
        name = row[name],
        type = row[type],
        value = row[value],
        unit = row[unit],
        serviceId = row[serviceId],
        timestamp = row[timestamp].toKotlinInstant(),
        tags = row[tags]?.let { 
            // 假设tags是JSON字符串，需要解析为Map
            try {
                kotlinx.serialization.json.Json.decodeFromString<Map<String, String>>(it)
            } catch (e: Exception) {
                emptyMap()
            }
        } ?: emptyMap()
    )
}