package com.kace.user.domain.repository

import com.kace.user.domain.model.UserProfile

/**
 * 用户资料仓库接口
 */
interface UserProfileRepository {
    /**
     * 根据ID查找用户资料
     * @param id 用户资料ID
     * @return 用户资料对象，如果不存在则返回null
     */
    suspend fun findById(id: String): UserProfile?
    
    /**
     * 根据用户ID查找用户资料
     * @param userId 用户ID
     * @return 用户资料对象，如果不存在则返回null
     */
    suspend fun findByUserId(userId: String): UserProfile?
    
    /**
     * 保存用户资料（创建或更新）
     * @param profile 用户资料对象
     * @return 保存后的用户资料对象
     */
    suspend fun save(profile: UserProfile): UserProfile
    
    /**
     * 根据ID删除用户资料
     * @param id 用户资料ID
     * @return 是否删除成功
     */
    suspend fun deleteById(id: String): Boolean
    
    /**
     * 根据用户ID删除用户资料
     * @param userId 用户ID
     * @return 是否删除成功
     */
    suspend fun deleteByUserId(userId: String): Boolean
} 