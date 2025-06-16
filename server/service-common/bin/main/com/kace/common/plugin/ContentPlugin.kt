package com.kace.common.plugin

import io.ktor.server.routing.*
import com.kace.common.model.dto.PageRequest
import com.kace.common.model.dto.PageResponse

/**
 * 内容插件接口
 */
interface ContentPlugin {
    /**
     * 插件ID
     */
    val id: String
    
    /**
     * 插件名称
     */
    val name: String
    
    /**
     * 插件版本
     */
    val version: String
    
    /**
     * 获取内容类型定义
     */
    fun getContentTypes(): List<ContentType>
    
    /**
     * 注册API路由
     */
    fun registerRoutes(routing: Routing)
    
    /**
     * 初始化插件
     */
    fun initialize()
    
    /**
     * 清理插件资源
     */
    fun cleanup()
    
    /**
     * 处理插件事件
     */
    fun handleEvent(event: PluginEvent)
}

/**
 * 内容类型定义
 */
data class ContentType(
    val id: String,
    val name: String,
    val description: String? = null,
    val fields: List<ContentField>
)

/**
 * 内容字段定义
 */
data class ContentField(
    val id: String,
    val name: String,
    val type: FieldType,
    val required: Boolean = false,
    val defaultValue: Any? = null,
    val validations: List<Validation> = emptyList()
)

/**
 * 字段类型枚举
 */
enum class FieldType {
    TEXT, RICH_TEXT, NUMBER, DATE, BOOLEAN, MEDIA, REFERENCE, JSON
}

/**
 * 字段验证定义
 */
data class Validation(
    val type: ValidationType,
    val params: Map<String, Any> = emptyMap()
)

/**
 * 验证类型枚举
 */
enum class ValidationType {
    REQUIRED, MIN_LENGTH, MAX_LENGTH, PATTERN, MIN, MAX, EMAIL, URL
}

/**
 * 插件事件
 */
data class PluginEvent(
    val type: String,
    val payload: Map<String, Any> = emptyMap()
) 