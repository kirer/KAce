package com.kace.analytics.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 事件领域模型
 * 表示系统中发生的各种事件，如页面访问、按钮点击、内容查看等
 */
data class Event(
    val id: UUID? = null,
    val type: String,           // 事件类型，如 PAGE_VIEW, CLICK, CONTENT_VIEW 等
    val name: String,           // 事件名称，如 "homepage_visit", "button_click", "article_view" 等
    val userId: UUID? = null,   // 用户ID，可为空（未登录用户）
    val sessionId: String? = null, // 会话ID
    val properties: Map<String, Any> = emptyMap(), // 事件属性，如页面URL、按钮ID、内容ID等
    val timestamp: Instant = Instant.now(), // 事件发生时间
    val appVersion: String? = null, // 应用版本
    val deviceInfo: Map<String, Any>? = null, // 设备信息
    val source: String? = null  // 事件来源，如 "web", "mobile", "api" 等
)

/**
 * 事件类型枚举
 */
enum class EventType {
    PAGE_VIEW,       // 页面访问
    CONTENT_VIEW,    // 内容查看
    CLICK,           // 点击事件
    SEARCH,          // 搜索事件
    FORM_SUBMIT,     // 表单提交
    ERROR,           // 错误事件
    CUSTOM           // 自定义事件
}

/**
 * 事件过滤器
 * 用于查询事件
 */
data class EventFilter(
    val types: List<String>? = null,
    val names: List<String>? = null,
    val userId: UUID? = null,
    val sessionId: String? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val source: String? = null,
    val properties: Map<String, Any>? = null
)

/**
 * 设备信息
 */
@Serializable
data class DeviceInfo(
    val type: DeviceType,
    val os: String? = null,
    val osVersion: String? = null,
    val browser: String? = null,
    val browserVersion: String? = null,
    val screenSize: String? = null,
    val locale: String? = null
)

/**
 * 设备类型
 */
@Serializable
enum class DeviceType {
    DESKTOP,
    MOBILE,
    TABLET,
    TV,
    OTHER
} 