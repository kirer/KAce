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
 * 角色表定义
 */
object RoleTable : UUIDTable("roles") {
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val isSystem = bool("is_system").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
}

/**
 * 角色实体
 */
class RoleEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RoleEntity>(RoleTable)
    
    var name by RoleTable.name
    var description by RoleTable.description
    var isSystem by RoleTable.isSystem
    var createdAt by RoleTable.createdAt
    var updatedAt by RoleTable.updatedAt
    
    // 多对多关系：角色-用户
    var users by UserEntity via UserRoleTable
    
    // 多对多关系：角色-权限
    var permissions by PermissionEntity via RolePermissionTable
}

/**
 * 用户-角色关联表
 */
object UserRoleTable : Table("user_roles") {
    val user = reference("user_id", UserTable)
    val role = reference("role_id", RoleTable)
    
    override val primaryKey = PrimaryKey(user, role)
} 