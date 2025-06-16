package com.kace.plugin.api

import kotlinx.serialization.Serializable

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
     * 获取插件作者
     */
    fun getAuthor(): String
    
    /**
     * 获取插件内容类型定义
     */
    fun getContentTypes(): List<ContentTypeDefinition>
    
    /**
     * 获取插件路由定义
     */
    fun getRoutes(): List<RouteDefinition>
    
    /**
     * 处理事件
     */
    fun handleEvent(event: PluginEvent)
    
    /**
     * 初始化插件
     */
    fun initialize()
    
    /**
     * 销毁插件
     */
    fun destroy()
}

/**
 * 内容类型定义
 */
@Serializable
data class ContentTypeDefinition(
    val id: String,
    val name: String,
    val description: String,
    val fields: List<FieldDefinition>
)

/**
 * 字段定义
 */
@Serializable
data class FieldDefinition(
    val id: String,
    val name: String,
    val type: FieldType,
    val required: Boolean = false,
    val defaultValue: String? = null,
    val validations: List<ValidationRule> = emptyList()
)

/**
 * 字段类型
 */
@Serializable
enum class FieldType {
    TEXT,
    RICH_TEXT,
    NUMBER,
    DATE,
    BOOLEAN,
    MEDIA,
    REFERENCE,
    JSON
}

/**
 * 验证规则
 */
@Serializable
data class ValidationRule(
    val type: ValidationType,
    val params: Map<String, String> = emptyMap()
)

/**
 * 验证类型
 */
@Serializable
enum class ValidationType {
    REQUIRED,
    MIN_LENGTH,
    MAX_LENGTH,
    PATTERN,
    MIN_VALUE,
    MAX_VALUE,
    EMAIL,
    URL
}

/**
 * 路由定义
 */
@Serializable
data class RouteDefinition(
    val path: String,
    val method: HttpMethod,
    val handler: String,
    val auth: Boolean = true
)

/**
 * HTTP方法
 */
@Serializable
enum class HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH
}

/**
 * 插件事件
 */
@Serializable
data class PluginEvent(
    val type: String,
    val payload: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
) 