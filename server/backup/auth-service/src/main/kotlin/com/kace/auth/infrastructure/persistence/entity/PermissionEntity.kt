package com.kace.auth.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.Table
import java.time.Instant
import java.util.*

/**
 * 权限表定义
 */
object PermissionTable : UUIDTable("permissions") {
    val name = varchar("name", 100).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val resource = varchar("resource", 50)
    val action = varchar("action", 50)
    val isSystem = bool("is_system").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
}

/**
 * 权限实体
 */
class PermissionEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PermissionEntity>(PermissionTable)
    
    var name by PermissionTable.name
    var description by PermissionTable.description
    var resource by PermissionTable.resource
    var action by PermissionTable.action
    var isSystem by PermissionTable.isSystem
    var createdAt by PermissionTable.createdAt
    var updatedAt by PermissionTable.updatedAt
    
    // 多对多关系：权限-角色
    var roles by RoleEntity via RolePermissionTable
}

/**
 * 角色-权限关联表
 */
object RolePermissionTable : Table("role_permissions") {
    val role = reference("role_id", RoleTable)
    val permission = reference("permission_id", PermissionTable)
    
    override val primaryKey = PrimaryKey(role, permission)
} 