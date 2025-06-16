package com.kace.user.infrastructure.persistence.repository

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Permission
import com.kace.user.domain.model.SystemPermissions
import com.kace.user.domain.repository.PermissionRepository
import com.kace.user.infrastructure.persistence.entity.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 权限仓库实现
 */
class PermissionRepositoryImpl : PermissionRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 根据ID获取权限
     */
    override suspend fun getById(id: String): Permission? {
        return transaction {
            try {
                val permissionEntity = PermissionEntity.findById(UUID.fromString(id))
                permissionEntity?.toModel()
            } catch (e: Exception) {
                logger.error("获取权限失败: ${e.message}", e)
                null
            }
        }
    }
    
    /**
     * 根据权限代码获取权限
     */
    override suspend fun getByCode(code: String): Permission? {
        return transaction {
            try {
                val query = PermissionTable.select {
                    PermissionTable.code eq code
                }
                
                val permissionRow = query.singleOrNull() ?: return@transaction null
                PermissionEntity.wrapRow(permissionRow).toModel()
            } catch (e: Exception) {
                logger.error("根据代码获取权限失败: ${e.message}", e)
                null
            }
        }
    }
    
    /**
     * 分页获取权限列表
     */
    override suspend fun getPermissions(page: Int, size: Int, category: String?, query: String?): PageDto<Permission> {
        return transaction {
            try {
                val offset = (page - 1) * size
                
                // 构建查询条件
                var whereClause: Op<Boolean> = Op.TRUE
                
                if (category != null) {
                    whereClause = whereClause and (PermissionTable.category eq category)
                }
                
                if (query != null && query.isNotBlank()) {
                    whereClause = whereClause and (
                        PermissionTable.name.lowerCase() like "%${query.lowercase()}%" or
                        PermissionTable.code.lowerCase() like "%${query.lowercase()}%" or
                        (PermissionTable.description.isNotNull() and (PermissionTable.description.lowerCase() like "%${query.lowercase()}%"))
                    )
                }
                
                // 查询总数
                val totalCount = PermissionTable.select { whereClause }.count()
                
                // 分页查询
                val permissions = PermissionTable.select { whereClause }
                    .orderBy(PermissionTable.category to SortOrder.ASC, PermissionTable.name to SortOrder.ASC)
                    .limit(size, offset.toLong())
                    .map { PermissionEntity.wrapRow(it).toModel() }
                
                PageDto(
                    content = permissions,
                    page = page,
                    size = size,
                    totalElements = totalCount,
                    totalPages = ((totalCount + size - 1) / size).toInt()
                )
            } catch (e: Exception) {
                logger.error("分页获取权限列表失败: ${e.message}", e)
                PageDto(emptyList(), page, size, 0, 0)
            }
        }
    }
    
    /**
     * 获取所有权限
     */
    override suspend fun getAllPermissions(): List<Permission> {
        return transaction {
            try {
                PermissionTable.selectAll()
                    .orderBy(PermissionTable.category to SortOrder.ASC, PermissionTable.name to SortOrder.ASC)
                    .map { PermissionEntity.wrapRow(it).toModel() }
            } catch (e: Exception) {
                logger.error("获取所有权限失败: ${e.message}", e)
                emptyList()
            }
        }
    }
    
    /**
     * 根据分类获取权限
     */
    override suspend fun getPermissionsByCategory(category: String): List<Permission> {
        return transaction {
            try {
                PermissionTable.select { PermissionTable.category eq category }
                    .orderBy(PermissionTable.name to SortOrder.ASC)
                    .map { PermissionEntity.wrapRow(it).toModel() }
            } catch (e: Exception) {
                logger.error("根据分类获取权限失败: ${e.message}", e)
                emptyList()
            }
        }
    }
    
    /**
     * 创建权限
     */
    override suspend fun createPermission(permission: Permission): Permission {
        return transaction {
            try {
                val permissionEntity = PermissionEntity.new(UUID.fromString(permission.id)) {
                    name = permission.name
                    code = permission.code
                    description = permission.description
                    category = permission.category
                    isSystem = permission.isSystem
                    createdAt = permission.createdAt
                    updatedAt = permission.updatedAt
                }
                permissionEntity.toModel()
            } catch (e: Exception) {
                logger.error("创建权限失败: ${e.message}", e)
                throw e
            }
        }
    }
    
    /**
     * 更新权限
     */
    override suspend fun updatePermission(permission: Permission): Permission {
        return transaction {
            try {
                val permissionEntity = PermissionEntity.findById(UUID.fromString(permission.id))
                    ?: throw NoSuchElementException("权限不存在: ${permission.id}")
                
                permissionEntity.name = permission.name
                permissionEntity.description = permission.description
                permissionEntity.category = permission.category
                permissionEntity.updatedAt = Clock.System.now()
                
                permissionEntity.toModel()
            } catch (e: Exception) {
                logger.error("更新权限失败: ${e.message}", e)
                throw e
            }
        }
    }
    
    /**
     * 删除权限
     */
    override suspend fun deletePermission(id: String): Boolean {
        return transaction {
            try {
                val permissionEntity = PermissionEntity.findById(UUID.fromString(id))
                    ?: return@transaction false
                
                // 删除权限前先删除相关的角色权限关联
                RolePermissionTable.deleteWhere { RolePermissionTable.permissionId eq UUID.fromString(id) }
                
                // 删除权限
                permissionEntity.delete()
                true
            } catch (e: Exception) {
                logger.error("删除权限失败: ${e.message}", e)
                false
            }
        }
    }
    
    /**
     * 获取用户的所有权限
     */
    override suspend fun getUserPermissions(userId: String): List<Permission> {
        return transaction {
            try {
                // 获取用户的所有角色ID
                val roleIds = UserRoleTable.select {
                    UserRoleTable.userId eq UUID.fromString(userId)
                }.map { it[UserRoleTable.roleId] }
                
                if (roleIds.isEmpty()) {
                    return@transaction emptyList()
                }
                
                // 获取这些角色的所有权限
                val permissions = mutableSetOf<Permission>()
                
                // 从角色权限关联表获取权限
                val permissionIds = RolePermissionTable.select {
                    RolePermissionTable.roleId inList roleIds
                }.map { it[RolePermissionTable.permissionId] }
                
                if (permissionIds.isNotEmpty()) {
                    PermissionTable.select {
                        PermissionTable.id inList permissionIds
                    }.map {
                        permissions.add(PermissionEntity.wrapRow(it).toModel())
                    }
                }
                
                // 从角色表的permissions字段获取权限
                roleIds.forEach { roleId ->
                    val role = RoleEntity.findById(roleId)
                    if (role != null && role.permissions.isNotEmpty()) {
                        val permissionCodes = role.permissions.split(",")
                        if (permissionCodes.isNotEmpty()) {
                            PermissionTable.select {
                                PermissionTable.code inList permissionCodes
                            }.map {
                                permissions.add(PermissionEntity.wrapRow(it).toModel())
                            }
                        }
                    }
                }
                
                permissions.toList()
            } catch (e: Exception) {
                logger.error("获取用户权限失败: ${e.message}", e)
                emptyList()
            }
        }
    }
    
    /**
     * 获取角色的所有权限
     */
    override suspend fun getRolePermissions(roleId: String): List<Permission> {
        return transaction {
            try {
                // 从角色权限关联表获取权限
                val permissionIds = RolePermissionTable.select {
                    RolePermissionTable.roleId eq UUID.fromString(roleId)
                }.map { it[RolePermissionTable.permissionId] }
                
                val permissions = mutableListOf<Permission>()
                
                if (permissionIds.isNotEmpty()) {
                    PermissionTable.select {
                        PermissionTable.id inList permissionIds
                    }.map {
                        permissions.add(PermissionEntity.wrapRow(it).toModel())
                    }
                }
                
                // 从角色表的permissions字段获取权限
                val role = RoleEntity.findById(UUID.fromString(roleId))
                if (role != null && role.permissions.isNotEmpty()) {
                    val permissionCodes = role.permissions.split(",")
                    if (permissionCodes.isNotEmpty()) {
                        PermissionTable.select {
                            PermissionTable.code inList permissionCodes
                        }.map {
                            val permission = PermissionEntity.wrapRow(it).toModel()
                            if (!permissions.any { p -> p.id == permission.id }) {
                                permissions.add(permission)
                            }
                        }
                    }
                }
                
                permissions
            } catch (e: Exception) {
                logger.error("获取角色权限失败: ${e.message}", e)
                emptyList()
            }
        }
    }
    
    /**
     * 检查用户是否拥有指定权限
     */
    override suspend fun hasPermission(userId: String, permissionCode: String): Boolean {
        return transaction {
            try {
                // 获取用户的所有角色ID
                val roleIds = UserRoleTable.select {
                    UserRoleTable.userId eq UUID.fromString(userId)
                }.map { it[UserRoleTable.roleId] }
                
                if (roleIds.isEmpty()) {
                    return@transaction false
                }
                
                // 获取权限ID
                val permissionId = PermissionTable.select {
                    PermissionTable.code eq permissionCode
                }.singleOrNull()?.get(PermissionTable.id) ?: return@transaction false
                
                // 检查角色权限关联表
                val hasPermissionInRolePermission = RolePermissionTable.select {
                    (RolePermissionTable.roleId inList roleIds) and
                    (RolePermissionTable.permissionId eq permissionId)
                }.count() > 0
                
                if (hasPermissionInRolePermission) {
                    return@transaction true
                }
                
                // 检查角色表的permissions字段
                roleIds.forEach { roleId ->
                    val role = RoleEntity.findById(roleId)
                    if (role != null && role.permissions.isNotEmpty()) {
                        val permissionCodes = role.permissions.split(",")
                        if (permissionCodes.contains(permissionCode)) {
                            return@transaction true
                        }
                    }
                }
                
                false
            } catch (e: Exception) {
                logger.error("检查用户权限失败: ${e.message}", e)
                false
            }
        }
    }
    
    /**
     * 初始化系统权限
     */
    override suspend fun initSystemPermissions(): List<Permission> {
        return transaction {
            try {
                val now = Clock.System.now()
                val systemPermissions = listOf(
                    // 用户管理权限
                    createSystemPermission(SystemPermissions.USER_CREATE, "创建用户", "创建新用户", "USER_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.USER_READ, "查看用户", "查看用户信息", "USER_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.USER_UPDATE, "更新用户", "更新用户信息", "USER_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.USER_DELETE, "删除用户", "删除用户", "USER_MANAGEMENT", now),
                    
                    // 角色管理权限
                    createSystemPermission(SystemPermissions.ROLE_CREATE, "创建角色", "创建新角色", "ROLE_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.ROLE_READ, "查看角色", "查看角色信息", "ROLE_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.ROLE_UPDATE, "更新角色", "更新角色信息", "ROLE_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.ROLE_DELETE, "删除角色", "删除角色", "ROLE_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.ROLE_ASSIGN, "分配角色", "为用户分配角色", "ROLE_MANAGEMENT", now),
                    
                    // 权限管理权限
                    createSystemPermission(SystemPermissions.PERMISSION_CREATE, "创建权限", "创建新权限", "ROLE_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.PERMISSION_READ, "查看权限", "查看权限信息", "ROLE_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.PERMISSION_UPDATE, "更新权限", "更新权限信息", "ROLE_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.PERMISSION_DELETE, "删除权限", "删除权限", "ROLE_MANAGEMENT", now),
                    
                    // 组织管理权限
                    createSystemPermission(SystemPermissions.ORGANIZATION_CREATE, "创建组织", "创建新组织", "ORGANIZATION_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.ORGANIZATION_READ, "查看组织", "查看组织信息", "ORGANIZATION_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.ORGANIZATION_UPDATE, "更新组织", "更新组织信息", "ORGANIZATION_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.ORGANIZATION_DELETE, "删除组织", "删除组织", "ORGANIZATION_MANAGEMENT", now),
                    
                    // 系统管理权限
                    createSystemPermission(SystemPermissions.SYSTEM_CONFIG, "系统配置", "管理系统配置", "SYSTEM_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.SYSTEM_BACKUP, "系统备份", "执行系统备份", "SYSTEM_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.SYSTEM_RESTORE, "系统恢复", "执行系统恢复", "SYSTEM_MANAGEMENT", now),
                    createSystemPermission(SystemPermissions.SYSTEM_LOG, "系统日志", "查看系统日志", "SYSTEM_MANAGEMENT", now)
                )
                
                val createdPermissions = mutableListOf<Permission>()
                
                for (permission in systemPermissions) {
                    // 检查权限是否已存在
                    val existingPermission = PermissionTable.select {
                        PermissionTable.code eq permission.code
                    }.singleOrNull()
                    
                    if (existingPermission == null) {
                        // 创建新权限
                        val permissionEntity = PermissionEntity.new {
                            name = permission.name
                            code = permission.code
                            description = permission.description
                            category = permission.category
                            isSystem = true
                            createdAt = now
                            updatedAt = now
                        }
                        createdPermissions.add(permissionEntity.toModel())
                    } else {
                        // 权限已存在，返回现有权限
                        createdPermissions.add(PermissionEntity.wrapRow(existingPermission).toModel())
                    }
                }
                
                createdPermissions
            } catch (e: Exception) {
                logger.error("初始化系统权限失败: ${e.message}", e)
                emptyList()
            }
        }
    }
    
    /**
     * 创建系统权限对象
     */
    private fun createSystemPermission(
        code: String,
        name: String,
        description: String,
        category: String,
        timestamp: Instant
    ): Permission {
        return Permission(
            id = UUID.randomUUID().toString(),
            name = name,
            code = code,
            description = description,
            category = category,
            isSystem = true,
            createdAt = timestamp,
            updatedAt = timestamp
        )
    }
} 