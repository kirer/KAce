package com.kace.auth.infrastructure.persistence.repository

import com.kace.auth.domain.model.Permission
import com.kace.auth.domain.model.Role
import com.kace.auth.domain.repository.RoleRepository
import com.kace.auth.infrastructure.persistence.entity.PermissionEntity
import com.kace.auth.infrastructure.persistence.entity.RoleEntity
import com.kace.auth.infrastructure.persistence.entity.RoleTable
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * 角色仓库实现
 */
class RoleRepositoryImpl : RoleRepository {

    /**
     * 根据ID查找角色
     */
    override suspend fun findById(id: UUID): Role? = transaction {
        RoleEntity.findById(id)?.toRole()
    }

    /**
     * 根据名称查找角色
     */
    override suspend fun findByName(name: String): Role? = transaction {
        RoleEntity.find { RoleTable.name eq name }.firstOrNull()?.toRole()
    }

    /**
     * 分页查询角色
     */
    override suspend fun findAll(pageRequest: PageRequest): PageResponse<Role> = transaction {
        val page = pageRequest.page.coerceAtLeast(1)
        val size = pageRequest.size.coerceAtLeast(1)
        val offset = ((page - 1) * size).toLong()
        
        val sortField = when (pageRequest.sort?.lowercase()) {
            "name" -> RoleTable.name
            "createdat" -> RoleTable.createdAt
            else -> RoleTable.createdAt
        }
        
        val sortOrder = when (pageRequest.direction?.uppercase()) {
            "ASC" -> SortOrder.ASC
            else -> SortOrder.DESC
        }
        
        val query = RoleEntity.all().orderBy(sortField to sortOrder)
        val totalElements = query.count()
        val roles = query.limit(size, offset).map { it.toRole() }
        
        PageResponse.of(roles, page, size, totalElements)
    }

    /**
     * 保存角色
     */
    override suspend fun save(role: Role): Role = transaction {
        val entity = RoleEntity.new {
            name = role.name
            description = role.description
            isSystem = role.isSystem
            createdAt = role.createdAt
            updatedAt = role.updatedAt
        }
        
        // 保存权限关联
        if (role.permissions.isNotEmpty()) {
            // 此处需要实现权限关联逻辑
        }
        
        entity.toRole()
    }

    /**
     * 更新角色
     */
    override suspend fun update(role: Role): Role = transaction {
        val entity = RoleEntity.findById(role.id) ?: throw NoSuchElementException("角色不存在: ${role.id}")
        
        entity.apply {
            name = role.name
            description = role.description
            isSystem = role.isSystem
            updatedAt = role.updatedAt
        }
        
        // 更新权限关联
        // 此处需要实现权限关联逻辑
        
        entity.toRole()
    }

    /**
     * 删除角色
     */
    override suspend fun delete(id: UUID): Boolean = transaction {
        val entity = RoleEntity.findById(id) ?: return@transaction false
        entity.delete()
        true
    }

    /**
     * 检查角色名是否存在
     */
    override suspend fun existsByName(name: String): Boolean = transaction {
        RoleEntity.find { RoleTable.name eq name }.count() > 0
    }

    /**
     * 将实体转换为领域模型
     */
    private fun RoleEntity.toRole(): Role {
        val permissionsList = this.permissions.map {
            Permission(
                id = it.id.value,
                name = it.name,
                description = it.description,
                resource = it.resource,
                action = it.action,
                isSystem = it.isSystem,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }.toSet()
        
        return Role(
            id = this.id.value,
            name = this.name,
            description = this.description,
            permissions = permissionsList,
            isSystem = this.isSystem,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
} 