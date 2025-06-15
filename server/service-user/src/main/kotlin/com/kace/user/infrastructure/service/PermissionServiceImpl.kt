package com.kace.user.infrastructure.service

import com.kace.common.exception.EntityNotFoundException
import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Permission
import com.kace.user.domain.model.PermissionCreateRequest
import com.kace.user.domain.model.PermissionUpdateRequest
import com.kace.user.domain.model.SystemPermissions
import com.kace.user.domain.repository.PermissionRepository
import com.kace.user.domain.service.PermissionService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 权限服务实现
 */
class PermissionServiceImpl(private val permissionRepository: PermissionRepository) : PermissionService {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 根据ID获取权限
     */
    override suspend fun getPermissionById(id: String): Permission? {
        return permissionRepository.getById(id)
    }
    
    /**
     * 根据权限代码获取权限
     */
    override suspend fun getPermissionByCode(code: String): Permission? {
        return permissionRepository.getByCode(code)
    }
    
    /**
     * 分页获取权限列表
     */
    override suspend fun getPermissions(
        page: Int,
        size: Int,
        category: String?,
        query: String?
    ): PageDto<Permission> {
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
    override suspend fun createPermission(request: PermissionCreateRequest, createdBy: String): Permission {
        // 检查权限代码是否已存在
        permissionRepository.getByCode(request.code)?.let {
            throw IllegalArgumentException("权限代码 '${request.code}' 已存在")
        }
        
        val now = Clock.System.now()
        val permission = Permission(
            id = UUID.randomUUID().toString(),
            name = request.name,
            code = request.code,
            description = request.description,
            category = request.category,
            isSystem = false, // 用户创建的权限不是系统权限
            createdAt = now,
            updatedAt = now
        )
        
        return permissionRepository.createPermission(permission)
    }
    
    /**
     * 更新权限
     */
    override suspend fun updatePermission(id: String, request: PermissionUpdateRequest, updatedBy: String): Permission {
        val existingPermission = permissionRepository.getById(id)
            ?: throw EntityNotFoundException("权限不存在: $id")
        
        // 系统权限不允许修改
        if (existingPermission.isSystem) {
            throw IllegalArgumentException("系统权限不允许修改")
        }
        
        val updatedPermission = existingPermission.copy(
            name = request.name ?: existingPermission.name,
            description = request.description ?: existingPermission.description,
            category = request.category ?: existingPermission.category,
            updatedAt = Clock.System.now()
        )
        
        return permissionRepository.updatePermission(updatedPermission)
    }
    
    /**
     * 删除权限
     */
    override suspend fun deletePermission(id: String): Boolean {
        val permission = permissionRepository.getById(id)
            ?: throw EntityNotFoundException("权限不存在: $id")
        
        // 系统权限不允许删除
        if (permission.isSystem) {
            throw IllegalArgumentException("系统权限不允许删除")
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
    override suspend fun initSystemPermissions(createdBy: String): List<Permission> {
        val now = Clock.System.now()
        val systemPermissions = listOf(
            // 用户管理权限
            createSystemPermission(SystemPermissions.USER_CREATE, "创建用户", "创建新用户", "USER_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.USER_READ, "查看用户", "查看用户信息", "USER_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.USER_UPDATE, "更新用户", "更新用户信息", "USER_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.USER_DELETE, "删除用户", "删除用户", "USER_MANAGEMENT", now),
            
            // 角色管理权限
            createSystemPermission(SystemPermissions.ROLE_CREATE, "创建角色", "创建新角色", "ROLE_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.ROLE_READ, "查看角色", "查看角色信息", "ROLE_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.ROLE_UPDATE, "更新角色", "更新角色信息", "ROLE_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.ROLE_DELETE, "删除角色", "删除角色", "ROLE_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.ROLE_ASSIGN, "分配角色", "为用户分配角色", "ROLE_MANAGEMENT", now),
            
            // 权限管理权限
            createSystemPermission(SystemPermissions.PERMISSION_CREATE, "创建权限", "创建新权限", "ROLE_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.PERMISSION_READ, "查看权限", "查看权限信息", "ROLE_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.PERMISSION_UPDATE, "更新权限", "更新权限信息", "ROLE_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.PERMISSION_DELETE, "删除权限", "删除权限", "ROLE_MANAGEMENT", now),
            
            // 组织管理权限
            createSystemPermission(SystemPermissions.ORGANIZATION_CREATE, "创建组织", "创建新组织", "ORGANIZATION_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.ORGANIZATION_READ, "查看组织", "查看组织信息", "ORGANIZATION_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.ORGANIZATION_UPDATE, "更新组织", "更新组织信息", "ORGANIZATION_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.ORGANIZATION_DELETE, "删除组织", "删除组织", "ORGANIZATION_MANAGEMENT", now),
            
            // 系统管理权限
            createSystemPermission(SystemPermissions.SYSTEM_CONFIG, "系统配置", "管理系统配置", "SYSTEM_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.SYSTEM_BACKUP, "系统备份", "执行系统备份", "SYSTEM_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.SYSTEM_RESTORE, "系统恢复", "执行系统恢复", "SYSTEM_MANAGEMENT", now),
            createSystemPermission(SystemPermissions.SYSTEM_LOG, "系统日志", "查看系统日志", "SYSTEM_MANAGEMENT", now)
        )
        
        val createdPermissions = mutableListOf<Permission>()
        for (permission in systemPermissions) {
            // 检查权限是否已存在
            val existingPermission = permissionRepository.getByCode(permission.code)
            if (existingPermission == null) {
                createdPermissions.add(permissionRepository.createPermission(permission))
            } else {
                createdPermissions.add(existingPermission)
            }
        }
        
        return createdPermissions
    }
    
    /**
     * 创建系统权限对象
     */
    private fun createSystemPermission(
        code: String,
        name: String,
        description: String,
        category: String,
        timestamp: Instant
    ): Permission {
        return Permission(
            id = UUID.randomUUID().toString(),
            name = name,
            code = code,
            description = description,
            category = category,
            isSystem = true,
            createdAt = timestamp,
            updatedAt = timestamp
        )
    }
} 