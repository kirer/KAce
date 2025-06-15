package com.kace.auth.domain.service

import com.kace.auth.domain.model.User

/**
 * 认证服务接口
 */
interface AuthService {
    /**
     * 用户登录
     */
    suspend fun login(username: String, password: String): AuthResult
    
    /**
     * 用户注册
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String? = null,
        lastName: String? = null
    ): AuthResult
    
    /**
     * 刷新令牌
     */
    suspend fun refreshToken(refreshToken: String): AuthResult
    
    /**
     * 验证令牌
     */
    suspend fun validateToken(token: String): Boolean
    
    /**
     * 从令牌中获取用户信息
     */
    suspend fun getUserFromToken(token: String): User?
    
    /**
     * 修改密码
     */
    suspend fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean
    
    /**
     * 忘记密码
     */
    suspend fun forgotPassword(email: String): Boolean
    
    /**
     * 重置密码
     */
    suspend fun resetPassword(resetToken: String, newPassword: String): Boolean
    
    /**
     * 退出登录
     */
    suspend fun logout(token: String): Boolean
}

/**
 * 认证结果数据类
 */
data class AuthResult(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
) 