package com.kace.plugin.article

import com.kace.plugin.api.ContentPlugin

/**
 * 文章插件实现
 */
class ArticlePlugin : ContentPlugin {
    override fun getId(): String {
        return "article-plugin"
    }
    
    override fun getName(): String {
        return "文章插件"
    }
    
    override fun getDescription(): String {
        return "提供文章管理功能"
    }
    
    override fun getVersion(): String {
        return "0.1.0"
    }
    
    override fun initialize() {
        println("文章插件初始化")
    }
    
    override fun destroy() {
        println("文章插件销毁")
    }
} 