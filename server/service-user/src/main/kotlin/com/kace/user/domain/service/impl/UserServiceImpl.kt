package com.kace.user.domain.service.impl

import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.User
import com.kace.user.domain.model.UserProfile
import com.kace.user.domain.model.UserStatus
import com.kace.user.domain.repository.UserProfileRepository
import com.kace.user.domain.repository.UserRepository
import com.kace.user.domain.service.UserService
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 用户服务实现类
 */
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository
) : UserService {
    
    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    
    /**
     * 根据ID获取用户
     */
    override suspend fun getUserById(id: String): User? {
        return userRepository.findById(id)
    }
    
    /**
     * 根据用户名获取用户
     */
    override suspend fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
    
    /**
     * 根据邮箱获取用户
     */
    override suspend fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
    
    /**
     * 分页获取用户列表
     */
    override suspend fun getUsers(page: Int, size: Int, status: UserStatus?, query: String?): PageDto<User> {
        return userRepository.findAll(page, size, status, query)
    }
    
    /**
     * 创建用户
     */
    override suspend fun createUser(user: User): User {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.username) != null) {
            throw BadRequestException("用户名已存在")
        }
        
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(user.email) != null) {
            throw BadRequestException("邮箱已存在")
        }
        
        // 生成用户ID
        val userId = UUID.randomUUID().toString()
        
        // 创建用户
        val newUser = user.copy(
            id = userId,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        
        return userRepository.save(newUser)
    }
    
    /**
     * 更新用户
     */
    override suspend fun updateUser(user: User): User {
        // 检查用户是否存在
        val existingUser = userRepository.findById(user.id)
            ?: throw NotFoundException("用户不存在")
        
        // 检查用户名是否已被其他用户使用
        if (user.username != existingUser.username && userRepository.findByUsername(user.username) != null) {
            throw BadRequestException("用户名已存在")
        }
        
        // 检查邮箱是否已被其他用户使用
        if (user.email != existingUser.email && userRepository.findByEmail(user.email) != null) {
            throw BadRequestException("邮箱已存在")
        }
        
        // 更新用户
        val updatedUser = user.copy(
            updatedAt = Clock.System.now()
        )
        
        return userRepository.save(updatedUser)
    }
    
    /**
     * 更新用户状态
     */
    override suspend fun updateUserStatus(id: String, status: UserStatus): Boolean {
        // 检查用户是否存在
        val existingUser = userRepository.findById(id)
            ?: throw NotFoundException("用户不存在")
        
        // 更新用户状态
        val updatedUser = existingUser.copy(
            status = status,
            updatedAt = Clock.System.now()
        )
        
        return userRepository.save(updatedUser) != null
    }
    
    /**
     * 删除用户
     */
    override suspend fun deleteUser(id: String): Boolean {
        // 检查用户是否存在
        if (userRepository.findById(id) == null) {
            throw NotFoundException("用户不存在")
        }
        
        // 删除用户资料
        userProfileRepository.deleteByUserId(id)
        
        // 删除用户
        return userRepository.deleteById(id)
    }
    
    /**
     * 获取用户资料
     */
    override suspend fun getUserProfile(userId: String): UserProfile? {
        // 检查用户是否存在
        if (userRepository.findById(userId) == null) {
            throw NotFoundException("用户不存在")
        }
        
        return userProfileRepository.findByUserId(userId)
    }
    
    /**
     * 创建或更新用户资料
     */
    override suspend fun saveUserProfile(profile: UserProfile): UserProfile {
        // 检查用户是否存在
        if (userRepository.findById(profile.userId) == null) {
            throw NotFoundException("用户不存在")
        }
        
        // 检查资料是否已存在
        val existingProfile = userProfileRepository.findByUserId(profile.userId)
        
        return if (existingProfile == null) {
            // 创建资料
            val newProfile = profile.copy(
                id = UUID.randomUUID().toString(),
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
            userProfileRepository.save(newProfile)
        } else {
            // 更新资料
            val updatedProfile = profile.copy(
                id = existingProfile.id,
                updatedAt = Clock.System.now()
            )
            userProfileRepository.save(updatedProfile)
        }
    }
    
    /**
     * 验证用户凭证
     */
    override suspend fun validateCredentials(username: String, password: String): User? {
        // 这个方法在用户服务中不实现，因为密码验证应该在认证服务中处理
        // 这里只是为了满足接口定义
        logger.warn("validateCredentials方法在用户服务中被调用，但此方法应在认证服务中实现")
        return null
    }
} 