package com.github.kirer.kace.plugin

import kotlinx.serialization.Serializable

/**
 * 插件接口
 * 所有插件必须实现此接口
 */
interface Plugin {
    /**
     * 插件元数据
     */
    val metadata: PluginMetadata
    
    /**
     * 插件初始化
     * 在插件被加载时调用
     */
    fun initialize()
    
    /**
     * 插件启用
     * 在插件被启用时调用
     */
    fun onEnable()
    
    /**
     * 插件禁用
     * 在插件被禁用时调用
     */
    fun onDisable()
}

/**
 * 插件依赖描述
 */
@Serializable
data class PluginDependency(
    /**
     * 依赖插件ID
     */
    val id: String,
    
    /**
     * 依赖插件所需版本
     * 可以是具体版本号或通配符"*"表示任意版本
     */
    val version: String
)

/**
 * 插件元数据
 */
@Serializable
data class PluginMetadata(
    /**
     * 插件ID，必须唯一
     */
    val id: String,
    
    /**
     * 插件名称
     */
    val name: String,
    
    /**
     * 插件版本
     */
    val version: String,
    
    /**
     * 插件描述
     */
    val description: String,
    
    /**
     * 插件作者
     */
    val author: String,
    
    /**
     * 插件主类的完全限定名
     */
    val mainClass: String,
    
    /**
     * 插件依赖列表
     */
    val dependencies: List<PluginDependency> = emptyList(),
    
    /**
     * 插件文件哈希值，用于验证完整性
     */
    val fileHash: String = ""
) 