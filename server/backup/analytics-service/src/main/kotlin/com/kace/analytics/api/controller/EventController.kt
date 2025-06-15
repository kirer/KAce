package com.kace.analytics.api.controller

import com.kace.analytics.api.model.request.BatchCreateEventsRequest
import com.kace.analytics.api.model.request.CreateEventRequest
import com.kace.analytics.api.model.request.QueryEventsRequest
import com.kace.analytics.api.model.response.EventCountResponse
import com.kace.analytics.api.model.response.EventListResponse
import com.kace.analytics.api.model.response.EventResponse
import com.kace.analytics.domain.model.Event
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
            // 创建单个事件
            post {
                try {
                    val request = call.receive<CreateEventRequest>()
                    val event = createEventFromRequest(request)
                    val createdEvent = eventService.trackEvent(event)
                    call.respond(HttpStatusCode.Created, EventResponse.fromEvent(createdEvent))
                } catch (e: Exception) {
                    logger.error("Failed to create event: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 批量创建事件
            post("/batch") {
                try {
                    val request = call.receive<BatchCreateEventsRequest>()
                    val events = request.events.map { createEventFromRequest(it) }
                    val createdEvents = eventService.trackEvents(events)
                    call.respond(HttpStatusCode.Created, createdEvents.map { EventResponse.fromEvent(it) })
                } catch (e: Exception) {
                    logger.error("Failed to batch create events: ${e.message}", e)
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
                    val request = QueryEventsRequest(
                        type = call.request.queryParameters["type"],
                        startTime = call.request.queryParameters["startTime"],
                        endTime = call.request.queryParameters["endTime"],
                        userId = call.request.queryParameters["userId"],
                        sessionId = call.request.queryParameters["sessionId"],
                        limit = call.request.queryParameters["limit"]?.toInt() ?: 20,
                        offset = call.request.queryParameters["offset"]?.toInt() ?: 0
                    )
                    
                    val events = when {
                        request.userId != null -> {
                            eventService.getEventsByUserId(UUID.fromString(request.userId), request.limit, request.offset)
                        }
                        request.sessionId != null -> {
                            eventService.getEventsBySessionId(request.sessionId, request.limit, request.offset)
                        }
                        request.type != null && request.startTime != null && request.endTime != null -> {
                            val startTime = Instant.parse(request.startTime)
                            val endTime = Instant.parse(request.endTime)
                            eventService.getEventsByTimeRange(startTime, endTime, request.limit, request.offset)
                        }
                        request.type != null -> {
                            eventService.getEventsByType(request.type, request.limit, request.offset)
                        }
                        request.startTime != null && request.endTime != null -> {
                            val startTime = Instant.parse(request.startTime)
                            val endTime = Instant.parse(request.endTime)
                            eventService.getEventsByTimeRange(startTime, endTime, request.limit, request.offset)
                        }
                        else -> {
                            throw IllegalArgumentException("Invalid query parameters")
                        }
                    }
                    
                    val total = when {
                        request.type != null -> {
                            eventService.countEventsByType(request.type)
                        }
                        request.startTime != null && request.endTime != null -> {
                            val startTime = Instant.parse(request.startTime)
                            val endTime = Instant.parse(request.endTime)
                            eventService.countEventsByTimeRange(startTime, endTime)
                        }
                        else -> {
                            events.size.toLong()
                        }
                    }
                    
                    call.respond(HttpStatusCode.OK, EventListResponse(
                        events = events.map { EventResponse.fromEvent(it) },
                        total = total,
                        limit = request.limit,
                        offset = request.offset
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to query events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 统计事件
            get("/count") {
                try {
                    val type = call.request.queryParameters["type"]
                    val startTime = call.request.queryParameters["startTime"]
                    val endTime = call.request.queryParameters["endTime"]
                    
                    val count = when {
                        type != null -> {
                            eventService.countEventsByType(type)
                        }
                        startTime != null && endTime != null -> {
                            val start = Instant.parse(startTime)
                            val end = Instant.parse(endTime)
                            eventService.countEventsByTimeRange(start, end)
                        }
                        else -> {
                            throw IllegalArgumentException("Invalid count parameters")
                        }
                    }
                    
                    call.respond(HttpStatusCode.OK, EventCountResponse(
                        count = count,
                        type = type,
                        startTime = startTime,
                        endTime = endTime
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to count events: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 删除事件
            delete("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing event ID")
                    val success = eventService.deleteEvent(UUID.fromString(id))
                    if (success) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Event not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to delete event: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 删除时间范围内的事件
            delete {
                try {
                    val startTime = call.request.queryParameters["startTime"] ?: throw IllegalArgumentException("Missing startTime")
                    val endTime = call.request.queryParameters["endTime"] ?: throw IllegalArgumentException("Missing endTime")
                    
                    val start = Instant.parse(startTime)
                    val end = Instant.parse(endTime)
                    
                    val count = eventService.deleteEventsByTimeRange(start, end)
                    call.respond(HttpStatusCode.OK, mapOf("deletedCount" to count))
                } catch (e: Exception) {
                    logger.error("Failed to delete events by time range: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }
    
    /**
     * 从请求创建事件
     */
    private fun createEventFromRequest(request: CreateEventRequest): Event {
        return Event(
            id = UUID.randomUUID(),
            type = request.type,
            name = request.name,
            userId = request.userId?.let { UUID.fromString(it) },
            sessionId = request.sessionId,
            properties = request.properties,
            timestamp = request.timestamp?.let { Instant.parse(it) } ?: Instant.now(),
            appVersion = request.appVersion,
            deviceInfo = request.deviceInfo,
            source = request.source
        )
    }
} 