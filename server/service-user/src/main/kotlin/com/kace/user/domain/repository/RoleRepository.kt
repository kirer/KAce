package com.kace.user.domain.repository

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Role
import com.kace.user.domain.model.UserRole

/**
 * 角色仓库接口
 */
interface RoleRepository {
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色对象，如果不存在则返回null
     */
    suspend fun getById(id: String): Role?
    
    /**
     * 根据名称获取角色
     * @param name 角色名称
     * @param organizationId 组织ID，null表示全局角色
     * @return 角色对象，如果不存在则返回null
     */
    suspend fun getByName(name: String, organizationId: String?): Role?
    
    /**
     * 分页获取角色列表
     * @param page 页码
     * @param size 每页大小
     * @param organizationId 组织ID过滤，null表示获取全局角色
     * @param query 搜索关键词
     * @return 角色分页对象
     */
    suspend fun getRoles(page: Int, size: Int, organizationId: String?, query: String?): PageDto<Role>
    
    /**
     * 创建角色
     * @param role 角色对象
     * @return 创建的角色对象
     */
    suspend fun createRole(role: Role): Role
    
    /**
     * 更新角色
     * @param role 角色对象
     * @return 更新后的角色对象
     */
    suspend fun updateRole(role: Role): Role
    
    /**
     * 删除角色
     * @param id 角色ID
     * @return 是否删除成功
     */
    suspend fun deleteRole(id: String): Boolean
    
    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    suspend fun getUserRoles(userId: String): List<Role>
    
    /**
     * 获取角色的所有用户
     * @param roleId 角色ID
     * @param page 页码
     * @param size 每页大小
     * @return 用户ID分页对象
     */
    suspend fun getRoleUsers(roleId: String, page: Int, size: Int): PageDto<String>
    
    /**
     * 为用户分配角色
     * @param userRole 用户角色关联对象
     * @return 是否分配成功
     */
    suspend fun assignRoleToUser(userRole: UserRole): Boolean
    
    /**
     * 移除用户的角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否移除成功
     */
    suspend fun removeRoleFromUser(userId: String, roleId: String): Boolean
    
    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    suspend fun hasRole(userId: String, roleId: String): Boolean
    
    /**
     * 检查用户是否拥有指定角色名称
     * @param userId 用户ID
     * @param roleName 角色名称
     * @return 是否拥有角色
     */
    suspend fun hasRoleByName(userId: String, roleName: String): Boolean
} 