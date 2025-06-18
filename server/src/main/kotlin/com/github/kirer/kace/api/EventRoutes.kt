package com.github.kirer.kace.api

import com.github.kirer.kace.event.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 事件查询请求DTO
 */
@Serializable
data class EventQueryRequest(
    val types: List<String>? = null,
    val sources: List<String>? = null,
    val fromTime: String? = null,
    val toTime: String? = null,
    val limit: Int = 100,
    val offset: Int = 0
)

/**
 * 事件响应DTO
 */
@Serializable
data class EventResponse(
    val id: String,
    val type: String,
    val timestamp: String,
    val source: String,
    val data: Map<String, String>,
    val cancelable: Boolean,
    val cancelled: Boolean
)

/**
 * 事件发布请求DTO
 */
@Serializable
data class EventPublishRequest(
    val type: String,
    val source: String,
    val data: Map<String, String> = emptyMap(),
    val cancelable: Boolean = false,
    val async: Boolean = true
)

/**
 * 事件API路由
 */
fun Route.eventRoutes() {
    val eventBus by inject<EventBus>()
    val eventStorage by inject<EventStorage>()
    val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    
    route("/api/events") {
        // 查询事件
        get {
            val types = call.request.queryParameters.getAll("type")?.toSet()
            val sources = call.request.queryParameters.getAll("source")?.toSet()
            val fromTime = call.request.queryParameters["fromTime"]?.let {
                try {
                    LocalDateTime.parse(it, dateTimeFormatter)
                } catch (e: Exception) {
                    null
                }
            }
            val toTime = call.request.queryParameters["toTime"]?.let {
                try {
                    LocalDateTime.parse(it, dateTimeFormatter)
                } catch (e: Exception) {
                    null
                }
            }
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 1000) ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
            
            val criteria = EventQueryCriteria(
                types = types,
                sources = sources,
                fromTime = fromTime,
                toTime = toTime,
                limit = limit,
                offset = offset
            )
            
            val events = eventStorage.queryEvents(criteria)
            val response = events.map { event ->
                EventResponse(
                    id = event.id,
                    type = event.type,
                    timestamp = event.timestamp.format(dateTimeFormatter),
                    source = event.source,
                    data = event.data.mapValues { it.value.toString() },
                    cancelable = event.cancelable,
                    cancelled = event.cancelled
                )
            }
            
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        // 发布事件
        post {
            val request = call.receive<EventPublishRequest>()
            
            val data = request.data.mapValues { it.value as Any }
            
            val event = BusinessEvent(
                type = request.type,
                source = request.source,
                data = data,
                cancelable = request.cancelable
            )
            
            if (request.async) {
                eventBus.publishAsync(event)
            } else {
                eventBus.publishSync(event)
            }
            
            call.respond(HttpStatusCode.Created, mapOf(
                "success" to true,
                "data" to mapOf(
                    "id" to event.id,
                    "type" to event.type,
                    "timestamp" to event.timestamp.format(dateTimeFormatter),
                    "source" to event.source
                )
            ))
        }
        
        // 清理事件
        delete {
            val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 30
            val before = LocalDateTime.now().minusDays(days.toLong())
            val count = eventStorage.cleanupEvents(before)
            
            call.respond(mapOf(
                "success" to true,
                "data" to mapOf("deletedCount" to count)
            ))
        }
    }
} 