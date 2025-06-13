package com.kace.common.security.jwt

import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.*

/**
 * JWT配置类
 */
class JwtConfig(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    private val expirationInMinutes: Long,
    private val realm: String = "kace-app"
) {
    private val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())
    
    /**
     * 创建JWT令牌
     */
    fun createToken(subject: String, claims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val expiration = Date(now.time + expirationInMinutes * 60 * 1000)
        
        return Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(expiration)
            .issuer(issuer)
            .audience().add(audience).and()
            .claims(claims)
            .signWith(key)
            .compact()
    }
    
    /**
     * 验证JWT令牌
     */
    fun validateToken(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 解析JWT令牌
     */
    fun parseToken(token: String): io.jsonwebtoken.Claims {
        return Jwts.parser()
            .verifyWith(key)
            .requireIssuer(issuer)
            .requireAudience(audience)
            .build()
            .parseSignedClaims(token)
            .payload
    }
    
    /**
     * 从JWT令牌中获取主题
     */
    fun getSubject(token: String): String? {
        return try {
            parseToken(token).subject
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 从JWT令牌中获取声明
     */
    fun getClaim(token: String, claimName: String): Any? {
        return try {
            parseToken(token)[claimName]
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 获取密钥
     */
    fun getSecretKey(): Key {
        return key
    }
    
    /**
     * 获取发行者
     */
    fun getIssuer(): String {
        return issuer
    }
    
    /**
     * 获取受众
     */
    fun getAudience(): String {
        return audience
    }
    
    /**
     * 获取领域
     */
    fun getRealm(): String {
        return realm
    }
} 