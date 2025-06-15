package com.kace.auth.domain.service

import com.kace.auth.domain.model.Permission
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.util.*

/**
 * 权限服务接口
 */
interface PermissionService {
    /**
     * 创建权限
     */
    suspend fun createPermission(
        name: String,
        description: String? = null,
        resource: String,
        action: String
    ): Permission
    
    /**
     * 更新权限
     */
    suspend fun updatePermission(
        id: UUID,
        name: String,
        description: String? = null,
        resource: String,
        action: String
    ): Permission
    
    /**
     * 删除权限
     */
    suspend fun deletePermission(id: UUID): Boolean
    
    /**
     * 获取权限
     */
    suspend fun getPermission(id: UUID): Permission
    
    /**
     * 根据名称获取权限
     */
    suspend fun getPermissionByName(name: String): Permission
    
    /**
     * 根据资源和操作获取权限
     */
    suspend fun getPermissionByResourceAndAction(resource: String, action: String): Permission
    
    /**
     * 分页获取所有权限
     */
    suspend fun getAllPermissions(pageRequest: PageRequest): PageResponse<Permission>
    
    /**
     * 根据资源分页获取权限
     */
    suspend fun getPermissionsByResource(resource: String, pageRequest: PageRequest): PageResponse<Permission>
} 