package com.kace.auth.domain.repository

import com.kace.auth.domain.model.Permission
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.util.*

/**
 * 权限仓库接口
 */
interface PermissionRepository {
    /**
     * 根据ID查找权限
     */
    suspend fun findById(id: UUID): Permission?
    
    /**
     * 根据名称查找权限
     */
    suspend fun findByName(name: String): Permission?
    
    /**
     * 根据资源和操作查找权限
     */
    suspend fun findByResourceAndAction(resource: String, action: String): Permission?
    
    /**
     * 分页查询所有权限
     */
    suspend fun findAll(pageRequest: PageRequest): PageResponse<Permission>
    
    /**
     * 根据资源查询权限
     */
    suspend fun findByResource(resource: String, pageRequest: PageRequest): PageResponse<Permission>
    
    /**
     * 保存权限
     */
    suspend fun save(permission: Permission): Permission
    
    /**
     * 更新权限
     */
    suspend fun update(permission: Permission): Permission
    
    /**
     * 删除权限
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 检查权限名是否存在
     */
    suspend fun existsByName(name: String): Boolean
    
    /**
     * 检查资源和操作组合是否存在
     */
    suspend fun existsByResourceAndAction(resource: String, action: String): Boolean
} 