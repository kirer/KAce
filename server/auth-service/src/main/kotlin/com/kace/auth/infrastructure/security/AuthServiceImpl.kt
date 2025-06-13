package com.kace.auth.infrastructure.security

import com.kace.auth.domain.model.User
import com.kace.auth.domain.repository.UserRepository
import com.kace.auth.domain.service.AuthResult
import com.kace.auth.domain.service.AuthService
import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.exception.UnauthorizedException
import com.kace.common.security.crypto.PasswordEncoder
import com.kace.common.security.jwt.JwtConfig
import java.time.Instant
import java.util.*

/**
 * 认证服务实现
 */
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig,
    private val tokenStore: TokenStore
) : AuthService {
    
    /**
     * 用户登录
     */
    override suspend fun login(username: String, password: String): AuthResult {
        val user = userRepository.findByUsername(username)
            ?: throw UnauthorizedException("用户名或密码错误")
        
        if (!user.active) {
            throw UnauthorizedException("账户已被禁用")
        }
        
        if (!PasswordEncoder.verify(password, user.passwordHash)) {
            throw UnauthorizedException("用户名或密码错误")
        }
        
        // 更新最后登录时间
        val updatedUser = user.copy(lastLoginAt = Instant.now())
        userRepository.update(updatedUser)
        
        // 生成令牌
        return generateAuthResult(updatedUser)
    }
    
    /**
     * 用户注册
     */
    override suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): AuthResult {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw BadRequestException("用户名已存在")
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw BadRequestException("邮箱已存在")
        }
        
        // 创建用户
        val user = User(
            username = username,
            email = email,
            passwordHash = PasswordEncoder.encode(password),
            firstName = firstName,
            lastName = lastName,
            active = true,
            verified = false,
            roles = emptySet(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        val savedUser = userRepository.save(user)
        
        // 生成令牌
        return generateAuthResult(savedUser)
    }
    
    /**
     * 刷新令牌
     */
    override suspend fun refreshToken(refreshToken: String): AuthResult {
        // 验证刷新令牌
        val userId = tokenStore.validateRefreshToken(refreshToken)
            ?: throw UnauthorizedException("刷新令牌无效或已过期")
        
        // 获取用户信息
        val user = userRepository.findById(UUID.fromString(userId))
            ?: throw NotFoundException("用户不存在")
        
        if (!user.active) {
            throw UnauthorizedException("账户已被禁用")
        }
        
        // 生成新的令牌
        return generateAuthResult(user)
    }
    
    /**
     * 验证令牌
     */
    override suspend fun validateToken(token: String): Boolean {
        return jwtConfig.validateToken(token)
    }
    
    /**
     * 从令牌中获取用户信息
     */
    override suspend fun getUserFromToken(token: String): User? {
        try {
            val claims = jwtConfig.parseToken(token)
            val userId = claims.subject
            return userRepository.findById(UUID.fromString(userId))
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * 修改密码
     */
    override suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        val user = userRepository.findById(UUID.fromString(userId))
            ?: throw NotFoundException("用户不存在")
        
        if (!PasswordEncoder.verify(oldPassword, user.passwordHash)) {
            throw BadRequestException("原密码错误")
        }
        
        val updatedUser = user.copy(
            passwordHash = PasswordEncoder.encode(newPassword),
            updatedAt = Instant.now()
        )
        
        userRepository.update(updatedUser)
        
        // 使所有令牌失效
        tokenStore.revokeAllTokens(userId)
        
        return true
    }
    
    /**
     * 忘记密码
     */
    override suspend fun forgotPassword(email: String): Boolean {
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException("邮箱不存在")
        
        // 生成重置令牌
        val resetToken = UUID.randomUUID().toString()
        
        // 存储重置令牌
        tokenStore.storeResetToken(user.id.toString(), resetToken)
        
        // 发送重置密码邮件
        // 此处需要实现邮件发送逻辑
        
        return true
    }
    
    /**
     * 重置密码
     */
    override suspend fun resetPassword(resetToken: String, newPassword: String): Boolean {
        // 验证重置令牌
        val userId = tokenStore.validateResetToken(resetToken)
            ?: throw BadRequestException("重置令牌无效或已过期")
        
        val user = userRepository.findById(UUID.fromString(userId))
            ?: throw NotFoundException("用户不存在")
        
        val updatedUser = user.copy(
            passwordHash = PasswordEncoder.encode(newPassword),
            updatedAt = Instant.now()
        )
        
        userRepository.update(updatedUser)
        
        // 使重置令牌失效
        tokenStore.revokeResetToken(userId)
        
        // 使所有令牌失效
        tokenStore.revokeAllTokens(userId)
        
        return true
    }
    
    /**
     * 退出登录
     */
    override suspend fun logout(token: String): Boolean {
        try {
            val claims = jwtConfig.parseToken(token)
            val userId = claims.subject
            
            // 使令牌失效
            tokenStore.revokeToken(userId, token)
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * 生成认证结果
     */
    private fun generateAuthResult(user: User): AuthResult {
        val userId = user.id.toString()
        
        // 生成访问令牌
        val claims = mapOf(
            "roles" to user.roles.map { it.name },
            "email" to user.email
        )
        val accessToken = jwtConfig.createToken(userId, claims)
        
        // 生成刷新令牌
        val refreshToken = UUID.randomUUID().toString()
        
        // 存储刷新令牌
        tokenStore.storeRefreshToken(userId, refreshToken)
        
        return AuthResult(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtConfig.expirationInMinutes * 60
        )
    }
} 