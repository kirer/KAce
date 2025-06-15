package com.kace.auth.domain.repository

import com.kace.auth.domain.model.User
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.util.*

/**
 * 用户仓库接口
 */
interface UserRepository {
    /**
     * 根据ID查找用户
     */
    suspend fun findById(id: UUID): User?
    
    /**
     * 根据用户名查找用户
     */
    suspend fun findByUsername(username: String): User?
    
    /**
     * 根据邮箱查找用户
     */
    suspend fun findByEmail(email: String): User?
    
    /**
     * 分页查询用户
     */
    suspend fun findAll(pageRequest: PageRequest): PageResponse<User>
    
    /**
     * 保存用户
     */
    suspend fun save(user: User): User
    
    /**
     * 更新用户
     */
    suspend fun update(user: User): User
    
    /**
     * 删除用户
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 检查用户名是否存在
     */
    suspend fun existsByUsername(username: String): Boolean
    
    /**
     * 检查邮箱是否存在
     */
    suspend fun existsByEmail(email: String): Boolean
} 