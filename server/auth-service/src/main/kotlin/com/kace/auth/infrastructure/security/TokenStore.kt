package com.kace.auth.infrastructure.security

/**
 * 令牌存储接口
 */
interface TokenStore {
    /**
     * 存储刷新令牌
     */
    suspend fun storeRefreshToken(userId: String, refreshToken: String)
    
    /**
     * 验证刷新令牌
     * @return 用户ID，如果令牌无效则返回null
     */
    suspend fun validateRefreshToken(refreshToken: String): String?
    
    /**
     * 撤销令牌
     */
    suspend fun revokeToken(userId: String, token: String)
    
    /**
     * 撤销用户的所有令牌
     */
    suspend fun revokeAllTokens(userId: String)
    
    /**
     * 存储重置密码令牌
     */
    suspend fun storeResetToken(userId: String, resetToken: String)
    
    /**
     * 验证重置密码令牌
     * @return 用户ID，如果令牌无效则返回null
     */
    suspend fun validateResetToken(resetToken: String): String?
    
    /**
     * 撤销重置密码令牌
     */
    suspend fun revokeResetToken(userId: String)
} 