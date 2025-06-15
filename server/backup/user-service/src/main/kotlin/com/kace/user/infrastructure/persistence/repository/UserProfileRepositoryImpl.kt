package com.kace.user.infrastructure.persistence.repository

import com.kace.user.domain.model.Address
import com.kace.user.domain.model.Gender
import com.kace.user.domain.model.UserPreferences
import com.kace.user.domain.model.UserProfile
import com.kace.user.domain.repository.UserProfileRepository
import com.kace.user.infrastructure.persistence.entity.UserProfileEntity
import com.kace.user.infrastructure.persistence.entity.UserProfiles
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * 用户资料仓库实现类
 */
class UserProfileRepositoryImpl : UserProfileRepository {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * 根据ID查找用户资料
     */
    override suspend fun findById(id: String): UserProfile? {
        return transaction {
            UserProfileEntity.findById(UUID.fromString(id))?.toModel()
        }
    }
    
    /**
     * 根据用户ID查找用户资料
     */
    override suspend fun findByUserId(userId: String): UserProfile? {
        return transaction {
            UserProfileEntity.find { UserProfiles.userId eq UUID.fromString(userId) }
                .firstOrNull()
                ?.toModel()
        }
    }
    
    /**
     * 保存用户资料
     */
    override suspend fun save(profile: UserProfile): UserProfile {
        return transaction {
            val existingProfile = profile.id.let { id ->
                try {
                    UserProfileEntity.findById(UUID.fromString(id))
                } catch (e: Exception) {
                    null
                }
            }
            
            if (existingProfile != null) {
                // 更新现有资料
                existingProfile.apply {
                    bio = profile.bio
                    phoneNumber = profile.phoneNumber
                    birthDate = profile.birthDate?.toString()
                    gender = profile.gender?.name
                    addressJson = profile.address?.let { json.encodeToString(it) }
                    preferencesJson = profile.preferences?.let { json.encodeToString(it) }
                    updatedAt = profile.updatedAt.toJavaInstant()
                }.toModel()
            } else {
                // 创建新资料
                UserProfileEntity.new(UUID.fromString(profile.id)) {
                    userId = UUID.fromString(profile.userId)
                    bio = profile.bio
                    phoneNumber = profile.phoneNumber
                    birthDate = profile.birthDate?.toString()
                    gender = profile.gender?.name
                    addressJson = profile.address?.let { json.encodeToString(it) }
                    preferencesJson = profile.preferences?.let { json.encodeToString(it) }
                    createdAt = profile.createdAt.toJavaInstant()
                    updatedAt = profile.updatedAt.toJavaInstant()
                }.toModel()
            }
        }
    }
    
    /**
     * 根据ID删除用户资料
     */
    override suspend fun deleteById(id: String): Boolean {
        return transaction {
            try {
                val profile = UserProfileEntity.findById(UUID.fromString(id))
                profile?.delete()
                profile != null
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * 根据用户ID删除用户资料
     */
    override suspend fun deleteByUserId(userId: String): Boolean {
        return transaction {
            try {
                val profile = UserProfileEntity.find { UserProfiles.userId eq UUID.fromString(userId) }
                    .firstOrNull()
                profile?.delete()
                profile != null
            } catch (e: Exception) {
                false
            }
        }
    }
    
    /**
     * 将实体转换为领域模型
     */
    private fun UserProfileEntity.toModel(): UserProfile {
        return UserProfile(
            id = id.value.toString(),
            userId = userId.toString(),
            bio = bio,
            phoneNumber = phoneNumber,
            birthDate = birthDate?.let { kotlinx.datetime.LocalDate.parse(it) },
            gender = gender?.let { Gender.valueOf(it) },
            address = addressJson?.let { json.decodeFromString<Address>(it) },
            preferences = preferencesJson?.let { json.decodeFromString<UserPreferences>(it) },
            createdAt = createdAt.toKotlinInstant(),
            updatedAt = updatedAt.toKotlinInstant()
        )
    }
} 