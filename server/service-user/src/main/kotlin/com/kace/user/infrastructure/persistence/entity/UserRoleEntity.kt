package com.kace.user.infrastructure.persistence.entity

import java.time.Instant
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * 用户角色关联表定义
 */
object UserRoleTable : Table("user_roles") {
    val userId = uuid("user_id").references(UserTable.id)
    val roleId = uuid("role_id").references(RoleTable.id)
    val assignedAt = timestamp("assigned_at")
    val assignedBy = uuid("assigned_by").references(UserTable.id)
    
    override val primaryKey = PrimaryKey(userId, roleId)
}

/**
 * 从数据库中获取用户角色关联
 */
fun getUserRole(userId: UUID, roleId: UUID): com.kace.user.domain.model.UserRole? {
    return transaction {
        val userRoleRow = UserRoleTable.select {
            (UserRoleTable.userId eq userId) and (UserRoleTable.roleId eq roleId)
        }.singleOrNull() ?: return@transaction null
        
        com.kace.user.domain.model.UserRole(
            userId = userRoleRow[UserRoleTable.userId].value.toString(),
            roleId = userRoleRow[UserRoleTable.roleId].value.toString(),
            assignedAt = userRoleRow[UserRoleTable.assignedAt],
            assignedBy = userRoleRow[UserRoleTable.assignedBy].toString()
        )
    }
}

/**
 * 插入用户角色关联
 */
fun insertUserRole(userRole: com.kace.user.domain.model.UserRole): Boolean {
    return transaction {
        try {
            UserRoleTable.insert {
                it[userId] = UUID.fromString(userRole.userId)
                it[roleId] = UUID.fromString(userRole.roleId)
                it[assignedAt] = userRole.assignedAt
                it[assignedBy] = UUID.fromString(userRole.assignedBy)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * 删除用户角色关联
 */
fun deleteUserRole(userId: UUID, roleId: UUID): Boolean {
    return transaction {
        try {
            val deleted = UserRoleTable.deleteWhere {
                (UserRoleTable.userId eq userId) and (UserRoleTable.roleId eq roleId)
            }
            deleted > 0
        } catch (e: Exception) {
            false
        }
    }
} 