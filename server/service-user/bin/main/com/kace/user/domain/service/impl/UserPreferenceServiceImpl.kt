package com.kace.user.domain.service.impl

import com.kace.user.domain.model.PreferenceCategory
import com.kace.user.domain.model.UserPreference
import com.kace.user.domain.repository.UserPreferenceRepository
import com.kace.user.domain.service.UserPreferenceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 用户偏好设置服务实现
 */
@Service
class UserPreferenceServiceImpl(
    private val preferenceRepository: UserPreferenceRepository
) : UserPreferenceService {

    private val logger = LoggerFactory.getLogger(UserPreferenceServiceImpl::class.java)
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    
    /**
     * 获取用户所有偏好设置
     */
    override suspend fun getAllPreferences(userId: UUID): List<UserPreference> = withContext(Dispatchers.IO) {
        try {
            return@withContext preferenceRepository.findAllByUserId(userId)
        } catch (e: Exception) {
            logger.error("Error getting all preferences for user: $userId", e)
            return@withContext emptyList()
        }
    }
    
    /**
     * 根据类别获取用户偏好设置
     */
    override suspend fun getPreferencesByCategory(userId: UUID, category: PreferenceCategory): List<UserPreference> = withContext(Dispatchers.IO) {
        try {
            return@withContext preferenceRepository.findByUserIdAndCategory(userId, category)
        } catch (e: Exception) {
            logger.error("Error getting preferences by category for user: $userId, category: $category", e)
            return@withContext emptyList()
        }
    }
    
    /**
     * 获取用户特定偏好设置
     */
    override suspend fun getPreference(userId: UUID, key: String): UserPreference? = withContext(Dispatchers.IO) {
        try {
            return@withContext preferenceRepository.findByUserIdAndKey(userId, key)
        } catch (e: Exception) {
            logger.error("Error getting preference for user: $userId, key: $key", e)
            return@withContext null
        }
    }
    
    /**
     * 设置用户偏好
     */
    @Transactional
    override suspend fun setPreference(preference: UserPreference): UserPreference = withContext(Dispatchers.IO) {
        try {
            val now = Clock.System.now()
            
            val existing = preference.key.let { key ->
                preferenceRepository.findByUserIdAndKey(preference.userId, key)
            }
            
            val toSave = if (existing != null) {
                preference.copy(
                    id = existing.id,
                    createdAt = existing.createdAt,
                    updatedAt = now
                )
            } else {
                preference.copy(
                    createdAt = now,
                    updatedAt = now
                )
            }
            
            return@withContext preferenceRepository.save(toSave)
        } catch (e: Exception) {
            logger.error("Error setting preference for user: ${preference.userId}, key: ${preference.key}", e)
            throw e
        }
    }
    
    /**
     * 批量设置用户偏好
     */
    @Transactional
    override suspend fun setPreferences(preferences: List<UserPreference>): List<UserPreference> = withContext(Dispatchers.IO) {
        if (preferences.isEmpty()) {
            return@withContext emptyList()
        }
        
        try {
            val now = Clock.System.now()
            val userId = preferences.first().userId
            
            // 确保所有偏好设置属于同一个用户
            if (preferences.any { it.userId != userId }) {
                throw IllegalArgumentException("All preferences must belong to the same user")
            }
            
            // 获取现有的偏好设置
            val existingPrefs = preferenceRepository.findAllByUserId(userId)
                .associateBy { it.key }
            
            // 准备保存的偏好设置
            val toSave = preferences.map { pref ->
                val existing = existingPrefs[pref.key]
                if (existing != null) {
                    pref.copy(
                        id = existing.id,
                        createdAt = existing.createdAt,
                        updatedAt = now
                    )
                } else {
                    pref.copy(
                        createdAt = now,
                        updatedAt = now
                    )
                }
            }
            
            return@withContext preferenceRepository.saveAll(toSave)
        } catch (e: Exception) {
            logger.error("Error setting multiple preferences", e)
            throw e
        }
    }
    
    /**
     * 删除用户特定偏好设置
     */
    @Transactional
    override suspend fun deletePreference(userId: UUID, key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            return@withContext preferenceRepository.deleteByUserIdAndKey(userId, key)
        } catch (e: Exception) {
            logger.error("Error deleting preference for user: $userId, key: $key", e)
            return@withContext false
        }
    }
    
    /**
     * 删除用户某类别的所有偏好设置
     */
    @Transactional
    override suspend fun deletePreferencesByCategory(userId: UUID, category: PreferenceCategory): Int = withContext(Dispatchers.IO) {
        try {
            return@withContext preferenceRepository.deleteByUserIdAndCategory(userId, category)
        } catch (e: Exception) {
            logger.error("Error deleting preferences by category for user: $userId, category: $category", e)
            return@withContext 0
        }
    }
    
    /**
     * 删除用户所有偏好设置
     */
    @Transactional
    override suspend fun deleteAllPreferences(userId: UUID): Int = withContext(Dispatchers.IO) {
        try {
            return@withContext preferenceRepository.deleteAllByUserId(userId)
        } catch (e: Exception) {
            logger.error("Error deleting all preferences for user: $userId", e)
            return@withContext 0
        }
    }
    
    /**
     * 获取用户特定偏好设置值
     */
    override suspend fun getPreferenceValue(userId: UUID, key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        try {
            val preference = getPreference(userId, key)
            return@withContext preference?.value ?: defaultValue
        } catch (e: Exception) {
            logger.error("Error getting preference value for user: $userId, key: $key", e)
            return@withContext defaultValue
        }
    }
    
    /**
     * 导出用户偏好设置
     */
    override suspend fun exportPreferences(userId: UUID): String = withContext(Dispatchers.IO) {
        try {
            val preferences = getAllPreferences(userId)
            return@withContext json.encodeToString(ListSerializer(UserPreference.serializer()), preferences)
        } catch (e: Exception) {
            logger.error("Error exporting preferences for user: $userId", e)
            return@withContext "[]"
        }
    }
    
    /**
     * 导入用户偏好设置
     */
    @Transactional
    override suspend fun importPreferences(userId: UUID, preferences: String, overwrite: Boolean): Int = withContext(Dispatchers.IO) {
        try {
            val prefsToImport = json.decodeFromString(ListSerializer(UserPreference.serializer()), preferences)
                .map { it.copy(userId = userId) }
            
            if (prefsToImport.isEmpty()) {
                return@withContext 0
            }
            
            if (overwrite) {
                // 如果要覆盖，先获取现有的偏好设置
                val existingPrefs = preferenceRepository.findAllByUserId(userId)
                    .associateBy { it.key }
                
                val toSave = prefsToImport.map { pref ->
                    val existing = existingPrefs[pref.key]
                    if (existing != null) {
                        pref.copy(
                            id = existing.id,
                            createdAt = existing.createdAt,
                            updatedAt = Clock.System.now()
                        )
                    } else {
                        pref.copy(
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    }
                }
                
                val saved = preferenceRepository.saveAll(toSave)
                return@withContext saved.size
            } else {
                // 如果不覆盖，只导入不存在的偏好设置
                val existingKeys = preferenceRepository.findAllByUserId(userId)
                    .map { it.key }
                    .toSet()
                
                val toSave = prefsToImport.filter { it.key !in existingKeys }
                    .map { pref ->
                        pref.copy(
                            createdAt = Clock.System.now(),
                            updatedAt = Clock.System.now()
                        )
                    }
                
                if (toSave.isEmpty()) {
                    return@withContext 0
                }
                
                val saved = preferenceRepository.saveAll(toSave)
                return@withContext saved.size
            }
        } catch (e: Exception) {
            logger.error("Error importing preferences for user: $userId", e)
            return@withContext 0
        }
    }
} 