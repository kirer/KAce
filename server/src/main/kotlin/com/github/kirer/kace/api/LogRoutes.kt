package com.github.kirer.kace.api

import com.github.kirer.kace.log.LogQueryCriteria
import com.github.kirer.kace.log.LogService
import com.github.kirer.kace.log.LogType
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
 * 日志查询请求DTO
 */
@Serializable
data class LogQueryRequest(
    val types: List<String>? = null,
    val levels: List<String>? = null,
    val loggers: List<String>? = null,
    val fromTime: String? = null,
    val toTime: String? = null,
    val messageContains: String? = null,
    val limit: Int = 100,
    val offset: Int = 0
)

/**
 * 日志条目响应DTO
 */
@Serializable
data class LogEntryResponse(
    val timestamp: String,
    val type: String,
    val level: String,
    val logger: String,
    val message: String,
    val context: Map<String, String>,
    val exception: String?
)

/**
 * 日志API路由
 */
fun Route.logRoutes() {
    val logService by inject<LogService>()
    val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    
    route("/api/logs") {
        // 查询日志
        post {
            val request = call.receive<LogQueryRequest>()
            
            val types = request.types?.mapNotNull { 
                try {
                    LogType.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }?.toSet()
            
            val fromTime = request.fromTime?.let {
                try {
                    LocalDateTime.parse(it, dateTimeFormatter)
                } catch (e: Exception) {
                    null
                }
            }
            
            val toTime = request.toTime?.let {
                try {
                    LocalDateTime.parse(it, dateTimeFormatter)
                } catch (e: Exception) {
                    null
                }
            }
            
            val criteria = LogQueryCriteria(
                types = types,
                levels = request.levels?.toSet(),
                loggers = request.loggers?.toSet(),
                fromTime = fromTime,
                toTime = toTime,
                messageContains = request.messageContains,
                limit = request.limit.coerceIn(1, 1000),
                offset = request.offset.coerceAtLeast(0)
            )
            
            val logs = logService.queryLogs(criteria)
            val response = logs.map { entry ->
                LogEntryResponse(
                    timestamp = entry.timestamp.format(dateTimeFormatter),
                    type = entry.type.name,
                    level = entry.level,
                    logger = entry.logger,
                    message = entry.message,
                    context = entry.context.mapValues { it.value.toString() },
                    exception = entry.exception
                )
            }
            
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        // 获取系统日志
        get("/system") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 1000) ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
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
            
            val logs = logService.getSystemLogs(fromTime, toTime, limit, offset)
            val response = logs.map { entry ->
                LogEntryResponse(
                    timestamp = entry.timestamp.format(dateTimeFormatter),
                    type = entry.type.name,
                    level = entry.level,
                    logger = entry.logger,
                    message = entry.message,
                    context = entry.context.mapValues { it.value.toString() },
                    exception = entry.exception
                )
            }
            
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        // 获取业务日志
        get("/business") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 1000) ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
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
            
            val logs = logService.getBusinessLogs(fromTime, toTime, limit, offset)
            val response = logs.map { entry ->
                LogEntryResponse(
                    timestamp = entry.timestamp.format(dateTimeFormatter),
                    type = entry.type.name,
                    level = entry.level,
                    logger = entry.logger,
                    message = entry.message,
                    context = entry.context.mapValues { it.value.toString() },
                    exception = entry.exception
                )
            }
            
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        // 获取安全日志
        get("/security") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 1000) ?: 100
            val offset = call.request.queryParameters["offset"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
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
            
            val logs = logService.getSecurityLogs(fromTime, toTime, limit, offset)
            val response = logs.map { entry ->
                LogEntryResponse(
                    timestamp = entry.timestamp.format(dateTimeFormatter),
                    type = entry.type.name,
                    level = entry.level,
                    logger = entry.logger,
                    message = entry.message,
                    context = entry.context.mapValues { it.value.toString() },
                    exception = entry.exception
                )
            }
            
            call.respond(mapOf("success" to true, "data" to response))
        }
        
        // 清理日志
        delete {
            val days = call.request.queryParameters["days"]?.toIntOrNull() ?: 30
            val count = logService.cleanupLogs(days)
            call.respond(mapOf("success" to true, "data" to mapOf("deletedCount" to count)))
        }
    }
} 