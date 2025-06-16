package com.kace.user.infrastructure.security

import com.kace.user.domain.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.SecretKey

/**
 * JWT令牌提供者
 */
@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secretString: String,
    
    @Value("\${jwt.token-validity-in-seconds:86400}")
    private val tokenValidityInSeconds: Long,
    
    @Value("\${jwt.refresh-token-validity-in-seconds:604800}")
    private val refreshTokenValidityInSeconds: Long
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    private val secretKey: SecretKey
    private val invalidatedTokens = ConcurrentHashMap<String, Instant>()
    
    init {
        secretKey = Keys.hmacShaKeyFor(secretString.toByteArray())
        
        // 启动定期清理失效令牌的任务
        startInvalidatedTokensCleanupTask()
    }
    
    /**
     * 生成JWT令牌
     *
     * @param user 用户对象
     * @return 生成的JWT令牌
     */
    fun generateToken(user: User): String {
        val claims = Jwts.claims().subject(user.id).build()
        
        val now = Instant.now()
        val validity = Date.from(now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS))
        
        return Jwts.builder()
            .claims(claims)
            .claim("username", user.username)
            .claim("roles", user.roles.joinToString(","))
            .issuedAt(Date.from(now))
            .expiration(validity)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }
    
    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID，如果令牌无效则返回null
     */
    fun getUserIdFromToken(token: String): UUID? {
        try {
            if (isTokenInvalidated(token)) {
                return null
            }
            
            val claims = getClaimsFromToken(token)
            return UUID.fromString(claims.subject)
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException -> logger.debug("Expired JWT token: {}", e.message)
                is UnsupportedJwtException -> logger.error("Unsupported JWT token: {}", e.message)
                is MalformedJwtException -> logger.error("Malformed JWT token: {}", e.message)
                is SignatureException -> logger.error("Invalid JWT signature: {}", e.message)
                is IllegalArgumentException -> logger.error("Invalid JWT token: {}", e.message)
                else -> logger.error("Error getting user ID from token: {}", e.message)
            }
            return null
        }
    }
    
    /**
     * 验证令牌有效性
     *
     * @param token JWT令牌
     * @return 如果令牌有效则返回true，否则返回false
     */
    fun validateToken(token: String): Boolean {
        try {
            if (isTokenInvalidated(token)) {
                return false
            }
            
            val claims = getClaimsFromToken(token)
            val expiration = claims.expiration.toInstant()
            return expiration.isAfter(Instant.now())
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException -> logger.debug("Expired JWT token: {}", e.message)
                is UnsupportedJwtException -> logger.error("Unsupported JWT token: {}", e.message)
                is MalformedJwtException -> logger.error("Malformed JWT token: {}", e.message)
                is SignatureException -> logger.error("Invalid JWT signature: {}", e.message)
                is IllegalArgumentException -> logger.error("Invalid JWT token: {}", e.message)
                else -> logger.error("Error validating token: {}", e.message)
            }
            return false
        }
    }
    
    /**
     * 使令牌失效
     *
     * @param token JWT令牌
     */
    fun invalidateToken(token: String) {
        try {
            val claims = getClaimsFromToken(token)
            val expiration = claims.expiration.toInstant()
            
            // 将令牌加入失效列表，直到其过期
            invalidatedTokens[token] = expiration
        } catch (e: Exception) {
            logger.error("Error invalidating token: {}", e.message)
        }
    }
    
    /**
     * 检查令牌是否可以刷新
     *
     * @param token JWT令牌
     * @return 如果令牌可以刷新则返回true，否则返回false
     */
    fun canBeRefreshed(token: String): Boolean {
        try {
            if (isTokenInvalidated(token)) {
                return false
            }
            
            val claims = getClaimsFromToken(token)
            val issuedAt = claims.issuedAt.toInstant()
            val refreshLimit = issuedAt.plus(refreshTokenValidityInSeconds, ChronoUnit.SECONDS)
            
            return refreshLimit.isAfter(Instant.now())
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @param user 用户对象
     * @return 新令牌
     */
    fun refreshToken(token: String, user: User): String {
        // 先使原令牌失效
        invalidateToken(token)
        
        // 生成新令牌
        return generateToken(user)
    }
    
    /**
     * 从令牌中获取Claims
     *
     * @param token JWT令牌
     * @return Claims对象
     */
    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
    
    /**
     * 检查令牌是否已被失效
     *
     * @param token JWT令牌
     * @return 如果令牌已被失效则返回true，否则返回false
     */
    private fun isTokenInvalidated(token: String): Boolean {
        return invalidatedTokens.containsKey(token)
    }
    
    /**
     * 启动定期清理失效令牌的任务
     */
    private fun startInvalidatedTokensCleanupTask() {
        Thread {
            while (true) {
                try {
                    // 每小时清理一次
                    Thread.sleep(1000 * 60 * 60)
                    cleanupInvalidatedTokens()
                } catch (e: InterruptedException) {
                    logger.error("Invalidated tokens cleanup task interrupted", e)
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }.apply {
            isDaemon = true
            start()
        }
    }
    
    /**
     * 清理已过期的失效令牌
     */
    private fun cleanupInvalidatedTokens() {
        val now = Instant.now()
        val iterator = invalidatedTokens.entries.iterator()
        
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.isBefore(now)) {
                iterator.remove()
            }
        }
    }
} 