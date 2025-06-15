package com.kace.plugin.api

import kotlinx.serialization.Serializable

/**
 * 插件权限
 */
@Serializable
data class PluginPermission(
    val id: String,
    val name: String,
    val description: String,
    val scope: PermissionScope
)

/**
 * 权限范围
 */
@Serializable
enum class PermissionScope {
    /**
     * 系统级权限，可以访问系统资源
     */
    SYSTEM,
    
    /**
     * 内容级权限，可以访问和操作内容
     */
    CONTENT,
    
    /**
     * 用户级权限，可以访问用户信息
     */
    USER,
    
    /**
     * API级权限，可以调用特定API
     */
    API
}

/**
 * 权限检查结果
 */
data class PermissionCheckResult(
    val granted: Boolean,
    val reason: String? = null
)

/**
 * 插件权限检查器
 */
interface PluginPermissionChecker {
    /**
     * 检查插件是否具有指定权限
     * 
     * @param pluginId 插件ID
     * @param permissionId 权限ID
     * @return 权限检查结果
     */
    fun checkPermission(pluginId: String, permissionId: String): PermissionCheckResult
    
    /**
     * 授予插件权限
     * 
     * @param pluginId 插件ID
     * @param permissionId 权限ID
     * @return 是否成功
     */
    fun grantPermission(pluginId: String, permissionId: String): Boolean
    
    /**
     * 撤销插件权限
     * 
     * @param pluginId 插件ID
     * @param permissionId 权限ID
     * @return 是否成功
     */
    fun revokePermission(pluginId: String, permissionId: String): Boolean
    
    /**
     * 获取插件拥有的所有权限
     * 
     * @param pluginId 插件ID
     * @return 权限列表
     */
    fun getPluginPermissions(pluginId: String): List<PluginPermission>
    
    /**
     * 获取系统中定义的所有权限
     * 
     * @return 权限列表
     */
    fun getAllPermissions(): List<PluginPermission>
}
