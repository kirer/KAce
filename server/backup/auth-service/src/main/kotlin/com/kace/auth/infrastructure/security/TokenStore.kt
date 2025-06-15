package com.kace.auth.infrastructure.security

import java.util.Date

/**
 * 令牌存储接口
 */
interface TokenStore {
    /**
     * 存储刷新令牌
     */
    suspend fun storeRefreshToken(userId: String, refreshToken: String, expirationMinutes: Long)
    
    /**
     * 验证刷新令牌
     * 
     * @return 如果令牌有效，返回用户ID；否则返回null
     */
    suspend fun validateRefreshToken(refreshToken: String): String?
    
    /**
     * 存储密码重置令牌
     */
    suspend fun storeResetToken(userId: String, resetToken: String, expirationMinutes: Long)
    
    /**
     * 验证密码重置令牌
     * 
     * @return 如果令牌有效，返回用户ID；否则返回null
     */
    suspend fun validateResetToken(resetToken: String): String?
    
    /**
     * 删除密码重置令牌
     */
    suspend fun removeResetToken(resetToken: String)
    
    /**
     * 将令牌加入黑名单
     */
    suspend fun blacklistToken(token: String, expiration: Date)
    
    /**
     * 检查令牌是否在黑名单中
     */
    suspend fun isTokenBlacklisted(token: String): Boolean
} 