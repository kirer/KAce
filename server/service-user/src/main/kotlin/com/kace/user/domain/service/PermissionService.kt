package com.kace.user.domain.service

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Permission
import com.kace.user.domain.model.PermissionCreateRequest
import com.kace.user.domain.model.PermissionUpdateRequest

/**
 * 权限服务接口
 */
interface PermissionService {
    /**
     * 获取权限
     */
    suspend fun getPermission(id: String): Permission?
    
    /**
     * 根据权限代码获取权限
     */
    suspend fun getPermissionByCode(code: String): Permission?
    
    /**
     * 获取权限列表
     */
    suspend fun getPermissions(page: Int, size: Int, category: String?, query: String?): PageDto<Permission>
    
    /**
     * 获取所有权限
     */
    suspend fun getAllPermissions(): List<Permission>
    
    /**
     * 根据分类获取权限
     */
    suspend fun getPermissionsByCategory(category: String): List<Permission>
    
    /**
     * 创建权限
     */
    suspend fun createPermission(request: PermissionCreateRequest): Permission
    
    /**
     * 更新权限
     */
    suspend fun updatePermission(id: String, request: PermissionUpdateRequest): Permission
    
    /**
     * 删除权限
     */
    suspend fun deletePermission(id: String): Boolean
    
    /**
     * 获取用户的所有权限
     */
    suspend fun getUserPermissions(userId: String): List<Permission>
    
    /**
     * 获取角色的所有权限
     */
    suspend fun getRolePermissions(roleId: String): List<Permission>
    
    /**
     * 检查用户是否拥有指定权限
     */
    suspend fun hasPermission(userId: String, permissionCode: String): Boolean
    
    /**
     * 初始化系统权限
     */
    suspend fun initSystemPermissions(): List<Permission>
} 