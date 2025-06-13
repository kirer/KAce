package com.kace.user.infrastructure.persistence.entity

import java.time.Instant
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * 角色权限关联表定义
 */
object RolePermissionTable : Table("role_permissions") {
    val roleId = uuid("role_id").references(RoleTable.id)
    val permissionId = uuid("permission_id").references(PermissionTable.id)
    val assignedAt = timestamp("assigned_at")
    val assignedBy = uuid("assigned_by").references(UserTable.id)
    
    override val primaryKey = PrimaryKey(roleId, permissionId)
}

/**
 * 插入角色权限关联
 */
fun insertRolePermission(roleId: UUID, permissionId: UUID, assignedBy: UUID): Boolean {
    return transaction {
        try {
            RolePermissionTable.insert {
                it[this.roleId] = roleId
                it[this.permissionId] = permissionId
                it[this.assignedAt] = Instant.now()
                it[this.assignedBy] = assignedBy
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * 删除角色权限关联
 */
fun deleteRolePermission(roleId: UUID, permissionId: UUID): Boolean {
    return transaction {
        try {
            val deleted = RolePermissionTable.deleteWhere {
                (RolePermissionTable.roleId eq roleId) and (RolePermissionTable.permissionId eq permissionId)
            }
            deleted > 0
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * 获取角色的所有权限ID
 */
fun getRolePermissionIds(roleId: UUID): List<UUID> {
    return transaction {
        RolePermissionTable.select {
            RolePermissionTable.roleId eq roleId
        }.map { it[RolePermissionTable.permissionId].value }
    }
}

/**
 * 获取拥有特定权限的所有角色ID
 */
fun getRolesByPermissionId(permissionId: UUID): List<UUID> {
    return transaction {
        RolePermissionTable.select {
            RolePermissionTable.permissionId eq permissionId
        }.map { it[RolePermissionTable.roleId].value }
    }
}

/**
 * 移除角色的所有权限
 */
fun removeAllPermissionsFromRole(roleId: UUID): Int {
    return transaction {
        RolePermissionTable.deleteWhere {
            RolePermissionTable.roleId eq roleId
        }
    }
} 