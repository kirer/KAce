package com.kace.auth.infrastructure.security

import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.future.await
import java.util.concurrent.TimeUnit

/**
 * Redis令牌存储实现
 */
class RedisTokenStore(private val redis: RedisAsyncCommands<String, String>) : TokenStore {
    
    companion object {
        private const val REFRESH_TOKEN_PREFIX = "refresh_token:"
        private const val USER_TOKENS_PREFIX = "user_tokens:"
        private const val RESET_TOKEN_PREFIX = "reset_token:"
        
        // 刷新令牌有效期：30天
        private const val REFRESH_TOKEN_TTL = 30L * 24 * 60 * 60
        
        // 重置令牌有效期：1小时
        private const val RESET_TOKEN_TTL = 60L * 60
    }
    
    /**
     * 存储刷新令牌
     */
    override suspend fun storeRefreshToken(userId: String, refreshToken: String) {
        // 存储刷新令牌 -> 用户ID的映射
        redis.set("$REFRESH_TOKEN_PREFIX$refreshToken", userId).await()
        redis.expire("$REFRESH_TOKEN_PREFIX$refreshToken", REFRESH_TOKEN_TTL).await()
        
        // 存储用户ID -> 刷新令牌的映射
        redis.sadd("$USER_TOKENS_PREFIX$userId", refreshToken).await()
    }
    
    /**
     * 验证刷新令牌
     */
    override suspend fun validateRefreshToken(refreshToken: String): String? {
        return redis.get("$REFRESH_TOKEN_PREFIX$refreshToken").await()
    }
    
    /**
     * 撤销令牌
     */
    override suspend fun revokeToken(userId: String, token: String) {
        // 删除刷新令牌
        redis.del("$REFRESH_TOKEN_PREFIX$token").await()
        
        // 从用户令牌集合中移除
        redis.srem("$USER_TOKENS_PREFIX$userId", token).await()
    }
    
    /**
     * 撤销用户的所有令牌
     */
    override suspend fun revokeAllTokens(userId: String) {
        // 获取用户所有令牌
        val tokens = redis.smembers("$USER_TOKENS_PREFIX$userId").await()
        
        // 删除所有刷新令牌
        tokens.forEach { token ->
            redis.del("$REFRESH_TOKEN_PREFIX$token").await()
        }
        
        // 清空用户令牌集合
        redis.del("$USER_TOKENS_PREFIX$userId").await()
    }
    
    /**
     * 存储重置密码令牌
     */
    override suspend fun storeResetToken(userId: String, resetToken: String) {
        // 存储重置令牌 -> 用户ID的映射
        redis.set("$RESET_TOKEN_PREFIX$resetToken", userId).await()
        redis.expire("$RESET_TOKEN_PREFIX$resetToken", RESET_TOKEN_TTL).await()
    }
    
    /**
     * 验证重置密码令牌
     */
    override suspend fun validateResetToken(resetToken: String): String? {
        return redis.get("$RESET_TOKEN_PREFIX$resetToken").await()
    }
    
    /**
     * 撤销重置密码令牌
     */
    override suspend fun revokeResetToken(userId: String) {
        // 由于重置令牌是一次性的，且有过期时间，不需要额外的撤销逻辑
        // 如果需要强制撤销，可以实现查找和删除逻辑
    }
} 