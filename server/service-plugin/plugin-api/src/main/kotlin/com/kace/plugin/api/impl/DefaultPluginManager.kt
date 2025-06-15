package com.kace.plugin.api.impl

import com.kace.plugin.api.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

/**
 * 默认插件管理器实现
 */
class DefaultPluginManager : PluginManager {
    private val logger = LoggerFactory.getLogger(DefaultPluginManager::class.java)
    
    // 插件注册表
    private val plugins = ConcurrentHashMap<String, ContentPlugin>()
    
    // 插件状态
    private val pluginStatus = ConcurrentHashMap<String, Boolean>()
    
    // 插件类加载器
    private val pluginClassLoaders = ConcurrentHashMap<String, ClassLoader>()
    
    // 权限检查器
    private val permissionChecker = DefaultPluginPermissionChecker()
    
    /**
     * 注册插件
     */
    override fun registerPlugin(plugin: ContentPlugin) {
        val pluginId = plugin.getId()
        if (plugins.containsKey(pluginId)) {
            logger.warn("插件已注册: $pluginId")
            return
        }
        
        plugins[pluginId] = plugin
        pluginStatus[pluginId] = true
        logger.info("插件注册成功: ${plugin.getName()} (${plugin.getVersion()})")
    }
    
    /**
     * 卸载插件
     */
    override fun unregisterPlugin(pluginId: String) {
        val plugin = plugins[pluginId]
        if (plugin == null) {
            logger.warn("插件不存在: $pluginId")
            return
        }
        
        try {
            plugin.destroy()
            plugins.remove(pluginId)
            pluginStatus.remove(pluginId)
            
            // 关闭类加载器
            val classLoader = pluginClassLoaders[pluginId]
            if (classLoader is URLClassLoader) {
                classLoader.close()
            }
            pluginClassLoaders.remove(pluginId)
            
            logger.info("插件卸载成功: $pluginId")
        } catch (e: Exception) {
            logger.error("插件卸载失败: $pluginId", e)
        }
    }
    
    /**
     * 获取所有插件
     */
    override fun getAllPlugins(): List<ContentPlugin> {
        return plugins.values.toList()
    }
    
    /**
     * 获取插件
     */
    override fun getPlugin(pluginId: String): ContentPlugin? {
        return plugins[pluginId]
    }
    
    /**
     * 从目录加载插件
     */
    override fun loadPluginsFromDirectory(directory: Path) {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            logger.error("插件目录不存在或不是目录: $directory")
            return
        }
        
