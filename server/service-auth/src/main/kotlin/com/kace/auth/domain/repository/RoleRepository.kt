package com.kace.auth.domain.repository

import com.kace.auth.domain.model.Role
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import java.util.*

/**
 * 角色仓库接口
 */
interface RoleRepository {
    /**
     * 根据ID查找角色
     */
    suspend fun findById(id: UUID): Role?
    
    /**
     * 根据名称查找角色
     */
    suspend fun findByName(name: String): Role?
    
    /**
     * 分页查询角色
     */
    suspend fun findAll(pageRequest: PageRequest): PageResponse<Role>
    
    /**
     * 保存角色
     */
    suspend fun save(role: Role): Role
    
    /**
     * 更新角色
     */
    suspend fun update(role: Role): Role
    
    /**
     * 删除角色
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 检查角色名是否存在
     */
    suspend fun existsByName(name: String): Boolean
} 