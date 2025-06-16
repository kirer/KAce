package com.kace.user.infrastructure.persistence.repository

import com.kace.user.infrastructure.persistence.entity.UserCredentials
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

/**
 * 用户凭证JPA仓库接口
 */
@Repository
interface JpaUserCredentialRepository : JpaRepository<UserCredentials, UUID> {
    /**
     * 根据用户ID查找凭证
     *
     * @param userId 用户ID
     * @return 用户凭证实体，如果不存在则返回null
     */
    fun findByUserId(userId: UUID): UserCredentials?
    
    /**
     * 根据重置密码令牌查找凭证
     *
     * @param token 重置密码令牌
     * @return 用户凭证实体，如果不存在则返回null
     */
    fun findByPasswordResetToken(token: String): UserCredentials?
    
    /**
     * 根据重置密码令牌查找用户ID
     *
     * @param token 重置密码令牌
     * @param now 当前时间
     * @return 用户ID，如果令牌不存在或已过期则返回null
     */
    @Query("SELECT c.userId FROM UserCredentials c WHERE c.passwordResetToken = :token AND c.passwordResetExpires > :now")
    fun findUserIdByValidResetToken(token: String, now: Instant): UUID?
    
    /**
     * 清除重置密码令牌
     *
     * @param userId 用户ID
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE UserCredentials c SET c.passwordResetToken = NULL, c.passwordResetExpires = NULL, c.updatedAt = :now WHERE c.userId = :userId")
    fun clearResetToken(userId: UUID, now: Instant): Int
    
    /**
     * 更新最后登录时间
     *
     * @param userId 用户ID
     * @param lastLogin 最后登录时间
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE UserCredentials c SET c.lastLogin = :lastLogin, c.updatedAt = :lastLogin WHERE c.userId = :userId")
    fun updateLastLogin(userId: UUID, lastLogin: Instant): Int
} 