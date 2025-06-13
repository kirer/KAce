package com.kace.user.domain.repository

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.User
import com.kace.user.domain.model.UserProfile
import com.kace.user.domain.model.UserStatus

/**
 * 用户仓库接口
 */
interface UserRepository {
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    suspend fun getById(id: String): User?
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    suspend fun getByUsername(username: String): User?
    
    /**
     * 根据邮箱获取用户
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回null
     */
    suspend fun getByEmail(email: String): User?
    
    /**
     * 分页获取用户列表
     * @param page 页码
     * @param size 每页大小
     * @param status 用户状态过滤
     * @param query 搜索关键词
     * @return 用户分页对象
     */
    suspend fun getUsers(page: Int, size: Int, status: UserStatus? = null, query: String? = null): PageDto<User>
    
    /**
     * 创建用户
     * @param user 用户对象
     * @return 创建的用户对象
     */
    suspend fun createUser(user: User): User
    
    /**
     * 更新用户
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    suspend fun updateUser(user: User): User
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 用户状态
     * @return 是否更新成功
     */
    suspend fun updateStatus(id: String, status: UserStatus): Boolean
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    suspend fun deleteUser(id: String): Boolean
    
    /**
     * 获取用户资料
     * @param userId 用户ID
     * @return 用户资料对象，如果不存在则返回null
     */
    suspend fun getUserProfile(userId: String): UserProfile?
    
    /**
     * 创建或更新用户资料
     * @param profile 用户资料对象
     * @return 创建或更新后的用户资料对象
     */
    suspend fun saveUserProfile(profile: UserProfile): UserProfile
} 