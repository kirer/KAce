package com.kace.user.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * 角色表定义
 */
object Roles : UUIDTable("roles") {
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val isSystem = bool("is_system").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
}

/**
 * 用户角色关联表
 */
object UserRoles : Table("user_roles") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val roleId = reference("role_id", Roles, onDelete = ReferenceOption.CASCADE)
    
    override val primaryKey = PrimaryKey(userId, roleId)
}

/**
 * 角色权限关联表
 */
object RolePermissions : Table("role_permissions") {
    val roleId = reference("role_id", Roles, onDelete = ReferenceOption.CASCADE)
    val permissionId = reference("permission_id", Permissions, onDelete = ReferenceOption.CASCADE)
    
    override val primaryKey = PrimaryKey(roleId, permissionId)
}

/**
 * 角色实体类
 */
class RoleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RoleEntity>(Roles)
    
    var name by Roles.name
    var description by Roles.description
    var isSystem by Roles.isSystem
    var createdAt by Roles.createdAt
    var updatedAt by Roles.updatedAt
    
    // 角色权限关联
    var permissions by PermissionEntity via RolePermissions
    
    // 用户关联
    var users by UserEntity via UserRoles
} 