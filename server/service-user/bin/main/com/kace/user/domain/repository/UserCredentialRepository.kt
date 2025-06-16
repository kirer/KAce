package com.kace.user.domain.repository

import java.util.UUID

/**
 * 用户凭证仓库接口
 */
interface UserCredentialRepository {
    /**
     * 获取用户密码哈希
     *
     * @param userId 用户ID
     * @return 密码哈希值，如果不存在则返回null
     */
    suspend fun getPasswordHash(userId: UUID): String?
    
    /**
     * 设置用户密码哈希
     *
     * @param userId 用户ID
     * @param passwordHash 密码哈希值
     * @return 是否设置成功
     */
    suspend fun setPasswordHash(userId: UUID, passwordHash: String): Boolean
    
    /**
     * 验证用户名和密码
     *
     * @param username 用户名
     * @param passwordHash 密码哈希值
     * @return 用户ID，如果验证失败则返回null
     */
    suspend fun validateCredentials(username: String, passwordHash: String): UUID?
    
    /**
     * 创建重置密码令牌
     *
     * @param userId 用户ID
     * @param expirationInMinutes 过期时间（分钟）
     * @return 重置密码令牌，如果创建失败则返回null
     */
    suspend fun createResetPasswordToken(userId: UUID, expirationInMinutes: Int = 30): String?
    
    /**
     * 验证重置密码令牌
     *
     * @param token 重置密码令牌
     * @return 用户ID，如果验证失败则返回null
     */
    suspend fun validateResetPasswordToken(token: String): UUID?
    
    /**
     * 消费重置密码令牌
     *
     * @param token 重置密码令牌
     * @return 是否消费成功
     */
    suspend fun consumeResetPasswordToken(token: String): Boolean
    
    /**
     * 更新用户上次登录时间
     *
     * @param userId 用户ID
     * @return 是否更新成功
     */
    suspend fun updateLastLogin(userId: UUID): Boolean
} 