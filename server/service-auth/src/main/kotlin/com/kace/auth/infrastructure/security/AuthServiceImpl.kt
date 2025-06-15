package com.kace.auth.infrastructure.security

import at.favre.lib.crypto.bcrypt.BCrypt
import com.kace.auth.domain.model.User
import com.kace.auth.domain.repository.UserRepository
import com.kace.auth.domain.service.AuthResult
import com.kace.auth.domain.service.AuthService
import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.exception.UnauthorizedException
import com.kace.common.security.jwt.JwtConfig
import java.time.Instant
import java.util.*

/**
 * 认证服务实现类
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
        // 查找用户
        val user = userRepository.findByUsername(username)
            ?: throw UnauthorizedException("用户名或密码错误", "INVALID_CREDENTIALS")
        
        // 验证密码
        val verifier = BCrypt.verifyer()
        val result = verifier.verify(password.toCharArray(), user.passwordHash.toCharArray())
        
        if (!result.verified) {
            throw UnauthorizedException("用户名或密码错误", "INVALID_CREDENTIALS")
        }
        
        // 检查用户状态
        if (!user.active) {
            throw UnauthorizedException("账户已被禁用", "ACCOUNT_DISABLED")
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
        // 检查用户名和邮箱是否已存在
        if (userRepository.findByUsername(username) != null) {
            throw BadRequestException("用户名已存在", "USERNAME_EXISTS")
        }
        
        if (userRepository.findByEmail(email) != null) {
            throw BadRequestException("邮箱已存在", "EMAIL_EXISTS")
        }
        
        // 密码哈希
        val passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        
        // 创建用户
        val user = User(
            username = username,
            email = email,
            passwordHash = passwordHash,
            firstName = firstName,
            lastName = lastName,
            active = true,
            verified = false
        )
        
        // 保存用户
        val savedUser = userRepository.create(user)
        
        // 生成令牌
        return generateAuthResult(savedUser)
    }
    
    /**
     * 刷新令牌
     */
    override suspend fun refreshToken(refreshToken: String): AuthResult {
        // 验证刷新令牌
        val userId = tokenStore.validateRefreshToken(refreshToken)
            ?: throw UnauthorizedException("无效的刷新令牌", "INVALID_REFRESH_TOKEN")
        
        // 获取用户
        val user = userRepository.findById(UUID.fromString(userId))
            ?: throw NotFoundException("用户不存在", "USER_NOT_FOUND")
        
        // 检查用户状态
        if (!user.active) {
            throw UnauthorizedException("账户已被禁用", "ACCOUNT_DISABLED")
        }
        
        // 生成新的令牌
        return generateAuthResult(user)
    }
    
    /**
     * 验证令牌
     */
    override suspend fun validateToken(token: String): Boolean {
        return try {
            // 验证JWT格式
            if (!jwtConfig.validateToken(token)) {
                return false
            }
            
            // 检查令牌是否已被注销
            if (tokenStore.isTokenBlacklisted(token)) {
                return false
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 从令牌中获取用户信息
     */
    override suspend fun getUserFromToken(token: String): User? {
        return try {
            // 验证令牌
            if (!validateToken(token)) {
                return null
            }
            
            // 从令牌中获取用户ID
            val userId = jwtConfig.getSubject(token) ?: return null
            
            // 获取用户
            userRepository.findById(UUID.fromString(userId))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 修改密码
     */
    override suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        // 获取用户
        val user = userRepository.findById(UUID.fromString(userId))
            ?: throw NotFoundException("用户不存在", "USER_NOT_FOUND")
        
        // 验证旧密码
        val verifier = BCrypt.verifyer()
        val result = verifier.verify(oldPassword.toCharArray(), user.passwordHash.toCharArray())
        
        if (!result.verified) {
            throw UnauthorizedException("旧密码错误", "INVALID_PASSWORD")
        }
        
        // 生成新密码哈希
        val newPasswordHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
        
        // 更新用户密码
        val updatedUser = user.copy(passwordHash = newPasswordHash, updatedAt = Instant.now())
        userRepository.update(updatedUser)
        
        return true
    }
    
    /**
     * 忘记密码
     */
    override suspend fun forgotPassword(email: String): Boolean {
        // 获取用户
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException("用户不存在", "USER_NOT_FOUND")
        
        // 生成重置令牌
        val resetToken = UUID.randomUUID().toString()
        
        // 存储重置令牌（实际实现中应该发送邮件）
        tokenStore.storeResetToken(user.id.toString(), resetToken, 24 * 60) // 24小时有效
        
        // TODO: 发送重置密码邮件
        
        return true
    }
    
    /**
     * 重置密码
     */
    override suspend fun resetPassword(resetToken: String, newPassword: String): Boolean {
        // 验证重置令牌
        val userId = tokenStore.validateResetToken(resetToken)
            ?: throw UnauthorizedException("无效的重置令牌", "INVALID_RESET_TOKEN")
        
        // 获取用户
        val user = userRepository.findById(UUID.fromString(userId))
            ?: throw NotFoundException("用户不存在", "USER_NOT_FOUND")
        
        // 生成新密码哈希
        val newPasswordHash = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())
        
        // 更新用户密码
        val updatedUser = user.copy(passwordHash = newPasswordHash, updatedAt = Instant.now())
        userRepository.update(updatedUser)
        
        // 删除重置令牌
        tokenStore.removeResetToken(resetToken)
        
        return true
    }
    
    /**
     * 退出登录
     */
    override suspend fun logout(token: String): Boolean {
        // 验证令牌
        if (!validateToken(token)) {
            return false
        }
        
        // 获取令牌过期时间
        val claims = jwtConfig.parseToken(token)
        val expiration = claims.expiration
        
        // 将令牌加入黑名单
        tokenStore.blacklistToken(token, expiration)
        
        return true
    }
    
    /**
     * 生成认证结果
     */
    private suspend fun generateAuthResult(user: User): AuthResult {
        // 生成访问令牌
        val claims = mapOf(
            "roles" to user.roles.map { it.name }
        )
        val accessToken = jwtConfig.createToken(user.id.toString(), claims)
        
        // 生成刷新令牌
        val refreshToken = UUID.randomUUID().toString()
        tokenStore.storeRefreshToken(user.id.toString(), refreshToken, 30 * 24 * 60) // 30天有效
        
        return AuthResult(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtConfig.getExpirationInMinutes() * 60 // 转换为秒
        )
    }
} 