package com.kace.auth.infrastructure.persistence.repository

import com.kace.auth.domain.model.User
import com.kace.auth.domain.model.Role
import com.kace.auth.domain.repository.UserRepository
import com.kace.auth.infrastructure.persistence.entity.UserEntity
import com.kace.auth.infrastructure.persistence.entity.UserTable
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * 用户仓库实现
 */
class UserRepositoryImpl : UserRepository {
    
    /**
     * 根据ID查找用户
     */
    override suspend fun findById(id: UUID): User? = transaction {
        UserEntity.findById(id)?.toUser()
    }
    
    /**
     * 根据用户名查找用户
     */
    override suspend fun findByUsername(username: String): User? = transaction {
        UserEntity.find { UserTable.username eq username }.firstOrNull()?.toUser()
    }
    
    /**
     * 根据邮箱查找用户
     */
    override suspend fun findByEmail(email: String): User? = transaction {
        UserEntity.find { UserTable.email eq email }.firstOrNull()?.toUser()
    }
    
    /**
     * 分页查询用户
     */
    override suspend fun findAll(pageRequest: PageRequest): PageResponse<User> = transaction {
        val page = pageRequest.page.coerceAtLeast(1)
        val size = pageRequest.size.coerceAtLeast(1)
        val offset = ((page - 1) * size).toLong()
        
        val sortField = when (pageRequest.sort?.lowercase()) {
            "username" -> UserTable.username
            "email" -> UserTable.email
            "createdat" -> UserTable.createdAt
            else -> UserTable.createdAt
        }
        
        val sortOrder = when (pageRequest.direction?.uppercase()) {
            "ASC" -> SortOrder.ASC
            else -> SortOrder.DESC
        }
        
        val query = UserEntity.all().orderBy(sortField to sortOrder)
        val totalElements = query.count()
        val users = query.limit(size, offset).map { it.toUser() }
        
        PageResponse.of(users, page, size, totalElements)
    }
    
    /**
     * 保存用户
     */
    override suspend fun save(user: User): User = transaction {
        val entity = UserEntity.new {
            username = user.username
            email = user.email
            passwordHash = user.passwordHash
            firstName = user.firstName
            lastName = user.lastName
            active = user.active
            verified = user.verified
            createdAt = user.createdAt
            updatedAt = user.updatedAt
            lastLoginAt = user.lastLoginAt
        }
        
        // 保存角色关联
        if (user.roles.isNotEmpty()) {
            // 此处需要实现角色关联逻辑
        }
        
        entity.toUser()
    }
    
    /**
     * 更新用户
     */
    override suspend fun update(user: User): User = transaction {
        val entity = UserEntity.findById(user.id) ?: throw NoSuchElementException("用户不存在: ${user.id}")
        
        entity.apply {
            username = user.username
            email = user.email
            passwordHash = user.passwordHash
            firstName = user.firstName
            lastName = user.lastName
            active = user.active
            verified = user.verified
            updatedAt = user.updatedAt
            lastLoginAt = user.lastLoginAt
        }
        
        // 更新角色关联
        // 此处需要实现角色关联逻辑
        
        entity.toUser()
    }
    
    /**
     * 删除用户
     */
    override suspend fun delete(id: UUID): Boolean = transaction {
        val entity = UserEntity.findById(id) ?: return@transaction false
        entity.delete()
        true
    }
    
    /**
     * 检查用户名是否存在
     */
    override suspend fun existsByUsername(username: String): Boolean = transaction {
        UserEntity.find { UserTable.username eq username }.count() > 0
    }
    
    /**
     * 检查邮箱是否存在
     */
    override suspend fun existsByEmail(email: String): Boolean = transaction {
        UserEntity.find { UserTable.email eq email }.count() > 0
    }
    
    /**
     * 将实体转换为领域模型
     */
    private fun UserEntity.toUser(): User {
        val rolesList = this.roles.map {
            Role(
                id = it.id.value,
                name = it.name,
                description = it.description,
                permissions = emptySet(), // 此处需要实现权限关联逻辑
                isSystem = it.isSystem,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }.toSet()
        
        return User(
            id = this.id.value,
            username = this.username,
            email = this.email,
            passwordHash = this.passwordHash,
            firstName = this.firstName,
            lastName = this.lastName,
            active = this.active,
            verified = this.verified,
            roles = rolesList,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            lastLoginAt = this.lastLoginAt
        )
    }
} 