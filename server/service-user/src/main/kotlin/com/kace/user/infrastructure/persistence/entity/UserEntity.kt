package com.kace.user.infrastructure.persistence.entity

import com.kace.user.domain.model.User
import com.kace.user.domain.model.UserStatus
import java.time.Instant
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.UUID

/**
 * 用户表定义
 */
object UserTable : UUIDTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val avatarUrl = varchar("avatar_url", 255).nullable()
    val status = enumeration<UserStatus>("status")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
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
    var avatarUrl by UserTable.avatarUrl
    var status by UserTable.status
    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
    
    /**
     * 将实体转换为领域模型
     */
    fun toModel(): User {
        return User(
            id = id.value.toString(),
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            avatarUrl = avatarUrl,
            status = status,
            roles = emptyList(), // 需要单独查询角色
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    /**
     * 更新实体
     */
    fun update(user: User) {
        firstName = user.firstName
        lastName = user.lastName
        avatarUrl = user.avatarUrl
        status = user.status
        updatedAt = Instant.now()
    }
} 