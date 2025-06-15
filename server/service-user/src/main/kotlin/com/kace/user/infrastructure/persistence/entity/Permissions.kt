package com.kace.user.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * 权限表定义
 */
object Permissions : UUIDTable("permissions") {
    val name = varchar("name", 100).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val resource = varchar("resource", 50)
    val action = varchar("action", 50)
    val isSystem = bool("is_system").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
    
    init {
        // 资源和操作的组合应该是唯一的
        uniqueIndex(resource, action)
    }
}

/**
 * 权限实体类
 */
class PermissionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PermissionEntity>(Permissions)
    
    var name by Permissions.name
    var description by Permissions.description
    var resource by Permissions.resource
    var action by Permissions.action
    var isSystem by Permissions.isSystem
    var createdAt by Permissions.createdAt
    var updatedAt by Permissions.updatedAt
    
    // 角色关联
    var roles by RoleEntity via RolePermissions
} 