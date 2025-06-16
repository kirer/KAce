package com.kace.user.infrastructure.persistence.repository

import com.kace.user.domain.model.PreferenceCategory
import com.kace.user.infrastructure.persistence.entity.UserPreferences
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * 用户偏好设置JPA仓库接口
 */
@Repository
interface JpaUserPreferenceRepository : JpaRepository<UserPreferences, UUID> {
    /**
     * 查询用户的所有偏好设置
     *
     * @param userId 用户ID
     * @return 用户偏好设置列表
     */
    fun findByUserId(userId: UUID): List<UserPreferences>
    
    /**
     * 按类别查询用户的偏好设置
     *
     * @param userId 用户ID
     * @param category 偏好设置类别
     * @return 用户偏好设置列表
     */
    fun findByUserIdAndCategory(userId: UUID, category: PreferenceCategory): List<UserPreferences>
    
    /**
     * 查找用户特定的偏好设置
     *
     * @param userId 用户ID
     * @param key 偏好设置键
     * @return 用户偏好设置，如果不存在则返回null
     */
    fun findByUserIdAndKey(userId: UUID, key: String): UserPreferences?
    
    /**
     * 删除用户特定的偏好设置
     *
     * @param userId 用户ID
     * @param key 偏好设置键
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM UserPreferences p WHERE p.userId = :userId AND p.key = :key")
    fun deleteByUserIdAndKey(userId: UUID, key: String): Int
    
    /**
     * 删除用户某类别的所有偏好设置
     *
     * @param userId 用户ID
     * @param category 偏好设置类别
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM UserPreferences p WHERE p.userId = :userId AND p.category = :category")
    fun deleteByUserIdAndCategory(userId: UUID, category: PreferenceCategory): Int
    
    /**
     * 删除用户所有偏好设置
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM UserPreferences p WHERE p.userId = :userId")
    fun deleteAllByUserId(userId: UUID): Int
} 