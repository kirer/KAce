package com.kace.system.infrastructure.persistence.entity

import com.kace.system.domain.model.ConfigType
import com.kace.system.domain.model.SystemConfig
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.ResultRow

/**
 * 系统配置表
 */
object SystemConfigs : LongIdTable("system_configs") {
    val key = varchar("key", 255).uniqueIndex()
    val value = text("value")
    val type = enumeration("type", ConfigType::class)
    val description = text("description").default("")
    val category = varchar("category", 100).default("DEFAULT")
    val editable = bool("editable").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    
    /**
     * 将数据库结果行转换为领域模型
     *
     * @param row 数据库结果行
     * @return 系统配置领域模型
     */
    fun toDomain(row: ResultRow): SystemConfig = SystemConfig(
        id = row[id].value,
        key = row[key],
        value = row[value],
        type = row[type],
        description = row[description],
        category = row[category],
        editable = row[editable],
        createdAt = row[createdAt].toKotlinInstant(),
        updatedAt = row[updatedAt].toKotlinInstant()
    )
}