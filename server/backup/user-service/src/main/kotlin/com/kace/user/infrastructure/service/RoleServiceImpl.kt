package com.kace.user.infrastructure.service

import com.kace.common.exception.EntityNotFoundException
import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Role
import com.kace.user.domain.model.RoleCreateRequest
import com.kace.user.domain.model.RoleUpdateRequest
import com.kace.user.domain.model.UserRole
import com.kace.user.domain.repository.RoleRepository
import com.kace.user.domain.service.RoleService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 角色服务实现
 */
class RoleServiceImpl(private val roleRepository: RoleRepository) : RoleService {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 根据ID获取角色
     */
    override suspend fun getRoleById(id: String): Role? {
        return roleRepository.getById(id)
    }
    
    /**
     * 根据名称获取角色
     */
    override suspend fun getRoleByName(name: String, organizationId: String?): Role? {
        return roleRepository.getByName(name, organizationId)
    }
    
    /**
     * 分页获取角色列表
     */
    override suspend fun getRoles(
        page: Int,
        size: Int,
        organizationId: String?,
        query: String?
    ): PageDto<Role> {
        return roleRepository.getRoles(page, size, organizationId, query)
    }
    
    /**
     * 创建角色
     */
    override suspend fun createRole(request: RoleCreateRequest, createdBy: String): Role {
        // 检查角色名称是否已存在
        roleRepository.getByName(request.name, request.organizationId)?.let {
            throw IllegalArgumentException("角色名称 '${request.name}' 已存在")
        }
        
        val now = Clock.System.now()
        val role = Role(
            id = UUID.randomUUID().toString(),
            name = request.name,
            description = request.description,
            permissions = request.permissions,
            isSystem = false,
            organizationId = request.organizationId,
            createdAt = now,
            updatedAt = now
        )
        
        return roleRepository.createRole(role)
    }
    
    /**
     * 更新角色
     */
    override suspend fun updateRole(id: String, request: RoleUpdateRequest, updatedBy: String): Role {
        val existingRole = roleRepository.getById(id)
            ?: throw EntityNotFoundException("角色不存在: $id")
        
        // 系统角色不允许修改
        if (existingRole.isSystem) {
            throw IllegalArgumentException("系统角色不允许修改")
        }
        
        // 如果名称变更，检查是否与其他角色冲突
        if (request.name != null && request.name != existingRole.name) {
            roleRepository.getByName(request.name, existingRole.organizationId)?.let {
                throw IllegalArgumentException("角色名称 '${request.name}' 已存在")
            }
        }
        
        val updatedRole = existingRole.copy(
            name = request.name ?: existingRole.name,
            description = request.description ?: existingRole.description,
            permissions = request.permissions ?: existingRole.permissions,
            updatedAt = Clock.System.now()
        )
        
        return roleRepository.updateRole(updatedRole)
    }
    
    /**
     * 删除角色
     */
    override suspend fun deleteRole(id: String): Boolean {
        val role = roleRepository.getById(id)
            ?: throw EntityNotFoundException("角色不存在: $id")
        
        // 系统角色不允许删除
        if (role.isSystem) {
            throw IllegalArgumentException("系统角色不允许删除")
        }
        
        return roleRepository.deleteRole(id)
    }
    
    /**
     * 获取用户的所有角色
     */
    override suspend fun getUserRoles(userId: String): List<Role> {
        return roleRepository.getUserRoles(userId)
    }
    
    /**
     * 获取角色的所有用户
     */
    override suspend fun getRoleUsers(roleId: String, page: Int, size: Int): PageDto<String> {
        return roleRepository.getRoleUsers(roleId, page, size)
    }
    
    /**
     * 为用户分配角色
     */
    override suspend fun assignRoleToUser(userId: String, roleId: String, assignedBy: String): Boolean {
        // 检查角色是否存在
        val role = roleRepository.getById(roleId)
            ?: throw EntityNotFoundException("角色不存在: $roleId")
        
        // 检查用户是否已有此角色
        if (roleRepository.hasRole(userId, roleId)) {
            return true // 用户已有此角色，视为成功
        }
        
        val userRole = UserRole(
            userId = userId,
            roleId = roleId,
            assignedAt = Clock.System.now(),
            assignedBy = assignedBy
        )
        
        return roleRepository.assignRoleToUser(userRole)
    }
    
    /**
     * 移除用户的角色
     */
    override suspend fun removeRoleFromUser(userId: String, roleId: String): Boolean {
        return roleRepository.removeRoleFromUser(userId, roleId)
    }
    
    /**
     * 检查用户是否拥有指定角色
     */
    override suspend fun hasRole(userId: String, roleId: String): Boolean {
        return roleRepository.hasRole(userId, roleId)
    }
    
    /**
     * 检查用户是否拥有指定角色名称
     */
    override suspend fun hasRoleByName(userId: String, roleName: String): Boolean {
        return roleRepository.hasRoleByName(userId, roleName)
    }
    
    /**
     * 初始化系统角色
     */
    override suspend fun initSystemRoles(createdBy: String): List<Role> {
        val now = Clock.System.now()
        val systemRoles = listOf(
            Role(
                id = UUID.randomUUID().toString(),
                name = "ADMIN",
                description = "系统管理员，拥有所有权限",
                permissions = emptyList(), // 初始化时为空，后续会通过权限管理添加
                isSystem = true,
                organizationId = null,
                createdAt = now,
                updatedAt = now
            ),
            Role(
                id = UUID.randomUUID().toString(),
                name = "USER",
                description = "普通用户，拥有基本权限",
                permissions = emptyList(),
                isSystem = true,
                organizationId = null,
                createdAt = now,
                updatedAt = now
            ),
            Role(
                id = UUID.randomUUID().toString(),
                name = "GUEST",
                description = "访客，拥有最低权限",
                permissions = emptyList(),
                isSystem = true,
                organizationId = null,
                createdAt = now,
                updatedAt = now
            )
        )
        
        val createdRoles = mutableListOf<Role>()
        for (role in systemRoles) {
            // 检查角色是否已存在
            val existingRole = roleRepository.getByName(role.name, role.organizationId)
            if (existingRole == null) {
                createdRoles.add(roleRepository.createRole(role))
            } else {
                createdRoles.add(existingRole)
            }
        }
        
        return createdRoles
    }
} 