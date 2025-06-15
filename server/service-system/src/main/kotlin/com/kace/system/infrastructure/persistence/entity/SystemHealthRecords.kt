package com.kace.system.infrastructure.persistence.entity

import com.kace.system.domain.model.HealthStatus
import com.kace.system.domain.model.SystemHealth
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

/**
 * 系统健康记录数据库表
 */
object SystemHealthRecords : LongIdTable("system_health_records") {
    val serviceId = varchar("service_id", 100)
    val status = enumeration("status", HealthStatus::class)
    val details = jsonb("details", String::class).nullable()
    val timestamp = timestamp("timestamp")
    
    /**
     * 将数据库结果行转换为领域模型
     *
     * @param row 数据库结果行
     * @return 系统健康记录领域模型
     */
    fun toDomain(row: ResultRow): SystemHealth = SystemHealth(
        id = row[id].value,
        serviceId = row[serviceId],
        status = row[status],
        details = row[details]?.let {
            try {
                kotlinx.serialization.json.Json.decodeFromString<Map<String, String>>(it)
            } catch (e: Exception) {
                emptyMap()
            }
        } ?: emptyMap(),
        timestamp = row[timestamp].toKotlinInstant()
    )
}