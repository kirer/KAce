package com.kace.auth.domain.service

import com.kace.auth.domain.model.User
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.util.*

/**
 * 用户服务接口
 */
interface UserService {
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
     * 创建用户
     */
    suspend fun createUser(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null,
        roleIds: Set<UUID> = emptySet()
    ): User
    
    /**
     * 更新用户
     */
    suspend fun updateUser(
        id: UUID,
        username: String? = null,
        email: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        active: Boolean? = null,
        verified: Boolean? = null,
        roleIds: Set<UUID>? = null
    ): User
    
    /**
     * 删除用户
     */
    suspend fun deleteUser(id: UUID): Boolean
    
    /**
     * 获取用户
     */
    suspend fun getUser(id: UUID): User
    
    /**
     * 根据用户名获取用户
     */
    suspend fun getUserByUsername(username: String): User
    
    /**
     * 根据邮箱获取用户
     */
    suspend fun getUserByEmail(email: String): User
    
    /**
     * 分页获取所有用户
     */
    suspend fun getAllUsers(pageRequest: PageRequest): PageResponse<User>
    
    /**
     * 修改用户密码
     */
    suspend fun changePassword(id: UUID, oldPassword: String, newPassword: String): Boolean
    
    /**
     * 重置用户密码
     */
    suspend fun resetPassword(id: UUID, newPassword: String): Boolean
    
    /**
     * 为用户添加角色
     */
    suspend fun addRoleToUser(userId: UUID, roleId: UUID): User
    
    /**
     * 从用户中移除角色
     */
    suspend fun removeRoleFromUser(userId: UUID, roleId: UUID): User
    
    /**
     * 验证用户
     */
    suspend fun verifyUser(userId: UUID): User?
    
    /**
     * 禁用用户
     */
    suspend fun disableUser(userId: UUID): User?
    
    /**
     * 启用用户
     */
    suspend fun enableUser(userId: UUID): User?
} 