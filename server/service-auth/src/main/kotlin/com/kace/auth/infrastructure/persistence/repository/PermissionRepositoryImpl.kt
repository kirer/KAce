package com.kace.auth.infrastructure.persistence.repository

import com.kace.auth.domain.model.Permission
import com.kace.auth.domain.repository.PermissionRepository
import com.kace.auth.infrastructure.persistence.entity.PermissionEntity
import com.kace.auth.infrastructure.persistence.entity.PermissionTable
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * 权限仓库实现
 */
class PermissionRepositoryImpl : PermissionRepository {

    /**
     * 根据ID查找权限
     */
    override suspend fun findById(id: UUID): Permission? = transaction {
        PermissionEntity.findById(id)?.toPermission()
    }

    /**
     * 根据名称查找权限
     */
    override suspend fun findByName(name: String): Permission? = transaction {
        PermissionEntity.find { PermissionTable.name eq name }.firstOrNull()?.toPermission()
    }

    /**
     * 根据资源和操作查找权限
     */
    override suspend fun findByResourceAndAction(resource: String, action: String): Permission? = transaction {
        PermissionEntity.find { 
            (PermissionTable.resource eq resource) and (PermissionTable.action eq action) 
        }.firstOrNull()?.toPermission()
    }

    /**
     * 分页查询权限
     */
    override suspend fun findAll(pageRequest: PageRequest): PageResponse<Permission> = transaction {
        val page = pageRequest.page.coerceAtLeast(1)
        val size = pageRequest.size.coerceAtLeast(1)
        val offset = ((page - 1) * size).toLong()
        
        val sortField = when (pageRequest.sort?.lowercase()) {
            "name" -> PermissionTable.name
            "resource" -> PermissionTable.resource
            "action" -> PermissionTable.action
            "createdat" -> PermissionTable.createdAt
            else -> PermissionTable.createdAt
        }
        
        val sortOrder = when (pageRequest.direction?.uppercase()) {
            "ASC" -> SortOrder.ASC
            else -> SortOrder.DESC
        }
        
        val query = PermissionEntity.all().orderBy(sortField to sortOrder)
        val totalElements = query.count()
        val permissions = query.limit(size, offset).map { it.toPermission() }
        
        PageResponse.of(permissions, page, size, totalElements)
    }

    /**
     * 根据资源查询权限
     */
    override suspend fun findByResource(resource: String, pageRequest: PageRequest): PageResponse<Permission> = transaction {
        val page = pageRequest.page.coerceAtLeast(1)
        val size = pageRequest.size.coerceAtLeast(1)
        val offset = ((page - 1) * size).toLong()
        
        val sortField = when (pageRequest.sort?.lowercase()) {
            "name" -> PermissionTable.name
            "action" -> PermissionTable.action
            "createdat" -> PermissionTable.createdAt
            else -> PermissionTable.createdAt
        }
        
        val sortOrder = when (pageRequest.direction?.uppercase()) {
            "ASC" -> SortOrder.ASC
            else -> SortOrder.DESC
        }
        
        val query = PermissionEntity.find { PermissionTable.resource eq resource }.orderBy(sortField to sortOrder)
        val totalElements = query.count()
        val permissions = query.limit(size, offset).map { it.toPermission() }
        
        PageResponse.of(permissions, page, size, totalElements)
    }

    /**
     * 保存权限
     */
    override suspend fun save(permission: Permission): Permission = transaction {
        val entity = PermissionEntity.new {
            name = permission.name
            description = permission.description
            resource = permission.resource
            action = permission.action
            isSystem = permission.isSystem
            createdAt = permission.createdAt
            updatedAt = permission.updatedAt
        }
        
        entity.toPermission()
    }

    /**
     * 更新权限
     */
    override suspend fun update(permission: Permission): Permission = transaction {
        val entity = PermissionEntity.findById(permission.id) ?: throw NoSuchElementException("权限不存在: ${permission.id}")
        
        entity.apply {
            name = permission.name
            description = permission.description
            resource = permission.resource
            action = permission.action
            isSystem = permission.isSystem
            updatedAt = permission.updatedAt
        }
        
        entity.toPermission()
    }

    /**
     * 删除权限
     */
    override suspend fun delete(id: UUID): Boolean = transaction {
        val entity = PermissionEntity.findById(id) ?: return@transaction false
        entity.delete()
        true
    }

    /**
     * 检查权限名是否存在
     */
    override suspend fun existsByName(name: String): Boolean = transaction {
        PermissionEntity.find { PermissionTable.name eq name }.count() > 0
    }

    /**
     * 检查资源和操作组合是否存在
     */
    override suspend fun existsByResourceAndAction(resource: String, action: String): Boolean = transaction {
        PermissionEntity.find { 
            (PermissionTable.resource eq resource) and (PermissionTable.action eq action) 
        }.count() > 0
    }

    /**
     * 将实体转换为领域模型
     */
    private fun PermissionEntity.toPermission(): Permission {
        return Permission(
            id = this.id.value,
            name = this.name,
            description = this.description,
            resource = this.resource,
            action = this.action,
            isSystem = this.isSystem,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
} 