package com.kace.plugin.api

/**
 * 内容插件接口
 */
interface ContentPlugin {
    /**
     * 获取插件ID
     */
    fun getId(): String
    
    /**
     * 获取插件名称
     */
    fun getName(): String
    
    /**
     * 获取插件描述
     */
    fun getDescription(): String
    
    /**
     * 获取插件版本
     */
    fun getVersion(): String
    
    /**
     * 初始化插件
     */
    fun initialize()
    
    /**
     * 销毁插件
     */
    fun destroy()
} 