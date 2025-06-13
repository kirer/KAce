package com.kace.analytics.api.controller

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.service.EventService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

/**
 * 事件控制器
 */
class EventController(private val eventService: EventService) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 跟踪单个事件
     */
    suspend fun trackEvent(call: ApplicationCall) {
        try {
            val event = call.receive<Event>()
            val result = eventService.trackEvent(event)
            call.respond(HttpStatusCode.Created, result)
        } catch (e: Exception) {
            logger.error("Failed to track event: ${e.message}", e)
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }
    
    /**
     * 批量跟踪事件
     */
    suspend fun trackEvents(call: ApplicationCall) {
        try {
            val events = call.receive<List<Event>>()
            val results = eventService.trackEvents(events)
            call.respond(HttpStatusCode.Created, results)
        } catch (e: Exception) {
            logger.error("Failed to track events: ${e.message}", e)
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }
    
    /**
     * 获取单个事件
     */
    suspend fun getEvent(call: ApplicationCall) {
        try {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing event ID")
            val event = eventService.getEvent(id) ?: return call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, event)
        } catch (e: Exception) {
            logger.error("Failed to get event: ${e.message}", e)
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }
    
    /**
     * 查询事件列表
     */
    suspend fun getEvents(call: ApplicationCall) {
        try {
            val params = call.request.queryParameters
            
            val type = params["type"]
            val name = params["name"]
            val userId = params["userId"]
            val sessionId = params["sessionId"]
            val startTime = params["startTime"]?.let { Instant.parse(it) }
            val endTime = params["endTime"]?.let { Instant.parse(it) }
            val limit = params["limit"]?.toIntOrNull() ?: 100
            val offset = params["offset"]?.toIntOrNull() ?: 0
            
            val events = eventService.getEvents(
                type = type,
                name = name,
                userId = userId,
                sessionId = sessionId,
                startTime = startTime,
                endTime = endTime,
                limit = limit,
                offset = offset
            )
            
            call.respond(HttpStatusCode.OK, events)
        } catch (e: Exception) {
            logger.error("Failed to get events: ${e.message}", e)
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
        }
    }
} 