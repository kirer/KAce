package com.kace.common.plugin

import io.ktor.server.routing.*
import java.util.*

/**
 * 插件管理器
 */
class PluginManager {
    private val plugins = mutableMapOf<String, ContentPlugin>()
    
    /**
     * 加载插件
     */
    fun loadPlugins() {
        ServiceLoader.load(ContentPlugin::class.java).forEach { plugin ->
            plugins[plugin.id] = plugin
            plugin.initialize()
        }
    }
    
    /**
     * 获取插件
     */
    fun getPlugin(id: String): ContentPlugin? = plugins[id]
    
    /**
     * 获取所有插件
     */
    fun getAllPlugins(): List<ContentPlugin> = plugins.values.toList()
    
    /**
     * 广播事件给所有插件
     */
    fun broadcastEvent(event: PluginEvent) {
        plugins.values.forEach { it.handleEvent(event) }
    }
    
    /**
     * 注册所有插件的路由
     */
    fun registerAllRoutes(routing: Routing) {
        plugins.values.forEach { plugin ->
            routing.route("/plugins/${plugin.id}") {
                plugin.registerRoutes(this)
            }
        }
    }
    
    /**
     * 清理所有插件
     */
    fun cleanup() {
        plugins.values.forEach { it.cleanup() }
        plugins.clear()
    }
} 