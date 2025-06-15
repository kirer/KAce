package com.kace.user.infrastructure.persistence.entity

import com.kace.user.domain.model.Permission
import java.time.Instant
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID

/**
 * 权限表定义
 */
object PermissionTable : UUIDTable("permissions") {
    val name = varchar("name", 100)
    val code = varchar("code", 50).uniqueIndex()
    val description = text("description").nullable()
    val category = varchar("category", 50)
    val isSystem = bool("is_system").default(true)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

/**
 * 权限实体
 */
class PermissionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PermissionEntity>(PermissionTable)
    
    var name by PermissionTable.name
    var code by PermissionTable.code
    var description by PermissionTable.description
    var category by PermissionTable.category
    var isSystem by PermissionTable.isSystem
    var createdAt by PermissionTable.createdAt
    var updatedAt by PermissionTable.updatedAt
    
    /**
     * 将实体转换为领域模型
     */
    fun toModel(): Permission {
        return Permission(
            id = id.value.toString(),
            name = name,
            code = code,
            description = description,
            category = category,
            isSystem = isSystem,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    /**
     * 更新实体
     */
    fun update(permission: Permission) {
        name = permission.name
        description = permission.description
        category = permission.category
        updatedAt = Instant.now()
    }
} 