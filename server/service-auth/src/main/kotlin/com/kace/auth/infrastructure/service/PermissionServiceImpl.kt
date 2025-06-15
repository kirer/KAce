package com.kace.auth.infrastructure.service

import com.kace.auth.domain.model.Permission
import com.kace.auth.domain.repository.PermissionRepository
import com.kace.auth.domain.service.PermissionService
import com.kace.common.exception.BadRequestException
import com.kace.common.exception.NotFoundException
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.time.Instant
import java.util.*

/**
 * 权限服务实现
 */
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository
) : PermissionService {

    /**
     * 创建权限
     */
    override suspend fun createPermission(
        name: String,
        description: String?,
        resource: String,
        action: String
    ): Permission {
        // 检查权限名是否已存在
        if (permissionRepository.existsByName(name)) {
            throw BadRequestException("权限名已存在: $name")
        }
        
        // 检查资源和操作组合是否已存在
        if (permissionRepository.existsByResourceAndAction(resource, action)) {
            throw BadRequestException("资源和操作组合已存在: $resource:$action")
        }
        
        // 创建权限
        val permission = Permission(
            name = name,
            description = description,
            resource = resource,
            action = action,
            isSystem = false,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        
        return permissionRepository.save(permission)
    }

    /**
     * 更新权限
     */
    override suspend fun updatePermission(
        id: UUID,
        name: String,
        description: String?,
        resource: String,
        action: String
    ): Permission {
        // 获取权限
        val permission = permissionRepository.findById(id) ?: throw NotFoundException("权限不存在: $id")
        
        // 检查权限名是否已存在
        if (name != permission.name && permissionRepository.existsByName(name)) {
            throw BadRequestException("权限名已存在: $name")
        }
        
        // 检查资源和操作组合是否已存在
        if ((resource != permission.resource || action != permission.action) && 
            permissionRepository.existsByResourceAndAction(resource, action)) {
            throw BadRequestException("资源和操作组合已存在: $resource:$action")
        }
        
        // 不允许修改系统权限的资源和操作
        if (permission.isSystem && (resource != permission.resource || action != permission.action)) {
            throw BadRequestException("不能修改系统权限的资源和操作")
        }
        
        // 更新权限
        val updatedPermission = permission.copy(
            name = name,
            description = description,
            resource = resource,
            action = action,
            updatedAt = Instant.now()
        )
        
        return permissionRepository.update(updatedPermission)
    }

    /**
     * 删除权限
     */
    override suspend fun deletePermission(id: UUID): Boolean {
        val permission = permissionRepository.findById(id) ?: throw NotFoundException("权限不存在: $id")
        
        // 不允许删除系统权限
        if (permission.isSystem) {
            throw BadRequestException("不能删除系统权限")
        }
        
        return permissionRepository.delete(id)
    }

    /**
     * 获取权限
     */
    override suspend fun getPermission(id: UUID): Permission {
        return permissionRepository.findById(id) ?: throw NotFoundException("权限不存在: $id")
    }

    /**
     * 根据名称获取权限
     */
    override suspend fun getPermissionByName(name: String): Permission {
        return permissionRepository.findByName(name) ?: throw NotFoundException("权限不存在: $name")
    }

    /**
     * 根据资源和操作获取权限
     */
    override suspend fun getPermissionByResourceAndAction(resource: String, action: String): Permission {
        return permissionRepository.findByResourceAndAction(resource, action) 
            ?: throw NotFoundException("权限不存在: $resource:$action")
    }

    /**
     * 分页获取所有权限
     */
    override suspend fun getAllPermissions(pageRequest: PageRequest): PageResponse<Permission> {
        return permissionRepository.findAll(pageRequest)
    }

    /**
     * 根据资源分页获取权限
     */
    override suspend fun getPermissionsByResource(resource: String, pageRequest: PageRequest): PageResponse<Permission> {
        return permissionRepository.findByResource(resource, pageRequest)
    }
} 