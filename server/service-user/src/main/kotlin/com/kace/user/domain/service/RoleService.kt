package com.kace.user.domain.service

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Role
import com.kace.user.domain.model.RoleCreateRequest
import com.kace.user.domain.model.RoleUpdateRequest
import com.kace.user.domain.model.UserRole

/**
 * 角色服务接口
 */
interface RoleService {
    /**
     * 获取角色
     */
    suspend fun getRole(id: String): Role?
    
    /**
     * 根据名称获取角色
     */
    suspend fun getRoleByName(name: String, organizationId: String?): Role?
    
    /**
     * 获取角色列表
     */
    suspend fun getRoles(page: Int, size: Int, organizationId: String?, query: String?): PageDto<Role>
    
    /**
     * 创建角色
     */
    suspend fun createRole(request: RoleCreateRequest): Role
    
    /**
     * 更新角色
     */
    suspend fun updateRole(id: String, request: RoleUpdateRequest): Role
    
    /**
     * 删除角色
     */
    suspend fun deleteRole(id: String): Boolean
    
    /**
     * 获取用户的所有角色
     */
    suspend fun getUserRoles(userId: String): List<Role>
    
    /**
     * 获取角色的所有用户
     */
    suspend fun getRoleUsers(roleId: String, page: Int, size: Int): PageDto<String>
    
    /**
     * 为用户分配角色
     */
    suspend fun assignRoleToUser(roleId: String, userId: String, assignedBy: String): Boolean
    
    /**
     * 移除用户的角色
     */
    suspend fun removeRoleFromUser(roleId: String, userId: String): Boolean
    
    /**
     * 检查用户是否拥有指定角色
     */
    suspend fun hasRole(userId: String, roleId: String): Boolean
    
    /**
     * 检查用户是否拥有指定角色名称
     */
    suspend fun hasRoleByName(userId: String, roleName: String): Boolean
    
    /**
     * 初始化系统角色
     */
    suspend fun initSystemRoles(): List<Role>
} 