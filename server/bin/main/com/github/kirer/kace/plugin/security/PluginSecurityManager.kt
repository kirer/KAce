package com.github.kirer.kace.plugin.security

import com.github.kirer.kace.log.LoggerFactory
import java.io.FilePermission
import java.lang.reflect.ReflectPermission
import java.net.SocketPermission
import java.security.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 插件安全管理器
 * 负责管理插件的权限和资源访问控制
 */
class PluginSecurityManager : SecurityManager() {
    
    private val logger = LoggerFactory.getSystemLogger(PluginSecurityManager::class.java)
    private val pluginDomains = ConcurrentHashMap<String, ProtectionDomain>()
    private val pluginPermissions = ConcurrentHashMap<String, Permissions>()
    
    // 默认的系统安全管理器，用于委托处理非插件代码的安全检查
    private val systemSecurityManager = System.getSecurityManager()
    
    /**
     * 注册插件
     * @param pluginId 插件ID
     * @param domain 插件保护域
     * @param permissions 插件权限
     */
    fun registerPlugin(pluginId: String, domain: ProtectionDomain, permissions: Permissions) {
        pluginDomains[pluginId] = domain
        pluginPermissions[pluginId] = permissions
        logger.debug("注册插件安全域: $pluginId")
    }
    
    /**
     * 注销插件
     * @param pluginId 插件ID
     */
    fun unregisterPlugin(pluginId: String) {
        pluginDomains.remove(pluginId)
        pluginPermissions.remove(pluginId)
        logger.debug("注销插件安全域: $pluginId")
    }
    
    /**
     * 检查权限
     * @param perm 权限
     */
    override fun checkPermission(perm: Permission) {
        // 获取当前调用栈
        val context = AccessController.getContext()
        
        // 检查是否是插件代码
        val pluginId = getPluginIdFromContext(context)
        if (pluginId != null) {
            // 是插件代码，检查插件权限
            val permissions = pluginPermissions[pluginId]
            if (permissions == null || !permissions.implies(perm)) {
                logger.warn("插件 $pluginId 尝试执行未授权操作: $perm")
                throw SecurityException("插件 $pluginId 没有权限执行此操作: $perm")
            }
        } else {
            // 不是插件代码，委托给系统安全管理器
            systemSecurityManager?.checkPermission(perm)
        }
    }
    
    /**
     * 检查权限（带上下文）
     * @param perm 权限
     * @param context 访问控制上下文
     */
    override fun checkPermission(perm: Permission, context: Any) {
        if (context is AccessControlContext) {
            val pluginId = getPluginIdFromContext(context)
            if (pluginId != null) {
                val permissions = pluginPermissions[pluginId]
                if (permissions == null || !permissions.implies(perm)) {
                    logger.warn("插件 $pluginId 尝试执行未授权操作: $perm")
                    throw SecurityException("插件 $pluginId 没有权限执行此操作: $perm")
                }
            } else {
                systemSecurityManager?.checkPermission(perm, context)
            }
        } else {
            throw SecurityException("无效的访问控制上下文")
        }
    }
    
    /**
     * 从访问控制上下文中获取插件ID
     * @param context 访问控制上下文
     * @return 插件ID，如果不是插件代码则返回null
     */
    private fun getPluginIdFromContext(context: AccessControlContext): String? {
        val domains = getDomains(context)
        for (domain in domains) {
            for ((pluginId, pluginDomain) in pluginDomains) {
                if (domain == pluginDomain) {
                    return pluginId
                }
            }
        }
        return null
    }
    
    /**
     * 获取访问控制上下文中的所有保护域
     * @param context 访问控制上下文
     * @return 保护域列表
     */
    private fun getDomains(context: AccessControlContext): Array<ProtectionDomain> {
        // 使用反射获取AccessControlContext中的保护域
        try {
            val domainsField = AccessControlContext::class.java.getDeclaredField("context")
            domainsField.isAccessible = true
            val pdArray = domainsField.get(context)
            
            if (pdArray is Array<*>) {
                @Suppress("UNCHECKED_CAST")
                return pdArray as Array<ProtectionDomain>
            }
        } catch (e: Exception) {
            logger.error("获取保护域失败", e)
        }
        
        return emptyArray()
    }
    
    /**
     * 创建插件权限集
     * @param pluginId 插件ID
     * @param restrictedMode 是否为受限模式
     * @return 权限集
     */
    fun createPluginPermissions(pluginId: String, restrictedMode: Boolean): Permissions {
        val permissions = Permissions()
        
        // 基本运行时权限
        permissions.add(RuntimePermission("accessDeclaredMembers"))
        permissions.add(RuntimePermission("getClassLoader"))
        permissions.add(RuntimePermission("getProtectionDomain"))
        
        // 反射权限
        permissions.add(ReflectPermission("suppressAccessChecks"))
        
        // 如果不是受限模式，添加更多权限
        if (!restrictedMode) {
            // 文件读取权限（仅限插件目录）
            permissions.add(FilePermission("plugins/$pluginId/-", "read"))
            
            // 网络权限（受限）
            permissions.add(SocketPermission("localhost:1024-", "connect,resolve"))
            
            // 属性访问权限（受限）
            permissions.add(PropertyPermission("user.*", "read"))
            permissions.add(PropertyPermission("os.*", "read"))
            permissions.add(PropertyPermission("java.version", "read"))
        }
        
        return permissions
    }
    
    /**
     * 创建插件保护域
     * @param pluginId 插件ID
     * @param codeSource 代码源
     * @param permissions 权限集
     * @return 保护域
     */
    fun createPluginDomain(pluginId: String, codeSource: CodeSource, permissions: Permissions): ProtectionDomain {
        return ProtectionDomain(codeSource, permissions)
    }
} 