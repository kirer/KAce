package com.kace.analytics.api.model.response

import com.kace.analytics.domain.model.Event
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * 事件响应
 */
@Serializable
data class EventResponse(
    val id: String,
    val type: String,
    val name: String,
    val userId: String? = null,
    val sessionId: String? = null,
    val properties: Map<String, Any>,
    val timestamp: String,
    val appVersion: String? = null,
    val deviceInfo: Map<String, Any>? = null,
    val source: String? = null
) {
    companion object {
        /**
         * 从领域模型转换为响应模型
         */
        fun fromEvent(event: Event): EventResponse {
            return EventResponse(
                id = event.id.toString(),
                type = event.type,
                name = event.name,
                userId = event.userId?.toString(),
                sessionId = event.sessionId,
                properties = event.properties,
                timestamp = event.timestamp.toString(),
                appVersion = event.appVersion,
                deviceInfo = event.deviceInfo,
                source = event.source
            )
        }
    }
}

/**
 * 事件列表响应
 */
@Serializable
data class EventListResponse(
    val events: List<EventResponse>,
    val total: Long,
    val page: Int,
    val size: Int
)

/**
 * 事件统计响应
 */
@Serializable
data class EventCountResponse(
    val count: Long,
    val type: String? = null,
    val name: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val source: String? = null
)

/**
 * 事件趋势响应
 */
@Serializable
data class EventTrendResponse(
    val data: List<EventTrendPoint>
)

/**
 * 事件趋势点
 */
@Serializable
data class EventTrendPoint(
    val date: String,
    val count: Long
)

/**
 * 热门事件响应
 */
@Serializable
data class TopEventsResponse(
    val events: List<TopEvent>
)

/**
 * 热门事件
 */
@Serializable
data class TopEvent(
    val name: String,
    val count: Long
) 