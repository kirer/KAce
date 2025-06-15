package com.kace.auth.infrastructure.security

import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.future.await
import java.time.Duration
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * 基于Redis的令牌存储实现
 */
class RedisTokenStore(
    private val redis: RedisAsyncCommands<String, String>
) : TokenStore {
    
    companion object {
        // Redis键前缀
        private const val REFRESH_TOKEN_PREFIX = "refresh_token:"
        private const val RESET_TOKEN_PREFIX = "reset_token:"
        private const val TOKEN_BLACKLIST_PREFIX = "token_blacklist:"
        
        // 用户ID到令牌的映射前缀
        private const val USER_REFRESH_TOKEN_PREFIX = "user_refresh_token:"
        private const val USER_RESET_TOKEN_PREFIX = "user_reset_token:"
    }
    
    /**
     * 存储刷新令牌
     */
    override suspend fun storeRefreshToken(userId: String, refreshToken: String, expirationMinutes: Long) {
        // 存储令牌到用户ID的映射
        val tokenKey = "$REFRESH_TOKEN_PREFIX$refreshToken"
        redis.set(tokenKey, userId).await()
        redis.expire(tokenKey, expirationMinutes * 60).await()
        
        // 存储用户ID到令牌的映射（用于撤销所有令牌）
        val userTokenKey = "$USER_REFRESH_TOKEN_PREFIX$userId:$refreshToken"
        redis.set(userTokenKey, refreshToken).await()
        redis.expire(userTokenKey, expirationMinutes * 60).await()
    }
    
    /**
     * 验证刷新令牌
     */
    override suspend fun validateRefreshToken(refreshToken: String): String? {
        val tokenKey = "$REFRESH_TOKEN_PREFIX$refreshToken"
        return redis.get(tokenKey).await()
    }
    
    /**
     * 存储密码重置令牌
     */
    override suspend fun storeResetToken(userId: String, resetToken: String, expirationMinutes: Long) {
        // 存储令牌到用户ID的映射
        val tokenKey = "$RESET_TOKEN_PREFIX$resetToken"
        redis.set(tokenKey, userId).await()
        redis.expire(tokenKey, expirationMinutes * 60).await()
        
        // 存储用户ID到令牌的映射
        val userTokenKey = "$USER_RESET_TOKEN_PREFIX$userId"
        redis.set(userTokenKey, resetToken).await()
        redis.expire(userTokenKey, expirationMinutes * 60).await()
    }
    
    /**
     * 验证密码重置令牌
     */
    override suspend fun validateResetToken(resetToken: String): String? {
        val tokenKey = "$RESET_TOKEN_PREFIX$resetToken"
        return redis.get(tokenKey).await()
    }
    
    /**
     * 删除密码重置令牌
     */
    override suspend fun removeResetToken(resetToken: String) {
        val tokenKey = "$RESET_TOKEN_PREFIX$resetToken"
        val userId = redis.get(tokenKey).await() ?: return
        
        // 删除令牌到用户ID的映射
        redis.del(tokenKey).await()
        
        // 删除用户ID到令牌的映射
        val userTokenKey = "$USER_RESET_TOKEN_PREFIX$userId"
        redis.del(userTokenKey).await()
    }
    
    /**
     * 将令牌加入黑名单
     */
    override suspend fun blacklistToken(token: String, expiration: Date) {
        val tokenKey = "$TOKEN_BLACKLIST_PREFIX$token"
        val now = System.currentTimeMillis()
        val ttl = expiration.time - now
        
        if (ttl > 0) {
            redis.set(tokenKey, "1").await()
            redis.expire(tokenKey, ttl, TimeUnit.MILLISECONDS).await()
        }
    }
    
    /**
     * 检查令牌是否在黑名单中
     */
    override suspend fun isTokenBlacklisted(token: String): Boolean {
        val tokenKey = "$TOKEN_BLACKLIST_PREFIX$token"
        val result = redis.get(tokenKey).await()
        return result != null
    }
} 