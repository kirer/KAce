package com.kace.auth.infrastructure.service

import com.kace.auth.domain.model.Role
import com.kace.auth.domain.repository.PermissionRepository
import com.kace.auth.domain.repository.RoleRepository
import com.kace.auth.domain.service.RoleService
import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.time.Instant
import java.util.*

/**
 * 角色服务实现
 */
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) : RoleService {

    /**
     * 创建角色
     */
    override suspend fun createRole(name: String, description: String?, permissionIds: Set<UUID>): Role {
        // 检查角色名是否已存在
        if (roleRepository.existsByName(name)) {
            throw BadRequestException("角色名已存在: $name")
        }
        
        // 获取权限
        val permissions = if (permissionIds.isNotEmpty()) {
            permissionIds.mapNotNull { permissionRepository.findById(it) }.toSet()
        } else {
            emptySet()
        }
        
        // 创建角色
        val role = Role(
            name = name,
            description = description,
            permissions = permissions,
            isSystem = false,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        return roleRepository.save(role)
    }

    /**
     * 更新角色
     */
    override suspend fun updateRole(id: UUID, name: String, description: String?, permissionIds: Set<UUID>): Role {
        // 获取角色
        val role = roleRepository.findById(id) ?: throw NotFoundException("角色不存在: $id")
        
        // 检查角色名是否已存在
        if (name != role.name && roleRepository.existsByName(name)) {
            throw BadRequestException("角色名已存在: $name")
        }
        
        // 获取权限
        val permissions = if (permissionIds.isNotEmpty()) {
            permissionIds.mapNotNull { permissionRepository.findById(it) }.toSet()
        } else {
            emptySet()
        }
        
        // 更新角色
        val updatedRole = role.copy(
            name = name,
            description = description,
            permissions = permissions,
            updatedAt = Instant.now()
        )
        
        return roleRepository.update(updatedRole)
    }

    /**
     * 删除角色
     */
    override suspend fun deleteRole(id: UUID): Boolean {
        val role = roleRepository.findById(id) ?: throw NotFoundException("角色不存在: $id")
        
        // 不允许删除系统角色
        if (role.isSystem) {
            throw BadRequestException("不能删除系统角色")
        }
        
        return roleRepository.delete(id)
    }

    /**
     * 获取角色
     */
    override suspend fun getRole(id: UUID): Role {
        return roleRepository.findById(id) ?: throw NotFoundException("角色不存在: $id")
    }

    /**
     * 根据名称获取角色
     */
    override suspend fun getRoleByName(name: String): Role {
        return roleRepository.findByName(name) ?: throw NotFoundException("角色不存在: $name")
    }

    /**
     * 分页获取所有角色
     */
    override suspend fun getAllRoles(pageRequest: PageRequest): PageResponse<Role> {
        return roleRepository.findAll(pageRequest)
    }

    /**
     * 为角色添加权限
     */
    override suspend fun addPermissionToRole(roleId: UUID, permissionId: UUID): Role {
        val role = roleRepository.findById(roleId) ?: throw NotFoundException("角色不存在: $roleId")
        val permission = permissionRepository.findById(permissionId) ?: throw NotFoundException("权限不存在: $permissionId")
        
        // 检查权限是否已存在
        if (role.permissions.any { it.id == permissionId }) {
            return role
        }
        
        // 更新角色
        val updatedRole = role.copy(
            permissions = role.permissions + permission,
            updatedAt = Instant.now()
        )
        
        return roleRepository.update(updatedRole)
    }

    /**
     * 从角色中移除权限
     */
    override suspend fun removePermissionFromRole(roleId: UUID, permissionId: UUID): Role {
        val role = roleRepository.findById(roleId) ?: throw NotFoundException("角色不存在: $roleId")
        
        // 检查权限是否存在
        if (role.permissions.none { it.id == permissionId }) {
            return role
        }
        
        // 更新角色
        val updatedRole = role.copy(
            permissions = role.permissions.filter { it.id != permissionId }.toSet(),
            updatedAt = Instant.now()
        )
        
        return roleRepository.update(updatedRole)
    }
} 