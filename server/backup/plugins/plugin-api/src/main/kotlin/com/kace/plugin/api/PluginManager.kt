package com.kace.plugin.api

import java.nio.file.Path

/**
 * 插件管理器
 */
interface PluginManager {
    /**
     * 注册插件
     */
    fun registerPlugin(plugin: ContentPlugin)
    
    /**
     * 卸载插件
     */
    fun unregisterPlugin(pluginId: String)
    
    /**
     * 获取所有插件
     */
    fun getAllPlugins(): List<ContentPlugin>
    
    /**
     * 获取插件
     */
    fun getPlugin(pluginId: String): ContentPlugin?
    
    /**
     * 从目录加载插件
     */
    fun loadPluginsFromDirectory(directory: Path)
    
    /**
     * 从JAR文件加载插件
     */
    fun loadPluginFromJar(jarFile: Path): ContentPlugin?
    
    /**
     * 广播事件到所有插件
     */
    fun broadcastEvent(event: PluginEvent)
    
    /**
     * 发送事件到特定插件
     */
    fun sendEvent(pluginId: String, event: PluginEvent): Boolean
    
    /**
     * 启用插件
     */
    fun enablePlugin(pluginId: String): Boolean
    
    /**
     * 禁用插件
     */
    fun disablePlugin(pluginId: String): Boolean
    
    /**
     * 检查插件是否启用
     */
    fun isPluginEnabled(pluginId: String): Boolean
    
    /**
     * 获取插件内容类型
     */
    fun getContentTypes(): List<ContentTypeDefinition>
    
    /**
     * 获取插件路由
     */
    fun getRoutes(): List<Pair<RouteDefinition, ContentPlugin>>
    
    /**
     * 初始化所有插件
     */
    fun initializeAll()
    
    /**
     * 销毁所有插件
     */
    fun destroyAll()
} 