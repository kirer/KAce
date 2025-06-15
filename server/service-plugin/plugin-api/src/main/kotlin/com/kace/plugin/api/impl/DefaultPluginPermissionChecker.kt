package com.kace.plugin.api.impl

import com.kace.plugin.api.*
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 默认插件权限检查器实现
 */
class DefaultPluginPermissionChecker : PluginPermissionChecker {
    private val logger = LoggerFactory.getLogger(DefaultPluginPermissionChecker::class.java)
    
    // 系统定义的所有权限
    private val systemPermissions = mutableListOf<PluginPermission>()
    
    // 插件权限映射表，键为插件ID，值为权限ID集合
    private val pluginPermissions = ConcurrentHashMap<String, MutableSet<String>>()
    
    init {
        // 初始化系统权限
        initSystemPermissions()
    }
    
    /**
     * 初始化系统权限定义
     */
    private fun initSystemPermissions() {
        // 系统级权限
        systemPermissions.add(
            PluginPermission(
                id = "system.config.read",
                name = "读取系统配置",
                description = "允许插件读取系统配置信息",
                scope = PermissionScope.SYSTEM
            )
        )
        systemPermissions.add(
            PluginPermission(
                id = "system.config.write",
                name = "修改系统配置",
                description = "允许插件修改系统配置信息",
                scope = PermissionScope.SYSTEM
            )
        )
        
        // 内容级权限
        systemPermissions.add(
            PluginPermission(
                id = "content.read",
                name = "读取内容",
                description = "允许插件读取内容",
                scope = PermissionScope.CONTENT
            )
        )
        systemPermissions.add(
            PluginPermission(
                id = "content.write",
                name = "写入内容",
                description = "允许插件创建或修改内容",
                scope = PermissionScope.CONTENT
            )
        )
        systemPermissions.add(
            PluginPermission(
                id = "content.delete",
                name = "删除内容",
                description = "允许插件删除内容",
                scope = PermissionScope.CONTENT
            )
        )
        
        // 用户级权限
        systemPermissions.add(
            PluginPermission(
                id = "user.read",
                name = "读取用户信息",
                description = "允许插件读取用户信息",
                scope = PermissionScope.USER
            )
        )
        systemPermissions.add(
            PluginPermission(
                id = "user.write",
                name = "修改用户信息",
                description = "允许插件修改用户信息",
                scope = PermissionScope.USER
            )
        )
        
        // API级权限
        systemPermissions.add(
            PluginPermission(
                id = "api.external.call",
                name = "调用外部API",
                description = "允许插件调用外部API",
                scope = PermissionScope.API
            )
        )
    }
    
    /**
     * 检查权限是否存在
     */
    private fun permissionExists(permissionId: String): Boolean {
        return systemPermissions.any { it.id == permissionId }
    }
    
    /**
     * 检查插件是否具有指定权限
     */
    override fun checkPermission(pluginId: String, permissionId: String): PermissionCheckResult {
        if (!permissionExists(permissionId)) {
            return PermissionCheckResult(
                granted = false,
                reason = "权限不存在: $permissionId"
            )
        }
        
        val permissions = pluginPermissions[pluginId]
        if (permissions == null || !permissions.contains(permissionId)) {
            return PermissionCheckResult(
                granted = false,
                reason = "插件未被授予此权限: $permissionId"
            )
        }
        
        return PermissionCheckResult(granted = true)
    }
    
    /**
     * 授予插件权限
     */
    override fun grantPermission(pluginId: String, permissionId: String): Boolean {
        if (!permissionExists(permissionId)) {
            logger.warn("尝试授予不存在的权限: $permissionId")
            return false
        }
        
        val permissions = pluginPermissions.computeIfAbsent(pluginId) { mutableSetOf() }
        permissions.add(permissionId)
        logger.info("授予插件 $pluginId 权限: $permissionId")
        return true
    }
    
    /**
     * 撤销插件权限
     */
    override fun revokePermission(pluginId: String, permissionId: String): Boolean {
        val permissions = pluginPermissions[pluginId] ?: return false
        val result = permissions.remove(permissionId)
        if (result) {
            logger.info("撤销插件 $pluginId 权限: $permissionId")
        }
        return result
    }
    
    /**
     * 获取插件拥有的所有权限
     */
    override fun getPluginPermissions(pluginId: String): List<PluginPermission> {
        val permissionIds = pluginPermissions[pluginId] ?: return emptyList()
        return systemPermissions.filter { permissionIds.contains(it.id) }
    }
    
    /**
     * 获取系统中定义的所有权限
     */
    override fun getAllPermissions(): List<PluginPermission> {
        return systemPermissions.toList()
    }
}
