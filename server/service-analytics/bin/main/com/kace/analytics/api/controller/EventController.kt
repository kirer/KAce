package com.kace.analytics.api.controller

import com.kace.analytics.api.model.request.BatchRecordEventsRequest
import com.kace.analytics.api.model.request.RecordEventRequest
import com.kace.analytics.api.model.request.QueryEventsRequest
import com.kace.analytics.api.model.response.EventCountResponse
import com.kace.analytics.api.model.response.EventListResponse
import com.kace.analytics.api.model.response.EventResponse
import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.model.EventFilter
import com.kace.analytics.domain.service.EventService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 事件控制器
 */
class EventController(private val eventService: EventService) {
    
    private val logger = LoggerFactory.getLogger(EventController::class.java)
    
    /**
     * 配置路由
     */
    fun configureRoutes(routing: Routing) {
        routing.route("/api/v1/events") {
            // 记录单个事件
            post {
                try {
                    val request = call.receive<RecordEventRequest>()
                    val event = createEventFromRequest(request)
                    val createdEvent = eventService.recordEvent(event)
                    call.respond(HttpStatusCode.Created, EventResponse.fromEvent(createdEvent))
                } catch (e: Exception) {
                    logger.error("Failed to record event: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 批量记录事件
            post("/batch") {
                try {
                    val request = call.receive<BatchRecordEventsRequest>()
                    val events = request.events.map { createEventFromRequest(it) }
                    val createdEvents = eventService.recordEvents(events)
                    call.respond(HttpStatusCode.Created, createdEvents.map { EventResponse.fromEvent(it) })
                } catch (e: Exception) {
                    logger.error("Failed to batch record events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 获取单个事件
            get("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing event ID")
                    val event = eventService.getEvent(UUID.fromString(id))
                    if (event != null) {
                        call.respond(HttpStatusCode.OK, EventResponse.fromEvent(event))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Event not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to get event: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 查询事件
            get {
                try {
                    val type = call.request.queryParameters["type"]
                    val name = call.request.queryParameters["name"]
                    val startTime = call.request.queryParameters["startTime"]?.let { Instant.parse(it) }
                    val endTime = call.request.queryParameters["endTime"]?.let { Instant.parse(it) }
                    val userId = call.request.queryParameters["userId"]?.let { UUID.fromString(it) }
                    val sessionId = call.request.queryParameters["sessionId"]
                    val source = call.request.queryParameters["source"]
                    val page = call.request.queryParameters["page"]?.toInt() ?: 1
                    val size = call.request.queryParameters["size"]?.toInt() ?: 20

                    if (userId != null) {
                        // 获取用户事件
                        val events = eventService.getUserEvents(userId, page, size)
                        val total = eventService.countEvents(EventFilter(userId = userId))
                        call.respond(HttpStatusCode.OK, EventListResponse(
                            events = events.map { EventResponse.fromEvent(it) },
                            total = total,
                            page = page,
                            size = size
                        ))
                    } else if (sessionId != null) {
                        // 获取会话事件
                        val events = eventService.getSessionEvents(sessionId, page, size)
                        val total = eventService.countEvents(EventFilter(sessionId = sessionId))
                        call.respond(HttpStatusCode.OK, EventListResponse(
                            events = events.map { EventResponse.fromEvent(it) },
                            total = total,
                            page = page,
                            size = size
                        ))
                    } else {
                        // 按过滤条件查询
                        val filter = EventFilter(
                            types = type?.let { listOf(it) },
                            names = name?.let { listOf(it) },
                            startTime = startTime,
                            endTime = endTime,
                            source = source
                        )
                        
                        val events = eventService.queryEvents(filter, page, size)
                        val total = eventService.countEvents(filter)
                        
                        call.respond(HttpStatusCode.OK, EventListResponse(
                            events = events.map { EventResponse.fromEvent(it) },
                            total = total,
                            page = page,
                            size = size
                        ))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to query events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 统计事件
            get("/count") {
                try {
                    val type = call.request.queryParameters["type"]
                    val name = call.request.queryParameters["name"]
                    val startTime = call.request.queryParameters["startTime"]?.let { Instant.parse(it) }
                    val endTime = call.request.queryParameters["endTime"]?.let { Instant.parse(it) }
                    val source = call.request.queryParameters["source"]
                    
                    val filter = EventFilter(
                        types = type?.let { listOf(it) },
                        names = name?.let { listOf(it) },
                        startTime = startTime,
                        endTime = endTime,
                        source = source
                    )
                    
                    val count = eventService.countEvents(filter)
                    
                    call.respond(HttpStatusCode.OK, EventCountResponse(
                        count = count,
                        type = type,
                        name = name,
                        startTime = startTime?.toString(),
                        endTime = endTime?.toString(),
                        source = source
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to count events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 获取事件趋势
            get("/trend") {
                try {
                    val type = call.request.queryParameters["type"] ?: throw IllegalArgumentException("Missing event type")
                    val name = call.request.queryParameters["name"]
                    val startTimeParam = call.request.queryParameters["startTime"]
                    val endTimeParam = call.request.queryParameters["endTime"]
                    val interval = call.request.queryParameters["interval"] ?: "day"
                    
                    val startTime = startTimeParam?.let { Instant.parse(it) } ?: Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS)
                    val endTime = endTimeParam?.let { Instant.parse(it) } ?: Instant.now()
                    
                    val trend = eventService.getEventTrend(type, name, startTime, endTime, interval)
                    call.respond(HttpStatusCode.OK, trend)
                } catch (e: Exception) {
                    logger.error("Failed to get event trend: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 获取热门事件
            get("/top") {
                try {
                    val type = call.request.queryParameters["type"] ?: throw IllegalArgumentException("Missing event type")
                    val startTimeParam = call.request.queryParameters["startTime"]
                    val endTimeParam = call.request.queryParameters["endTime"]
                    val limit = call.request.queryParameters["limit"]?.toInt() ?: 10
                    
                    val startTime = startTimeParam?.let { Instant.parse(it) } ?: Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS)
                    val endTime = endTimeParam?.let { Instant.parse(it) } ?: Instant.now()
                    
                    val topEvents = eventService.getTopEvents(type, startTime, endTime, limit)
                    call.respond(HttpStatusCode.OK, topEvents)
                } catch (e: Exception) {
                    logger.error("Failed to get top events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 聚合事件数据
            get("/aggregate") {
                try {
                    val type = call.request.queryParameters["type"]
                    val name = call.request.queryParameters["name"]
                    val startTimeParam = call.request.queryParameters["startTime"]
                    val endTimeParam = call.request.queryParameters["endTime"]
                    val groupByFields = call.request.queryParameters["groupBy"]?.split(",") ?: listOf("type")
                    val aggregateField = call.request.queryParameters["field"]
                    val aggregationType = call.request.queryParameters["aggregation"] ?: "COUNT"
                    
                    val startTime = startTimeParam?.let { Instant.parse(it) }
                    val endTime = endTimeParam?.let { Instant.parse(it) }
                    
                    val filter = EventFilter(
                        types = type?.let { listOf(it) },
                        names = name?.let { listOf(it) },
                        startTime = startTime,
                        endTime = endTime
                    )
                    
                    val result = eventService.aggregateEvents(filter, groupByFields, aggregateField, aggregationType)
                    call.respond(HttpStatusCode.OK, result)
                } catch (e: Exception) {
                    logger.error("Failed to aggregate events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 清理旧事件数据
            delete("/cleanup") {
                try {
                    val days = call.request.queryParameters["days"]?.toInt() ?: 90
                    val count = eventService.cleanupOldEvents(days)
                    call.respond(HttpStatusCode.OK, mapOf("deletedCount" to count))
                } catch (e: Exception) {
                    logger.error("Failed to cleanup old events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }
    
    private fun createEventFromRequest(request: RecordEventRequest): Event {
        return Event(
            type = request.type,
            name = request.name,
            userId = request.userId,
            sessionId = request.sessionId,
            properties = request.properties ?: emptyMap(),
            timestamp = request.timestamp ?: Instant.now(),
            appVersion = request.appVersion,
            deviceInfo = request.deviceInfo,
            source = request.source
        )
    }
} 