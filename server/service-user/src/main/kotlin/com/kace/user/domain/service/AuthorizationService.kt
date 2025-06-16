package com.kace.user.domain.service

import com.kace.user.domain.model.Permission
import java.util.UUID

/**
 * 用户授权服务接口
 */
interface AuthorizationService {
    /**
     * 检查用户是否拥有指定权限
     *
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return 如果用户拥有权限则返回true，否则返回false
     */
    suspend fun hasPermission(userId: UUID, permissionCode: String): Boolean
    
    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleCode 角色代码
     * @return 如果用户拥有角色则返回true，否则返回false
     */
    suspend fun hasRole(userId: UUID, roleCode: String): Boolean
    
    /**
     * 获取用户的所有权限
     *
     * @param userId 用户ID
     * @return 用户的权限列表
     */
    suspend fun getUserPermissions(userId: UUID): List<Permission>
    
    /**
     * 获取用户的所有角色代码
     *
     * @param userId 用户ID
     * @return 用户的角色代码列表
     */
    suspend fun getUserRoles(userId: UUID): List<String>
    
    /**
     * 授予用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 如果授予成功则返回true，否则返回false
     */
    suspend fun assignRoleToUser(userId: UUID, roleId: UUID): Boolean
    
    /**
     * 撤销用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 如果撤销成功则返回true，否则返回false
     */
    suspend fun revokeRoleFromUser(userId: UUID, roleId: UUID): Boolean
    
    /**
     * 授予角色权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 如果授予成功则返回true，否则返回false
     */
    suspend fun assignPermissionToRole(roleId: UUID, permissionId: UUID): Boolean
    
    /**
     * 撤销角色权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 如果撤销成功则返回true，否则返回false
     */
    suspend fun revokePermissionFromRole(roleId: UUID, permissionId: UUID): Boolean
    
    /**
     * 检查用户是否可以访问特定资源
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param action 操作（如：read, write, delete等）
     * @return 如果用户有权限访问则返回true，否则返回false
     */
    suspend fun canAccessResource(userId: UUID, resourceType: String, resourceId: UUID, action: String): Boolean
} 