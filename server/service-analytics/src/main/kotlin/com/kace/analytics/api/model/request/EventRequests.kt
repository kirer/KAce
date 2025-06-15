package com.kace.analytics.api.model.request

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * 记录事件请求
 */
@Serializable
data class RecordEventRequest(
    val type: String,
    val name: String,
    val userId: UUID? = null,
    val sessionId: String? = null,
    val properties: Map<String, Any>? = null,
    val timestamp: Instant? = null,
    val appVersion: String? = null,
    val deviceInfo: Map<String, Any>? = null,
    val source: String? = null
)

/**
 * 批量记录事件请求
 */
@Serializable
data class BatchRecordEventsRequest(
    val events: List<RecordEventRequest>
)

/**
 * 查询事件请求
 */
@Serializable
data class QueryEventsRequest(
    val type: String? = null,
    val name: String? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val userId: UUID? = null,
    val sessionId: String? = null,
    val source: String? = null,
    val page: Int = 1,
    val size: Int = 20
)

/**
 * 事件趋势请求
 */
@Serializable
data class EventTrendRequest(
    val type: String,
    val name: String? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val interval: String = "day"
)

/**
 * 事件聚合请求
 */
@Serializable
data class EventAggregateRequest(
    val type: String? = null,
    val name: String? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val source: String? = null,
    val groupByFields: List<String> = listOf("type"),
    val aggregateField: String? = null,
    val aggregationType: String = "COUNT"
) 