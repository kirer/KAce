package com.kace.user.domain.repository

import com.kace.user.domain.model.PreferenceCategory
import com.kace.user.domain.model.UserPreference
import java.util.UUID

/**
 * 用户偏好设置仓库接口
 */
interface UserPreferenceRepository {
    /**
     * 获取用户所有偏好设置
     *
     * @param userId 用户ID
     * @return 用户偏好设置列表
     */
    suspend fun findAllByUserId(userId: UUID): List<UserPreference>
    
    /**
     * 根据类别获取用户偏好设置
     *
     * @param userId 用户ID
     * @param category 偏好设置类别
     * @return 用户偏好设置列表
     */
    suspend fun findByUserIdAndCategory(userId: UUID, category: PreferenceCategory): List<UserPreference>
    
    /**
     * 获取用户特定偏好设置
     *
     * @param userId 用户ID
     * @param key 偏好设置键
     * @return 用户偏好设置，如果不存在则返回null
     */
    suspend fun findByUserIdAndKey(userId: UUID, key: String): UserPreference?
    
    /**
     * 保存用户偏好设置
     *
     * @param preference 用户偏好设置
     * @return 保存后的用户偏好设置
     */
    suspend fun save(preference: UserPreference): UserPreference
    
    /**
     * 批量保存用户偏好设置
     *
     * @param preferences 用户偏好设置列表
     * @return 保存后的用户偏好设置列表
     */
    suspend fun saveAll(preferences: List<UserPreference>): List<UserPreference>
    
    /**
     * 删除用户特定偏好设置
     *
     * @param userId 用户ID
     * @param key 偏好设置键
     * @return 是否删除成功
     */
    suspend fun deleteByUserIdAndKey(userId: UUID, key: String): Boolean
    
    /**
     * 删除用户某类别的所有偏好设置
     *
     * @param userId 用户ID
     * @param category 偏好设置类别
     * @return 删除的偏好设置数量
     */
    suspend fun deleteByUserIdAndCategory(userId: UUID, category: PreferenceCategory): Int
    
    /**
     * 删除用户所有偏好设置
     *
     * @param userId 用户ID
     * @return 删除的偏好设置数量
     */
    suspend fun deleteAllByUserId(userId: UUID): Int
} 