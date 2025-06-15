package com.kace.user.infrastructure.persistence.repository

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.User
import com.kace.user.domain.model.UserStatus
import com.kace.user.domain.repository.UserRepository
import com.kace.user.infrastructure.persistence.entity.UserEntity
import com.kace.user.infrastructure.persistence.entity.Users
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * 用户仓库实现类
 */
class UserRepositoryImpl : UserRepository {
    
    /**
     * 根据ID查找用户
     */
    override suspend fun findById(id: String): User? {
        return transaction {
            UserEntity.findById(UUID.fromString(id))?.toModel()
        }
    }
    
    /**
     * 根据用户名查找用户
     */
    override suspend fun findByUsername(username: String): User? {
        return transaction {
            UserEntity.find { Users.username eq username }.firstOrNull()?.toModel()
        }
    }
    
    /**
     * 根据邮箱查找用户
     */
    override suspend fun findByEmail(email: String): User? {
        return transaction {
            UserEntity.find { Users.email eq email }.firstOrNull()?.toModel()
        }
    }
    
    /**
     * 分页查找所有用户
     */
    override suspend fun findAll(page: Int, size: Int, status: UserStatus?, query: String?): PageDto<User> {
        return transaction {
            // 构建查询条件
            val conditions = mutableListOf<Op<Boolean>>()
            
            // 添加状态过滤
            status?.let {
                conditions.add(Users.status eq status.name)
            }
            
            // 添加搜索过滤
            query?.let {
                if (it.isNotBlank()) {
                    conditions.add(
                        (Users.username like "%$it%") or
                        (Users.email like "%$it%") or
                        (Users.firstName like "%$it%") or
                        (Users.lastName like "%$it%")
                    )
                }
            }
            
            // 组合所有条件
            val whereClause = if (conditions.isNotEmpty()) {
                conditions.reduce { acc, op -> acc and op }
            } else {
                null
            }
            
            // 计算总数
            val totalCount = if (whereClause != null) {
                UserEntity.count(whereClause)
            } else {
                UserEntity.count()
            }
            
            // 查询数据
            val users = if (whereClause != null) {
                UserEntity.find(whereClause)
            } else {
                UserEntity.all()
            }
                .orderBy(Users.username to SortOrder.ASC)
                .limit(size, offset = ((page - 1) * size).toLong())
                .map { it.toModel() }
            
            // 返回分页结果
            PageDto(
                content = users,
                pageNumber = page,
                pageSize = size,
                totalElements = totalCount,
                totalPages = ((totalCount + size - 1) / size).toInt()
            )
        }
    }
    
    /**
     * 保存用户
     */
    override suspend fun save(user: User): User {
        return transaction {
            val existingUser = user.id.let { id ->
                try {
                    UserEntity.findById(UUID.fromString(id))
                } catch (e: Exception) {
                    null
                }
            }
            
            if (existingUser != null) {
                // 更新现有用户
                existingUser.apply {
                    username = user.username
                    email = user.email
                    firstName = user.firstName
                    lastName = user.lastName
                    avatarUrl = user.avatarUrl
                    status = user.status.name
                    updatedAt = user.updatedAt.toJavaInstant()
                }.toModel()
            } else {
                // 创建新用户
                UserEntity.new(UUID.fromString(user.id)) {
                    username = user.username
                    email = user.email
                    firstName = user.firstName
                    lastName = user.lastName
                    avatarUrl = user.avatarUrl
                    status = user.status.name
                    createdAt = user.createdAt.toJavaInstant()
                    updatedAt = user.updatedAt.toJavaInstant()
                }.toModel()
            }
        }
    }
    
    /**
     * 根据ID删除用户
     */
    override suspend fun deleteById(id: String): Boolean {
        return transaction {
            try {
                val user = UserEntity.findById(UUID.fromString(id))
                user?.delete()
                user != null
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * 将实体转换为领域模型
     */
    private fun UserEntity.toModel(): User {
        return User(
            id = id.value.toString(),
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            avatarUrl = avatarUrl,
            status = UserStatus.valueOf(status),
            roles = roles.map { it.name },
            createdAt = createdAt.toKotlinInstant(),
            updatedAt = updatedAt.toKotlinInstant()
        )
    }
} 