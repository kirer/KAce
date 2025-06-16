package com.kace.user.infrastructure.persistence.repository

import com.kace.user.domain.repository.UserCredentialRepository
import com.kace.user.domain.repository.UserRepository
import com.kace.user.infrastructure.persistence.entity.UserCredentials
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.Base64

/**
 * 用户凭证仓库实现
 */
@Repository
class UserCredentialRepositoryImpl(
    private val jpaRepository: JpaUserCredentialRepository,
    private val userRepository: UserRepository
) : UserCredentialRepository {
    
    private val logger = LoggerFactory.getLogger(UserCredentialRepositoryImpl::class.java)
    private val secureRandom = SecureRandom()
    private val tokenEncoder = Base64.getUrlEncoder().withoutPadding()
    
    /**
     * 获取用户密码哈希
     */
    override suspend fun getPasswordHash(userId: UUID): String? {
        return jpaRepository.findByUserId(userId)?.passwordHash
    }
    
    /**
     * 设置用户密码哈希
     */
    @Transactional
    override suspend fun setPasswordHash(userId: UUID, passwordHash: String): Boolean {
        try {
            val now = Instant.now()
            val credential = jpaRepository.findByUserId(userId)
            
            if (credential != null) {
                // 更新现有凭证
                jpaRepository.save(
                    credential.copy(
                        passwordHash = passwordHash,
                        updatedAt = now
                    )
                )
            } else {
                // 创建新凭证
                jpaRepository.save(
                    UserCredentials(
                        userId = userId,
                        passwordHash = passwordHash,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }
            return true
        } catch (e: Exception) {
            logger.error("Failed to set password hash for user: $userId", e)
            return false
        }
    }
    
    /**
     * 验证用户名和密码
     */
    override suspend fun validateCredentials(username: String, passwordHash: String): UUID? {
        try {
            // 根据用户名或邮箱查找用户
            val user = userRepository.findByUsername(username)
                ?: userRepository.findByEmail(username)
                ?: return null
            
            // 查找用户凭证
            val credential = jpaRepository.findByUserId(UUID.fromString(user.id))
                ?: return null
            
            // 验证密码
            if (credential.passwordHash == passwordHash) {
                return UUID.fromString(user.id)
            }
            
            return null
        } catch (e: Exception) {
            logger.error("Error validating credentials for: $username", e)
            return null
        }
    }
    
    /**
     * 创建重置密码令牌
     */
    @Transactional
    override suspend fun createResetPasswordToken(userId: UUID, expirationInMinutes: Int): String? {
        try {
            val now = Instant.now()
            val expiration = now.plus(expirationInMinutes.toLong(), ChronoUnit.MINUTES)
            
            // 生成随机令牌
            val tokenBytes = ByteArray(32)
            secureRandom.nextBytes(tokenBytes)
            val token = tokenEncoder.encodeToString(tokenBytes)
            
            val credential = jpaRepository.findByUserId(userId)
            
            if (credential != null) {
                // 更新现有凭证
                jpaRepository.save(
                    credential.copy(
                        passwordResetToken = token,
                        passwordResetExpires = expiration,
                        updatedAt = now
                    )
                )
            } else {
                // 用户凭证不存在
                logger.error("Cannot create reset token for non-existent user credential: $userId")
                return null
            }
            
            return token
        } catch (e: Exception) {
            logger.error("Error creating reset token for user: $userId", e)
            return null
        }
    }
    
    /**
     * 验证重置密码令牌
     */
    override suspend fun validateResetPasswordToken(token: String): UUID? {
        try {
            // 验证令牌是否存在且未过期
            return jpaRepository.findUserIdByValidResetToken(token, Instant.now())
        } catch (e: Exception) {
            logger.error("Error validating reset token", e)
            return null
        }
    }
    
    /**
     * 消费重置密码令牌
     */
    @Transactional
    override suspend fun consumeResetPasswordToken(token: String): Boolean {
        try {
            val credential = jpaRepository.findByPasswordResetToken(token) ?: return false
            
            // 清除令牌
            jpaRepository.clearResetToken(credential.userId, Instant.now())
            
            return true
        } catch (e: Exception) {
            logger.error("Error consuming reset token", e)
            return false
        }
    }
    
    /**
     * 更新用户上次登录时间
     */
    @Transactional
    override suspend fun updateLastLogin(userId: UUID): Boolean {
        try {
            val now = Instant.now()
            jpaRepository.updateLastLogin(userId, now)
            return true
        } catch (e: Exception) {
            logger.error("Error updating last login for user: $userId", e)
            return false
        }
    }
    
    /**
     * 将JPA实体复制为新对象，因为Kotlin中的数据类是不可变的
     */
    private fun UserCredentials.copy(
        id: UUID? = this.id,
        userId: UUID = this.userId,
        passwordHash: String = this.passwordHash,
        lastLogin: Instant? = this.lastLogin,
        passwordResetToken: String? = this.passwordResetToken,
        passwordResetExpires: Instant? = this.passwordResetExpires,
        createdAt: Instant = this.createdAt,
        updatedAt: Instant = this.updatedAt
    ): UserCredentials {
        return UserCredentials(
            id = id,
            userId = userId,
            passwordHash = passwordHash,
            lastLogin = lastLogin,
            passwordResetToken = passwordResetToken,
            passwordResetExpires = passwordResetExpires,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 