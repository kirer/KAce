package com.kace.auth.domain.service

import com.kace.auth.domain.model.Role
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.util.*

/**
 * 角色服务接口
 */
interface RoleService {
    /**
     * 创建角色
     */
    suspend fun createRole(name: String, description: String? = null, permissionIds: Set<UUID> = emptySet()): Role
    
    /**
     * 更新角色
     */
    suspend fun updateRole(id: UUID, name: String, description: String? = null, permissionIds: Set<UUID> = emptySet()): Role
    
    /**
     * 删除角色
     */
    suspend fun deleteRole(id: UUID): Boolean
    
    /**
     * 获取角色
     */
    suspend fun getRole(id: UUID): Role
    
    /**
     * 根据名称获取角色
     */
    suspend fun getRoleByName(name: String): Role
    
    /**
     * 分页获取所有角色
     */
    suspend fun getAllRoles(pageRequest: PageRequest): PageResponse<Role>
    
    /**
     * 为角色添加权限
     */
    suspend fun addPermissionToRole(roleId: UUID, permissionId: UUID): Role
    
    /**
     * 从角色中移除权限
     */
    suspend fun removePermissionFromRole(roleId: UUID, permissionId: UUID): Role
} 