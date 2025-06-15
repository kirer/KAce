package com.kace.user.infrastructure.persistence.repository

import com.kace.common.model.dto.PageDto
import com.kace.user.domain.model.Role
import com.kace.user.domain.model.UserRole
import com.kace.user.domain.repository.RoleRepository
import com.kace.user.infrastructure.persistence.entity.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 角色仓库实现
 */
class RoleRepositoryImpl : RoleRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 根据ID获取角色
     */
    override suspend fun getById(id: String): Role? {
        return transaction {
            try {
                val roleEntity = RoleEntity.findById(UUID.fromString(id))
                roleEntity?.toModel()
            } catch (e: Exception) {
                logger.error("获取角色失败: ${e.message}", e)
                null
            }
        }
    }
    
    /**
     * 根据名称获取角色
     */
    override suspend fun getByName(name: String, organizationId: String?): Role? {
        return transaction {
            try {
                val query = if (organizationId != null) {
                    RoleTable.select {
                        (RoleTable.name eq name) and (RoleTable.organizationId eq UUID.fromString(organizationId))
                    }
                } else {
                    RoleTable.select {
                        (RoleTable.name eq name) and (RoleTable.organizationId.isNull())
                    }
                }
                
                val roleRow = query.singleOrNull() ?: return@transaction null
                RoleEntity.wrapRow(roleRow).toModel()
            } catch (e: Exception) {
                logger.error("根据名称获取角色失败: ${e.message}", e)
                null
            }
        }
    }
    
    /**
     * 分页获取角色列表
     */
    override suspend fun getRoles(page: Int, size: Int, organizationId: String?, query: String?): PageDto<Role> {
        return transaction {
            try {
                val offset = (page - 1) * size
                
                // 构建查询条件
                var whereClause: Op<Boolean> = Op.TRUE
                
                if (organizationId != null) {
                    whereClause = whereClause and (RoleTable.organizationId eq UUID.fromString(organizationId))
                }
                
                if (query != null && query.isNotBlank()) {
                    whereClause = whereClause and (
                        RoleTable.name.lowerCase() like "%${query.lowercase()}%" or
                        (RoleTable.description.isNotNull() and (RoleTable.description.lowerCase() like "%${query.lowercase()}%"))
                    )
                }
                
                // 查询总数
                val totalCount = RoleTable.select { whereClause }.count()
                
                // 分页查询
                val roles = RoleTable.select { whereClause }
                    .orderBy(RoleTable.name to SortOrder.ASC)
                    .limit(size, offset.toLong())
                    .map { RoleEntity.wrapRow(it).toModel() }
                
                PageDto(
                    content = roles,
                    page = page,
                    size = size,
                    totalElements = totalCount,
                    totalPages = ((totalCount + size - 1) / size).toInt()
                )
            } catch (e: Exception) {
                logger.error("分页获取角色列表失败: ${e.message}", e)
                PageDto(emptyList(), page, size, 0, 0)
            }
        }
    }
    
    /**
     * 创建角色
     */
    override suspend fun createRole(role: Role): Role {
        return transaction {
            try {
                val roleEntity = RoleEntity.new(UUID.fromString(role.id)) {
                    name = role.name
                    description = role.description
                    permissions = role.permissions.joinToString(",")
                    isSystem = role.isSystem
                    organizationId = role.organizationId?.let { UUID.fromString(it) }
                    createdAt = role.createdAt
                    updatedAt = role.updatedAt
                }
                roleEntity.toModel()
            } catch (e: Exception) {
                logger.error("创建角色失败: ${e.message}", e)
                throw e
            }
        }
    }
    
    /**
     * 更新角色
     */
    override suspend fun updateRole(role: Role): Role {
        return transaction {
            try {
                val roleEntity = RoleEntity.findById(UUID.fromString(role.id))
                    ?: throw NoSuchElementException("角色不存在: ${role.id}")
                
                roleEntity.name = role.name
                roleEntity.description = role.description
                roleEntity.permissions = role.permissions.joinToString(",")
                roleEntity.updatedAt = Clock.System.now()
                
                roleEntity.toModel()
            } catch (e: Exception) {
                logger.error("更新角色失败: ${e.message}", e)
                throw e
            }
        }
    }
    
    /**
     * 删除角色
     */
    override suspend fun deleteRole(id: String): Boolean {
        return transaction {
            try {
                val roleEntity = RoleEntity.findById(UUID.fromString(id))
                    ?: return@transaction false
                
                // 删除角色前先删除相关的用户角色关联
                UserRoleTable.deleteWhere { UserRoleTable.roleId eq UUID.fromString(id) }
                
                // 删除角色
                roleEntity.delete()
                true
            } catch (e: Exception) {
                logger.error("删除角色失败: ${e.message}", e)
                false
            }
        }
    }
    
    /**
     * 获取用户的所有角色
     */
    override suspend fun getUserRoles(userId: String): List<Role> {
        return transaction {
            try {
                val userRoles = UserRoleTable.innerJoin(RoleTable)
                    .select { UserRoleTable.userId eq UUID.fromString(userId) }
                    .map { RoleEntity.wrapRow(it).toModel() }
                
                userRoles
            } catch (e: Exception) {
                logger.error("获取用户角色失败: ${e.message}", e)
                emptyList()
            }
        }
    }
    
    /**
     * 获取角色的所有用户
     */
    override suspend fun getRoleUsers(roleId: String, page: Int, size: Int): PageDto<String> {
        return transaction {
            try {
                val offset = (page - 1) * size
                
                // 查询总数
                val totalCount = UserRoleTable
                    .select { UserRoleTable.roleId eq UUID.fromString(roleId) }
                    .count()
                
                // 分页查询
                val userIds = UserRoleTable
                    .select { UserRoleTable.roleId eq UUID.fromString(roleId) }
                    .limit(size, offset.toLong())
                    .map { it[UserRoleTable.userId].toString() }
                
                PageDto(
                    content = userIds,
                    page = page,
                    size = size,
                    totalElements = totalCount,
                    totalPages = ((totalCount + size - 1) / size).toInt()
                )
            } catch (e: Exception) {
                logger.error("获取角色用户失败: ${e.message}", e)
                PageDto(emptyList(), page, size, 0, 0)
            }
        }
    }
    
    /**
     * 为用户分配角色
     */
    override suspend fun assignRoleToUser(userRole: UserRole): Boolean {
        return insertUserRole(userRole)
    }
    
    /**
     * 移除用户的角色
     */
    override suspend fun removeRoleFromUser(userId: String, roleId: String): Boolean {
        return deleteUserRole(UUID.fromString(userId), UUID.fromString(roleId))
    }
    
    /**
     * 检查用户是否拥有指定角色
     */
    override suspend fun hasRole(userId: String, roleId: String): Boolean {
        return transaction {
            try {
                UserRoleTable.select {
                    (UserRoleTable.userId eq UUID.fromString(userId)) and
                    (UserRoleTable.roleId eq UUID.fromString(roleId))
                }.count() > 0
            } catch (e: Exception) {
                logger.error("检查用户角色失败: ${e.message}", e)
                false
            }
        }
    }
    
    /**
     * 检查用户是否拥有指定角色名称
     */
    override suspend fun hasRoleByName(userId: String, roleName: String): Boolean {
        return transaction {
            try {
                (UserRoleTable innerJoin RoleTable)
                    .select {
                        (UserRoleTable.userId eq UUID.fromString(userId)) and
                        (RoleTable.name eq roleName)
                    }.count() > 0
            } catch (e: Exception) {
                logger.error("检查用户角色名称失败: ${e.message}", e)
                false
            }
        }
    }
} 