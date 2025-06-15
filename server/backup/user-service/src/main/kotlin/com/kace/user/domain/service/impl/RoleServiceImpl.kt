package com.kace.user.domain.service.impl

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Role
import com.kace.user.domain.model.RoleCreateRequest
import com.kace.user.domain.model.RoleUpdateRequest
import com.kace.user.domain.model.UserRole
import com.kace.user.domain.repository.RoleRepository
import com.kace.user.domain.service.RoleService
import java.time.Instant
import java.util.UUID

/**
 * 角色服务实现
 */
class RoleServiceImpl(
    private val roleRepository: RoleRepository
) : RoleService {
    /**
     * 获取角色
     */
    override suspend fun getRole(id: String): Role? {
        return roleRepository.getById(id)
    }
    
    /**
     * 根据名称获取角色
     */
    override suspend fun getRoleByName(name: String, organizationId: String?): Role? {
        return roleRepository.getByName(name, organizationId)
    }
    
    /**
     * 获取角色列表
     */
    override suspend fun getRoles(page: Int, size: Int, organizationId: String?, query: String?): PageDto<Role> {
        return roleRepository.getRoles(page, size, organizationId, query)
    }
    
    /**
     * 创建角色
     */
    override suspend fun createRole(request: RoleCreateRequest): Role {
        val now = Instant.now()
        val role = Role(
            id = UUID.randomUUID().toString(),
            name = request.name,
            description = request.description,
            permissions = request.permissions,
            organizationId = request.organizationId,
            isSystem = false,
            createdAt = now,
            updatedAt = now
        )
        
        return roleRepository.createRole(role)
    }
    
    /**
     * 更新角色
     */
    override suspend fun updateRole(id: String, request: RoleUpdateRequest): Role {
        val existingRole = roleRepository.getById(id)
            ?: throw NoSuchElementException("角色不存在: $id")
        
        val updatedRole = existingRole.copy(
            name = request.name ?: existingRole.name,
            description = request.description ?: existingRole.description,
            permissions = request.permissions ?: existingRole.permissions,
            updatedAt = Instant.now()
        )
        
        return roleRepository.updateRole(updatedRole)
    }
    
    /**
     * 删除角色
     */
    override suspend fun deleteRole(id: String): Boolean {
        val role = roleRepository.getById(id)
            ?: return false
        
        if (role.isSystem) {
            throw IllegalStateException("系统角色不能删除")
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
    override suspend fun assignRoleToUser(roleId: String, userId: String, assignedBy: String): Boolean {
        val role = roleRepository.getById(roleId)
            ?: throw NoSuchElementException("角色不存在: $roleId")
        
        val userRole = UserRole(
            userId = userId,
            roleId = roleId,
            assignedAt = Instant.now(),
            assignedBy = assignedBy
        )
        
        return roleRepository.assignRoleToUser(userRole)
    }
    
    /**
     * 移除用户的角色
     */
    override suspend fun removeRoleFromUser(roleId: String, userId: String): Boolean {
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
    override suspend fun initSystemRoles(): List<Role> {
        val now = Instant.now()
        
        // 创建系统角色
        val adminRole = Role(
            id = UUID.randomUUID().toString(),
            name = "管理员",
            description = "系统管理员，拥有所有权限",
            permissions = listOf("*"),  // 所有权限
            isSystem = true,
            organizationId = null,
            createdAt = now,
            updatedAt = now
        )
        
        val userRole = Role(
            id = UUID.randomUUID().toString(),
            name = "普通用户",
            description = "普通用户，拥有基本权限",
            permissions = listOf(
                "user:read",
                "content:read",
                "media:read"
            ),
            isSystem = true,
            organizationId = null,
            createdAt = now,
            updatedAt = now
        )
        
        val guestRole = Role(
            id = UUID.randomUUID().toString(),
            name = "访客",
            description = "访客，只有查看权限",
            permissions = listOf(
                "content:read"
            ),
            isSystem = true,
            organizationId = null,
            createdAt = now,
            updatedAt = now
        )
        
        val roles = listOf(adminRole, userRole, guestRole)
        val createdRoles = mutableListOf<Role>()
        
        for (role in roles) {
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