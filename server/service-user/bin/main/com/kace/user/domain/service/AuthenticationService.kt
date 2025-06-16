package com.kace.user.domain.service

import com.kace.user.domain.model.User
import java.util.UUID

/**
 * 用户认证服务接口
 */
interface AuthenticationService {
    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 认证令牌，如果认证失败则返回null
     */
    suspend fun login(username: String, password: String): String?
    
    /**
     * 用户登出
     *
     * @param token 认证令牌
     */
    suspend fun logout(token: String)
    
    /**
     * 验证令牌有效性
     *
     * @param token 认证令牌
     * @return 如果令牌有效则返回true，否则返回false
     */
    suspend fun validateToken(token: String): Boolean
    
    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌，如果原令牌无效则返回null
     */
    suspend fun refreshToken(token: String): String?
    
    /**
     * 从令牌中获取用户ID
     *
     * @param token 认证令牌
     * @return 用户ID，如果令牌无效则返回null
     */
    suspend fun getUserIdFromToken(token: String): UUID?
    
    /**
     * 从令牌中获取用户
     *
     * @param token 认证令牌
     * @return 用户对象，如果令牌无效则返回null
     */
    suspend fun getUserFromToken(token: String): User?
    
    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 如果修改成功则返回true，否则返回false
     */
    suspend fun changePassword(userId: UUID, oldPassword: String, newPassword: String): Boolean
    
    /**
     * 重置密码
     *
     * @param email 用户邮箱
     * @return 如果重置成功则返回true，否则返回false
     */
    suspend fun resetPassword(email: String): Boolean
    
    /**
     * 验证重置密码令牌
     *
     * @param token 重置密码令牌
     * @return 如果令牌有效则返回true，否则返回false
     */
    suspend fun validateResetPasswordToken(token: String): Boolean
    
    /**
     * 使用重置密码令牌更新密码
     *
     * @param token 重置密码令牌
     * @param newPassword 新密码
     * @return 如果更新成功则返回true，否则返回false
     */
    suspend fun completePasswordReset(token: String, newPassword: String): Boolean
} 