package com.kace.user.infrastructure.persistence.repository

import com.kace.user.domain.model.PreferenceCategory
import com.kace.user.domain.model.UserPreference
import com.kace.user.domain.repository.UserPreferenceRepository
import com.kace.user.infrastructure.persistence.entity.UserPreferences
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 用户偏好设置仓库实现
 */
@Repository
class UserPreferenceRepositoryImpl(
    private val jpaRepository: JpaUserPreferenceRepository
) : UserPreferenceRepository {
    
    /**
     * 获取用户所有偏好设置
     */
    override suspend fun findAllByUserId(userId: UUID): List<UserPreference> {
        return jpaRepository.findByUserId(userId).map { it.toDomain() }
    }
    
    /**
     * 根据类别获取用户偏好设置
     */
    override suspend fun findByUserIdAndCategory(userId: UUID, category: PreferenceCategory): List<UserPreference> {
        return jpaRepository.findByUserIdAndCategory(userId, category).map { it.toDomain() }
    }
    
    /**
     * 获取用户特定偏好设置
     */
    override suspend fun findByUserIdAndKey(userId: UUID, key: String): UserPreference? {
        return jpaRepository.findByUserIdAndKey(userId, key)?.toDomain()
    }
    
    /**
     * 保存用户偏好设置
     */
    @Transactional
    override suspend fun save(preference: UserPreference): UserPreference {
        val entity = preference.toEntity()
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    /**
     * 批量保存用户偏好设置
     */
    @Transactional
    override suspend fun saveAll(preferences: List<UserPreference>): List<UserPreference> {
        val entities = preferences.map { it.toEntity() }
        val savedEntities = jpaRepository.saveAll(entities)
        return savedEntities.map { it.toDomain() }
    }
    
    /**
     * 删除用户特定偏好设置
     */
    @Transactional
    override suspend fun deleteByUserIdAndKey(userId: UUID, key: String): Boolean {
        return jpaRepository.deleteByUserIdAndKey(userId, key) > 0
    }
    
    /**
     * 删除用户某类别的所有偏好设置
     */
    @Transactional
    override suspend fun deleteByUserIdAndCategory(userId: UUID, category: PreferenceCategory): Int {
        return jpaRepository.deleteByUserIdAndCategory(userId, category)
    }
    
    /**
     * 删除用户所有偏好设置
     */
    @Transactional
    override suspend fun deleteAllByUserId(userId: UUID): Int {
        return jpaRepository.deleteAllByUserId(userId)
    }
    
    /**
     * 将JPA实体转换为领域模型
     */
    private fun UserPreferences.toDomain(): UserPreference {
        return UserPreference(
            id = this.id,
            userId = this.userId,
            key = this.key,
            value = this.value,
            category = this.category,
            createdAt = this.createdAt.toKotlinInstant(),
            updatedAt = this.updatedAt.toKotlinInstant()
        )
    }
    
    /**
     * 将领域模型转换为JPA实体
     */
    private fun UserPreference.toEntity(): UserPreferences {
        return UserPreferences(
            id = this.id,
            userId = this.userId,
            key = this.key,
            value = this.value,
            category = this.category,
            createdAt = this.createdAt?.toJavaInstant() ?: java.time.Instant.now(),
            updatedAt = this.updatedAt?.toJavaInstant() ?: java.time.Instant.now()
        )
    }
} 