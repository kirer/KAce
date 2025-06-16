package com.kace.user.domain.service

import com.kace.user.domain.model.PreferenceCategory
import com.kace.user.domain.model.UserPreference
import java.util.UUID

/**
 * 用户偏好设置服务接口
 */
interface UserPreferenceService {
    /**
     * 获取用户所有偏好设置
     *
     * @param userId 用户ID
     * @return 用户偏好设置列表
     */
    suspend fun getAllPreferences(userId: UUID): List<UserPreference>
    
    /**
     * 根据类别获取用户偏好设置
     *
     * @param userId 用户ID
     * @param category 偏好设置类别
     * @return 用户偏好设置列表
     */
    suspend fun getPreferencesByCategory(userId: UUID, category: PreferenceCategory): List<UserPreference>
    
    /**
     * 获取用户特定偏好设置
     *
     * @param userId 用户ID
     * @param key 偏好设置键
     * @return 用户偏好设置，如果不存在则返回null
     */
    suspend fun getPreference(userId: UUID, key: String): UserPreference?
    
    /**
     * 设置用户偏好
     *
     * @param preference 用户偏好设置
     * @return 保存后的用户偏好设置
     */
    suspend fun setPreference(preference: UserPreference): UserPreference
    
    /**
     * 批量设置用户偏好
     *
     * @param preferences 用户偏好设置列表
     * @return 保存后的用户偏好设置列表
     */
    suspend fun setPreferences(preferences: List<UserPreference>): List<UserPreference>
    
    /**
     * 删除用户特定偏好设置
     *
     * @param userId 用户ID
     * @param key 偏好设置键
     * @return 如果删除成功则返回true，否则返回false
     */
    suspend fun deletePreference(userId: UUID, key: String): Boolean
    
    /**
     * 删除用户某类别的所有偏好设置
     *
     * @param userId 用户ID
     * @param category 偏好设置类别
     * @return 删除的偏好设置数量
     */
    suspend fun deletePreferencesByCategory(userId: UUID, category: PreferenceCategory): Int
    
    /**
     * 删除用户所有偏好设置
     *
     * @param userId 用户ID
     * @return 删除的偏好设置数量
     */
    suspend fun deleteAllPreferences(userId: UUID): Int
    
    /**
     * 获取用户特定偏好设置值
     *
     * @param userId 用户ID
     * @param key 偏好设置键
     * @param defaultValue 默认值（如果偏好设置不存在）
     * @return 偏好设置值，如果不存在则返回默认值
     */
    suspend fun getPreferenceValue(userId: UUID, key: String, defaultValue: String): String
    
    /**
     * 导出用户偏好设置
     *
     * @param userId 用户ID
     * @return 导出的偏好设置JSON字符串
     */
    suspend fun exportPreferences(userId: UUID): String
    
    /**
     * 导入用户偏好设置
     *
     * @param userId 用户ID
     * @param preferences 偏好设置JSON字符串
     * @param overwrite 是否覆盖已存在的设置
     * @return 导入的偏好设置数量
     */
    suspend fun importPreferences(userId: UUID, preferences: String, overwrite: Boolean = true): Int
} 