package com.kace.user.domain.service.impl

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Permission
import com.kace.user.domain.model.PermissionCreateRequest
import com.kace.user.domain.model.PermissionUpdateRequest
import com.kace.user.domain.repository.PermissionRepository
import com.kace.user.domain.service.PermissionService
import java.time.Instant
import java.util.UUID

/**
 * 权限服务实现
 */
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository
) : PermissionService {
    /**
     * 获取权限
     */
    override suspend fun getPermission(id: String): Permission? {
        return permissionRepository.getById(id)
    }
    
    /**
     * 根据权限代码获取权限
     */
    override suspend fun getPermissionByCode(code: String): Permission? {
        return permissionRepository.getByCode(code)
    }
    
    /**
     * 获取权限列表
     */
    override suspend fun getPermissions(page: Int, size: Int, category: String?, query: String?): PageDto<Permission> {
        return permissionRepository.getPermissions(page, size, category, query)
    }
    
    /**
     * 获取所有权限
     */
    override suspend fun getAllPermissions(): List<Permission> {
        return permissionRepository.getAllPermissions()
    }
    
    /**
     * 根据分类获取权限
     */
    override suspend fun getPermissionsByCategory(category: String): List<Permission> {
        return permissionRepository.getPermissionsByCategory(category)
    }
    
    /**
     * 创建权限
     */
    override suspend fun createPermission(request: PermissionCreateRequest): Permission {
        val now = Instant.now()
        val permission = Permission(
            id = UUID.randomUUID().toString(),
            name = request.name,
            code = request.code,
            description = request.description,
            category = request.category,
            isSystem = false,
            createdAt = now,
            updatedAt = now
        )
        
        return permissionRepository.createPermission(permission)
    }
    
    /**
     * 更新权限
     */
    override suspend fun updatePermission(id: String, request: PermissionUpdateRequest): Permission {
        val existingPermission = permissionRepository.getById(id)
            ?: throw NoSuchElementException("权限不存在: $id")
        
        if (existingPermission.isSystem) {
            throw IllegalStateException("系统权限不能修改")
        }
        
        val updatedPermission = existingPermission.copy(
            name = request.name ?: existingPermission.name,
            description = request.description ?: existingPermission.description,
            category = request.category ?: existingPermission.category,
            updatedAt = Instant.now()
        )
        
        return permissionRepository.updatePermission(updatedPermission)
    }
    
    /**
     * 删除权限
     */
    override suspend fun deletePermission(id: String): Boolean {
        val permission = permissionRepository.getById(id)
            ?: return false
        
        if (permission.isSystem) {
            throw IllegalStateException("系统权限不能删除")
        }
        
        return permissionRepository.deletePermission(id)
    }
    
    /**
     * 获取用户的所有权限
     */
    override suspend fun getUserPermissions(userId: String): List<Permission> {
        return permissionRepository.getUserPermissions(userId)
    }
    
    /**
     * 获取角色的所有权限
     */
    override suspend fun getRolePermissions(roleId: String): List<Permission> {
        return permissionRepository.getRolePermissions(roleId)
    }
    
    /**
     * 检查用户是否拥有指定权限
     */
    override suspend fun hasPermission(userId: String, permissionCode: String): Boolean {
        return permissionRepository.hasPermission(userId, permissionCode)
    }
    
    /**
     * 初始化系统权限
     */
    override suspend fun initSystemPermissions(): List<Permission> {
        return permissionRepository.initSystemPermissions()
    }
} 