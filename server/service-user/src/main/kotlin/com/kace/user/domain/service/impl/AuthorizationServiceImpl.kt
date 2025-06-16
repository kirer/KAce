package com.kace.user.domain.service.impl

import com.kace.user.domain.model.Permission
import com.kace.user.domain.repository.PermissionRepository
import com.kace.user.domain.repository.RoleRepository
import com.kace.user.domain.repository.UserRoleRepository
import com.kace.user.domain.service.AuthorizationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 用户授权服务实现
 */
@Service
class AuthorizationServiceImpl(
    private val userRoleRepository: UserRoleRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) : AuthorizationService {

    private val logger = LoggerFactory.getLogger(AuthorizationServiceImpl::class.java)

    /**
     * 检查用户是否拥有指定权限
     */
    override suspend fun hasPermission(userId: UUID, permissionCode: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val userPermissions = getUserPermissions(userId)
            return@withContext userPermissions.any { it.code == permissionCode }
        } catch (e: Exception) {
            logger.error("Error checking permission for user: $userId, permission: $permissionCode", e)
            return@withContext false
        }
    }

    /**
     * 检查用户是否拥有指定角色
     */
    override suspend fun hasRole(userId: UUID, roleCode: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val userRoles = getUserRoles(userId)
            return@withContext userRoles.contains(roleCode)
        } catch (e: Exception) {
            logger.error("Error checking role for user: $userId, role: $roleCode", e)
            return@withContext false
        }
    }

    /**
     * 获取用户的所有权限
     */
    override suspend fun getUserPermissions(userId: UUID): List<Permission> = withContext(Dispatchers.IO) {
        try {
            // 获取用户的所有角色ID
            val roleIds = userRoleRepository.findRoleIdsByUserId(userId)
            if (roleIds.isEmpty()) {
                return@withContext emptyList()
            }
            
            // 获取这些角色的所有权限
            return@withContext permissionRepository.findPermissionsByRoleIds(roleIds)
        } catch (e: Exception) {
            logger.error("Error getting permissions for user: $userId", e)
            return@withContext emptyList()
        }
    }

    /**
     * 获取用户的所有角色代码
     */
    override suspend fun getUserRoles(userId: UUID): List<String> = withContext(Dispatchers.IO) {
        try {
            // 获取用户的所有角色ID
            val roleIds = userRoleRepository.findRoleIdsByUserId(userId)
            if (roleIds.isEmpty()) {
                return@withContext emptyList()
            }
            
            // 获取角色代码
            return@withContext roleRepository.findRoleCodesByIds(roleIds)
        } catch (e: Exception) {
            logger.error("Error getting roles for user: $userId", e)
            return@withContext emptyList()
        }
    }

    /**
     * 授予用户角色
     */
    @Transactional
    override suspend fun assignRoleToUser(userId: UUID, roleId: UUID): Boolean = withContext(Dispatchers.IO) {
        try {
            return@withContext userRoleRepository.assignRole(userId, roleId)
        } catch (e: Exception) {
            logger.error("Error assigning role to user: $userId, role: $roleId", e)
            return@withContext false
        }
    }

    /**
     * 撤销用户角色
     */
    @Transactional
    override suspend fun revokeRoleFromUser(userId: UUID, roleId: UUID): Boolean = withContext(Dispatchers.IO) {
        try {
            return@withContext userRoleRepository.revokeRole(userId, roleId)
        } catch (e: Exception) {
            logger.error("Error revoking role from user: $userId, role: $roleId", e)
            return@withContext false
        }
    }

    /**
     * 授予角色权限
     */
    @Transactional
    override suspend fun assignPermissionToRole(roleId: UUID, permissionId: UUID): Boolean = withContext(Dispatchers.IO) {
        try {
            return@withContext roleRepository.assignPermission(roleId, permissionId)
        } catch (e: Exception) {
            logger.error("Error assigning permission to role: $roleId, permission: $permissionId", e)
            return@withContext false
        }
    }

    /**
     * 撤销角色权限
     */
    @Transactional
    override suspend fun revokePermissionFromRole(roleId: UUID, permissionId: UUID): Boolean = withContext(Dispatchers.IO) {
        try {
            return@withContext roleRepository.revokePermission(roleId, permissionId)
        } catch (e: Exception) {
            logger.error("Error revoking permission from role: $roleId, permission: $permissionId", e)
            return@withContext false
        }
    }

    /**
     * 检查用户是否可以访问特定资源
     */
    override suspend fun canAccessResource(userId: UUID, resourceType: String, resourceId: UUID, action: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // 获取用户的所有权限
            val permissions = getUserPermissions(userId)
            
            // 检查用户是否拥有特定资源的权限
            // 例如：资源类型.资源ID.动作
            val requiredPermissionPattern = "$resourceType.$action"
            val specificPermissionPattern = "$resourceType.$resourceId.$action"
            
            return@withContext permissions.any { permission ->
                val permCode = permission.code
                permCode == "admin" || // 管理员权限
                permCode == "$resourceType.*" || // 资源类型的所有权限
                permCode == requiredPermissionPattern || // 资源类型的特定动作权限
                permCode == specificPermissionPattern // 特定资源的特定动作权限
            }
        } catch (e: Exception) {
            logger.error("Error checking resource access: user=$userId, resource=$resourceType:$resourceId, action=$action", e)
            return@withContext false
        }
    }
} 