package com.kace.analytics.api.model.request

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * 创建事件请求
 */
@Serializable
data class CreateEventRequest(
    val type: String,
    val name: String,
    val userId: String? = null,
    val sessionId: String? = null,
    val properties: Map<String, String>,
    val timestamp: String? = null, // ISO-8601格式的时间字符串
    val appVersion: String? = null,
    val deviceInfo: Map<String, String>? = null,
    val source: String? = null
)

/**
 * 批量创建事件请求
 */
@Serializable
data class BatchCreateEventsRequest(
    val events: List<CreateEventRequest>
)

/**
 * 查询事件请求
 */
@Serializable
data class QueryEventsRequest(
    val type: String? = null,
    val startTime: String? = null, // ISO-8601格式的时间字符串
    val endTime: String? = null, // ISO-8601格式的时间字符串
    val userId: String? = null,
    val sessionId: String? = null,
    val limit: Int = 20,
    val offset: Int = 0
) 