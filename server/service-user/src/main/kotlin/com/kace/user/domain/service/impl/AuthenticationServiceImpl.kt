package com.kace.user.domain.service.impl

import com.kace.user.domain.model.User
import com.kace.user.domain.repository.UserCredentialRepository
import com.kace.user.domain.repository.UserRepository
import com.kace.user.domain.service.AuthenticationService
import com.kace.user.infrastructure.security.JwtTokenProvider
import com.kace.user.infrastructure.security.PasswordEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * 用户认证服务实现
 */
@Service
class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val credentialRepository: UserCredentialRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: JwtTokenProvider
) : AuthenticationService {
    
    private val logger = LoggerFactory.getLogger(AuthenticationServiceImpl::class.java)
    
    /**
     * 用户登录
     */
    override suspend fun login(username: String, password: String): String? = withContext(Dispatchers.IO) {
        try {
            // 根据用户名查找用户ID
            val userId = credentialRepository.validateCredentials(username, passwordEncoder.encode(password))
                ?: return@withContext null
            
            // 更新最后登录时间
            credentialRepository.updateLastLogin(userId)
            
            // 生成JWT令牌
            val user = userRepository.findById(userId) ?: return@withContext null
            return@withContext tokenProvider.generateToken(user)
        } catch (e: Exception) {
            logger.error("Login failed for user: $username", e)
            return@withContext null
        }
    }
    
    /**
     * 用户登出
     */
    override suspend fun logout(token: String) = withContext(Dispatchers.IO) {
        tokenProvider.invalidateToken(token)
    }
    
    /**
     * 验证令牌有效性
     */
    override suspend fun validateToken(token: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext tokenProvider.validateToken(token)
    }
    
    /**
     * 刷新令牌
     */
    override suspend fun refreshToken(token: String): String? = withContext(Dispatchers.IO) {
        if (!tokenProvider.canBeRefreshed(token)) {
            return@withContext null
        }
        
        val userId = getUserIdFromToken(token) ?: return@withContext null
        val user = userRepository.findById(userId) ?: return@withContext null
        
        return@withContext tokenProvider.refreshToken(token, user)
    }
    
    /**
     * 从令牌中获取用户ID
     */
    override suspend fun getUserIdFromToken(token: String): UUID? = withContext(Dispatchers.IO) {
        return@withContext tokenProvider.getUserIdFromToken(token)
    }
    
    /**
     * 从令牌中获取用户
     */
    override suspend fun getUserFromToken(token: String): User? = withContext(Dispatchers.IO) {
        val userId = getUserIdFromToken(token) ?: return@withContext null
        return@withContext userRepository.findById(userId)
    }
    
    /**
     * 修改密码
     */
    override suspend fun changePassword(userId: UUID, oldPassword: String, newPassword: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentHash = credentialRepository.getPasswordHash(userId) ?: return@withContext false
            
            if (!passwordEncoder.matches(oldPassword, currentHash)) {
                return@withContext false
            }
            
            val newHash = passwordEncoder.encode(newPassword)
            return@withContext credentialRepository.setPasswordHash(userId, newHash)
        } catch (e: Exception) {
            logger.error("Failed to change password for user: $userId", e)
            return@withContext false
        }
    }
    
    /**
     * 重置密码
     */
    override suspend fun resetPassword(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = userRepository.findByEmail(email) ?: return@withContext false
            
            // 创建重置密码令牌
            val token = credentialRepository.createResetPasswordToken(user.id, 30)
                ?: return@withContext false
            
            // 这里应该发送邮件给用户，包含重置密码链接
            // emailService.sendPasswordResetEmail(user.email, token)
            
            return@withContext true
        } catch (e: Exception) {
            logger.error("Failed to reset password for email: $email", e)
            return@withContext false
        }
    }
    
    /**
     * 验证重置密码令牌
     */
    override suspend fun validateResetPasswordToken(token: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext credentialRepository.validateResetPasswordToken(token) != null
    }
    
    /**
     * 使用重置密码令牌更新密码
     */
    override suspend fun completePasswordReset(token: String, newPassword: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val userId = credentialRepository.validateResetPasswordToken(token) ?: return@withContext false
            
            val newHash = passwordEncoder.encode(newPassword)
            val success = credentialRepository.setPasswordHash(userId, newHash)
            
            if (success) {
                credentialRepository.consumeResetPasswordToken(token)
            }
            
            return@withContext success
        } catch (e: Exception) {
            logger.error("Failed to complete password reset", e)
            return@withContext false
        }
    }
} 