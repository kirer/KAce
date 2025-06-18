package com.github.kirer.kace.plugin

import org.slf4j.LoggerFactory

/**
 * 插件管理器
 * 负责插件的加载、初始化、启用和禁用
 */
class PluginManager {
    private val logger = LoggerFactory.getLogger(PluginManager::class.java)
    private val plugins = mutableMapOf<String, Plugin>()
    
    /**
     * 初始化插件管理器
     */
    fun initialize() {
        logger.info("初始化插件管理器")
        // 在这里实现插件的发现和加载
    }
    
    /**
     * 加载插件
     */
    fun loadPlugin(plugin: Plugin): Boolean {
        return try {
            logger.info("加载插件: ${plugin.metadata.id}")
            plugins[plugin.metadata.id] = plugin
            true
        } catch (e: Exception) {
            logger.error("加载插件失败: ${plugin.metadata.id}", e)
            false
        }
    }
    
    /**
     * 启用插件
     */
    fun enablePlugin(pluginId: String): Boolean {
        val plugin = plugins[pluginId] ?: return false
        
        return try {
            logger.info("启用插件: ${plugin.metadata.id}")
            plugin.onEnable()
            true
        } catch (e: Exception) {
            logger.error("启用插件失败: ${plugin.metadata.id}", e)
            false
        }
    }
    
    /**
     * 禁用插件
     */
    fun disablePlugin(pluginId: String): Boolean {
        val plugin = plugins[pluginId] ?: return false
        
        return try {
            logger.info("禁用插件: ${plugin.metadata.id}")
            plugin.onDisable()
            true
        } catch (e: Exception) {
            logger.error("禁用插件失败: ${plugin.metadata.id}", e)
            false
        }
    }
    
    /**
     * 获取所有插件
     */
    fun getPlugins(): Map<String, Plugin> {
        return plugins.toMap()
    }
    
    /**
     * 获取插件
     */
    fun getPlugin(pluginId: String): Plugin? {
        return plugins[pluginId]
    }
} 