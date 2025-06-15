package com.kace.analytics.api.model.response

import com.kace.analytics.domain.model.Event
import kotlinx.serialization.Serializable

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
    val properties: Map<String, String>,
    val timestamp: String,
    val appVersion: String? = null,
    val deviceInfo: Map<String, String>? = null,
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
                properties = event.properties.mapValues { it.value.toString() },
                timestamp = event.timestamp.toString(),
                appVersion = event.appVersion,
                deviceInfo = event.deviceInfo?.mapValues { it.value.toString() },
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
    val limit: Int,
    val offset: Int
)

/**
 * 事件统计响应
 */
@Serializable
data class EventCountResponse(
    val count: Long,
    val type: String? = null,
    val startTime: String? = null,
    val endTime: String? = null
) 