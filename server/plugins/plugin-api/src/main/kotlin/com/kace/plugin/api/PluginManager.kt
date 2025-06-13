package com.kace.plugin.api

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
     * 初始化所有插件
     */
    fun initializeAll()
    
    /**
     * 销毁所有插件
     */
    fun destroyAll()
} 