        try {
            Files.list(directory)
                .filter { it.toString().endsWith(".jar") }
                .forEach { jarPath ->
                    try {
                        loadPluginFromJar(jarPath)
                    } catch (e: Exception) {
                        logger.error("加载插件失败: $jarPath", e)
                    }
                }
        } catch (e: Exception) {
            logger.error("从目录加载插件失败: $directory", e)
        }
    }
    
    /**
     * 从JAR文件加载插件
     */
    override fun loadPluginFromJar(jarFile: Path): ContentPlugin? {
        if (!Files.exists(jarFile) || !Files.isRegularFile(jarFile)) {
            logger.error("插件JAR文件不存在或不是文件: $jarFile")
            return null
        }
        
        try {
            // 读取插件元数据
            val jarFileObj = JarFile(jarFile.toFile())
            val manifestEntry = jarFileObj.getEntry("META-INF/plugin.properties")
            
            if (manifestEntry == null) {
                logger.error("插件元数据不存在: $jarFile")
                return null
            }
            
            val properties = Properties()
            jarFileObj.getInputStream(manifestEntry).use { properties.load(it) }
            
            val mainClass = properties.getProperty("plugin.mainClass")
            if (mainClass.isNullOrBlank()) {
                logger.error("插件主类未定义: $jarFile")
                return null
            }
            
            // 创建类加载器
            val urls = arrayOf(jarFile.toUri().toURL())
            val classLoader = URLClassLoader(urls, this.javaClass.classLoader)
            
            // 加载插件主类
            val pluginClass = classLoader.loadClass(mainClass)
            val plugin = pluginClass.getDeclaredConstructor().newInstance() as ContentPlugin
            
            // 注册插件
            val pluginId = plugin.getId()
            plugins[pluginId] = plugin
            pluginStatus[pluginId] = true
            pluginClassLoaders[pluginId] = classLoader
            
            // 初始化插件
            plugin.initialize()
            
            // 授予基本权限
            grantBasePermissions(pluginId)
            
            logger.info("插件加载成功: ${plugin.getName()} (${plugin.getVersion()})")
            return plugin
        } catch (e: Exception) {
            logger.error("加载插件失败: $jarFile", e)
            return null
        }
    }
    
    /**
     * 授予基本权限
     */
    private fun grantBasePermissions(pluginId: String) {
        // 默认授予内容读取权限
        grantPermission(pluginId, "content.read")
    }
    
    /**
     * 广播事件到所有插件
     */
    override fun broadcastEvent(event: PluginEvent) {
        plugins.values.forEach { plugin ->
            try {
                if (isPluginEnabled(plugin.getId())) {
                    plugin.handleEvent(event)
                }
            } catch (e: Exception) {
                logger.error("插件处理事件失败: ${plugin.getId()}", e)
            }
        }
    }
    
    /**
     * 发送事件到特定插件
     */
    override fun sendEvent(pluginId: String, event: PluginEvent): Boolean {
        val plugin = plugins[pluginId]
        if (plugin == null || !isPluginEnabled(pluginId)) {
            return false
        }
        
        try {
            plugin.handleEvent(event)
            return true
        } catch (e: Exception) {
            logger.error("插件处理事件失败: $pluginId", e)
            return false
        }
    }
    
    /**
     * 启用插件
     */
    override fun enablePlugin(pluginId: String): Boolean {
        val plugin = plugins[pluginId]
        if (plugin == null) {
            logger.warn("插件不存在: $pluginId")
            return false
        }
        
        pluginStatus[pluginId] = true
        logger.info("插件已启用: $pluginId")
        return true
    }
    
    /**
     * 禁用插件
     */
    override fun disablePlugin(pluginId: String): Boolean {
        val plugin = plugins[pluginId]
        if (plugin == null) {
            logger.warn("插件不存在: $pluginId")
            return false
        }
        
        pluginStatus[pluginId] = false
        logger.info("插件已禁用: $pluginId")
        return true
    }
    
    /**
     * 检查插件是否启用
     */
    override fun isPluginEnabled(pluginId: String): Boolean {
        return pluginStatus[pluginId] ?: false
    }
    
    /**
     * 获取插件内容类型
     */
    override fun getContentTypes(): List<ContentTypeDefinition> {
        return plugins.values
            .filter { isPluginEnabled(it.getId()) }
            .flatMap { it.getContentTypes() }
    }
    
    /**
     * 获取插件路由
     */
    override fun getRoutes(): List<Pair<RouteDefinition, ContentPlugin>> {
        return plugins.values
            .filter { isPluginEnabled(it.getId()) }
            .flatMap { plugin -> plugin.getRoutes().map { it to plugin } }
    }
    
    /**
     * 初始化所有插件
     */
    override fun initializeAll() {
        plugins.values.forEach { plugin ->
            try {
                plugin.initialize()
                logger.info("插件初始化成功: ${plugin.getId()}")
            } catch (e: Exception) {
                logger.error("插件初始化失败: ${plugin.getId()}", e)
            }
        }
    }
    
    /**
     * 销毁所有插件
     */
    override fun destroyAll() {
        plugins.values.forEach { plugin ->
            try {
                plugin.destroy()
                logger.info("插件销毁成功: ${plugin.getId()}")
            } catch (e: Exception) {
                logger.error("插件销毁失败: ${plugin.getId()}", e)
            }
        }
        
        // 关闭所有类加载器
        pluginClassLoaders.values.forEach { classLoader ->
            if (classLoader is URLClassLoader) {
                try {
                    classLoader.close()
                } catch (e: Exception) {
                    logger.error("关闭插件类加载器失败", e)
                }
            }
        }
        
        // 清空集合
        plugins.clear()
        pluginStatus.clear()
        pluginClassLoaders.clear()
    }
    
    /**
     * 检查插件是否具有指定权限
     */
    override fun checkPermission(pluginId: String, permissionId: String): PermissionCheckResult {
        // 检查插件是否存在并已启用
        if (!plugins.containsKey(pluginId)) {
            return PermissionCheckResult(granted = false, reason = "插件不存在: $pluginId")
        }
        
        if (!isPluginEnabled(pluginId)) {
            return PermissionCheckResult(granted = false, reason = "插件已禁用: $pluginId")
        }
        
        // 使用权限检查器检查权限
        return permissionChecker.checkPermission(pluginId, permissionId)
    }
    
    /**
     * 授予插件权限
     */
    override fun grantPermission(pluginId: String, permissionId: String): Boolean {
        // 检查插件是否存在
        if (!plugins.containsKey(pluginId)) {
            logger.warn("尝试授予权限给不存在的插件: $pluginId")
            return false
        }
        
        // 使用权限检查器授予权限
        return permissionChecker.grantPermission(pluginId, permissionId)
    }
    
    /**
     * 撤销插件权限
     */
    override fun revokePermission(pluginId: String, permissionId: String): Boolean {
        // 检查插件是否存在
        if (!plugins.containsKey(pluginId)) {
            logger.warn("尝试撤销不存在的插件的权限: $pluginId")
            return false
        }
        
        // 使用权限检查器撤销权限
        return permissionChecker.revokePermission(pluginId, permissionId)
    }
    
    /**
     * 获取插件拥有的所有权限
     */
    override fun getPluginPermissions(pluginId: String): List<PluginPermission> {
        // 检查插件是否存在
        if (!plugins.containsKey(pluginId)) {
            logger.warn("尝试获取不存在的插件的权限: $pluginId")
            return emptyList()
        }
        
        // 使用权限检查器获取权限
        return permissionChecker.getPluginPermissions(pluginId)
    }
    
    /**
     * 获取系统中定义的所有权限
     */
    override fun getAllPermissions(): List<PluginPermission> {
        return permissionChecker.getAllPermissions()
    }
} 