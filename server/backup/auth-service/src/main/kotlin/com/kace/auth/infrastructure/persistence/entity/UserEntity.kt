package com.kace.auth.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

/**
 * 用户表定义
 */
object UserTable : UUIDTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val firstName = varchar("first_name", 50).nullable()
    val lastName = varchar("last_name", 50).nullable()
    val active = bool("active").default(true)
    val verified = bool("verified").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())
    val lastLoginAt = timestamp("last_login_at").nullable()
}

/**
 * 用户实体
 */
class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UserTable)
    
    var username by UserTable.username
    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var firstName by UserTable.firstName
    var lastName by UserTable.lastName
    var active by UserTable.active
    var verified by UserTable.verified
    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
    var lastLoginAt by UserTable.lastLoginAt
    
    // 多对多关系：用户-角色
    var roles by RoleEntity via UserRoleTable
} 