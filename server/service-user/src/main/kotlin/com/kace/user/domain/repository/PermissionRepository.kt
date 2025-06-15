package com.kace.user.domain.repository

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Permission

/**
 * 权限仓库接口
 */
interface PermissionRepository {
    /**
     * 根据ID获取权限
     * @param id 权限ID
     * @return 权限对象，如果不存在则返回null
     */
    suspend fun getById(id: String): Permission?
    
    /**
     * 根据权限代码获取权限
     * @param code 权限代码
     * @return 权限对象，如果不存在则返回null
     */
    suspend fun getByCode(code: String): Permission?
    
    /**
     * 分页获取权限列表
     * @param page 页码
     * @param size 每页大小
     * @param category 权限分类过滤
     * @param query 搜索关键词
     * @return 权限分页对象
     */
    suspend fun getPermissions(page: Int, size: Int, category: String?, query: String?): PageDto<Permission>
    
    /**
     * 获取所有权限
     * @return 权限列表
     */
    suspend fun getAllPermissions(): List<Permission>
    
    /**
     * 根据分类获取权限
     * @param category 权限分类
     * @return 权限列表
     */
    suspend fun getPermissionsByCategory(category: String): List<Permission>
    
    /**
     * 创建权限
     * @param permission 权限对象
     * @return 创建的权限对象
     */
    suspend fun createPermission(permission: Permission): Permission
    
    /**
     * 更新权限
     * @param permission 权限对象
     * @return 更新后的权限对象
     */
    suspend fun updatePermission(permission: Permission): Permission
    
    /**
     * 删除权限
     * @param id 权限ID
     * @return 是否删除成功
     */
    suspend fun deletePermission(id: String): Boolean
    
    /**
     * 获取用户的所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    suspend fun getUserPermissions(userId: String): List<Permission>
    
    /**
     * 获取角色的所有权限
     * @param roleId 角色ID
     * @return 权限列表
     */
    suspend fun getRolePermissions(roleId: String): List<Permission>
    
    /**
     * 检查用户是否拥有指定权限
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    suspend fun hasPermission(userId: String, permissionCode: String): Boolean
    
    /**
     * 初始化系统权限
     * 创建系统预定义的权限
     * @return 创建的权限列表
     */
    suspend fun initSystemPermissions(): List<Permission>
} 