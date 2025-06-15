package com.kace.auth.infrastructure.service

import com.kace.auth.domain.model.Role
import com.kace.auth.domain.model.User
import com.kace.auth.domain.repository.RoleRepository
import com.kace.auth.domain.repository.UserRepository
import com.kace.auth.domain.service.UserService
import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import com.kace.common.security.crypto.PasswordEncoder
import java.time.Instant
import java.util.*

/**
 * 用户服务实现
 */
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) : UserService {
    
    /**
     * 根据ID查找用户
     */
    override suspend fun findById(id: UUID): User? {
        return userRepository.findById(id)
    }
    
    /**
     * 根据用户名查找用户
     */
    override suspend fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
    
    /**
     * 根据邮箱查找用户
     */
    override suspend fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
    
    /**
     * 分页查询用户
     */
    override suspend fun findAll(pageRequest: PageRequest): PageResponse<User> {
        return userRepository.findAll(pageRequest)
    }
    
    /**
     * 创建用户
     */
    override suspend fun createUser(
        username: String,
        email: String,
        password: String,
        firstName: String?,
        lastName: String?,
        roleIds: Set<UUID>
    ): User {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw BadRequestException("用户名已存在")
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw BadRequestException("邮箱已存在")
        }
        
        // 获取角色
        val roles = if (roleIds.isNotEmpty()) {
            roleIds.mapNotNull { roleRepository.findById(it) }.toSet()
        } else {
            // 默认分配普通用户角色
            val userRole = roleRepository.findByName(Role.USER_ROLE)
            if (userRole != null) setOf(userRole) else emptySet()
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
            roles = roles,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        return userRepository.save(user)
    }
    
    /**
     * 更新用户
     */
    override suspend fun updateUser(
        id: UUID,
        username: String?,
        email: String?,
        firstName: String?,
        lastName: String?,
        active: Boolean?,
        verified: Boolean?,
        roleIds: Set<UUID>?
    ): User {
        val existingUser = userRepository.findById(id)
            ?: throw NotFoundException("用户不存在")
        
        // 检查用户名是否已存在
        if (username != null && username != existingUser.username && userRepository.existsByUsername(username)) {
            throw BadRequestException("用户名已存在")
        }
        
        // 检查邮箱是否已存在
        if (email != null && email != existingUser.email && userRepository.existsByEmail(email)) {
            throw BadRequestException("邮箱已存在")
        }
        
        // 获取角色
        val roles = if (roleIds != null) {
            roleIds.mapNotNull { roleRepository.findById(it) }.toSet()
        } else {
            existingUser.roles
        }
        
        // 更新用户
        val updatedUser = existingUser.copy(
            username = username ?: existingUser.username,
            email = email ?: existingUser.email,
            firstName = firstName ?: existingUser.firstName,
            lastName = lastName ?: existingUser.lastName,
            active = active ?: existingUser.active,
            verified = verified ?: existingUser.verified,
            roles = roles,
            updatedAt = Instant.now()
        )
        
        return userRepository.update(updatedUser)
    }
    
    /**
     * 删除用户
     */
    override suspend fun deleteUser(id: UUID): Boolean {
        return userRepository.delete(id)
    }
    
    /**
     * 获取用户
     */
    override suspend fun getUser(id: UUID): User {
        return userRepository.findById(id)
            ?: throw NotFoundException("用户不存在")
    }
    
    /**
     * 根据用户名获取用户
     */
    override suspend fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username)
            ?: throw NotFoundException("用户不存在")
    }
    
    /**
     * 根据邮箱获取用户
     */
    override suspend fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw NotFoundException("用户不存在")
    }
    
    /**
     * 分页获取所有用户
     */
    override suspend fun getAllUsers(pageRequest: PageRequest): PageResponse<User> {
        return userRepository.findAll(pageRequest)
    }
    
    /**
     * 修改用户密码
     */
    override suspend fun changePassword(id: UUID, oldPassword: String, newPassword: String): Boolean {
        val user = userRepository.findById(id)
            ?: throw NotFoundException("用户不存在")
        
        if (!PasswordEncoder.verify(oldPassword, user.passwordHash)) {
            throw BadRequestException("原密码错误")
        }
        
        val updatedUser = user.copy(
            passwordHash = PasswordEncoder.encode(newPassword),
            updatedAt = Instant.now()
        )
        
        userRepository.update(updatedUser)
        return true
    }
    
    /**
     * 重置用户密码
     */
    override suspend fun resetPassword(id: UUID, newPassword: String): Boolean {
        val user = userRepository.findById(id)
            ?: throw NotFoundException("用户不存在")
        
        val updatedUser = user.copy(
            passwordHash = PasswordEncoder.encode(newPassword),
            updatedAt = Instant.now()
        )
        
        userRepository.update(updatedUser)
        return true
    }
    
    /**
     * 为用户添加角色
     */
    override suspend fun addRoleToUser(userId: UUID, roleId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException("用户不存在")
        
        val role = roleRepository.findById(roleId)
            ?: throw NotFoundException("角色不存在")
        
        val updatedRoles = user.roles + role
        
        val updatedUser = user.copy(
            roles = updatedRoles,
            updatedAt = Instant.now()
        )
        
        return userRepository.update(updatedUser)
    }
    
    /**
     * 从用户中移除角色
     */
    override suspend fun removeRoleFromUser(userId: UUID, roleId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException("用户不存在")
        
        val updatedRoles = user.roles.filter { it.id != roleId }.toSet()
        
        val updatedUser = user.copy(
            roles = updatedRoles,
            updatedAt = Instant.now()
        )
        
        return userRepository.update(updatedUser)
    }
    
    /**
     * 验证用户
     */
    override suspend fun verifyUser(userId: UUID): User? {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException("用户不存在")
        
        val updatedUser = user.copy(
            verified = true,
            updatedAt = Instant.now()
        )
        
        return userRepository.update(updatedUser)
    }
    
    /**
     * 禁用用户
     */
    override suspend fun disableUser(userId: UUID): User? {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException("用户不存在")
        
        val updatedUser = user.copy(
            active = false,
            updatedAt = Instant.now()
        )
        
        return userRepository.update(updatedUser)
    }
    
    /**
     * 启用用户
     */
    override suspend fun enableUser(userId: UUID): User? {
        val user = userRepository.findById(userId)
            ?: throw NotFoundException("用户不存在")
        
        val updatedUser = user.copy(
            active = true,
            updatedAt = Instant.now()
        )
        
        return userRepository.update(updatedUser)
    }
} 