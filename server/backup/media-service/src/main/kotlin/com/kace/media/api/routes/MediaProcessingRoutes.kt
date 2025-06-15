package com.kace.media.api.routes

import com.kace.media.api.request.ImageProcessingRequest
import com.kace.media.api.response.error
import com.kace.media.api.response.paged
import com.kace.media.api.response.success
import com.kace.media.api.response.successMessage
import com.kace.media.api.response.toResponse
import com.kace.media.domain.model.MediaProcessingTaskStatus
import com.kace.media.domain.model.MediaProcessingTaskType
import com.kace.media.domain.service.MediaProcessingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * 配置媒体处理路由
 */
fun Route.configureMediaProcessingRoutes(processingService: MediaProcessingService) {
    route("/processing") {
        // 获取媒体的所有处理任务
        get("/media/{mediaId}") {
            val mediaId = call.parameters["mediaId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid media ID")
            )
            
            val tasks = processingService.getTasksByMediaId(UUID.fromString(mediaId))
            
            call.respond(success(tasks.map { it.toResponse() }))
        }
        
        // 获取特定任务
        get("/tasks/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid task ID")
            )
            
            val task = processingService.getTaskById(UUID.fromString(id)) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                error("task_not_found", "Task not found")
            )
            
            call.respond(success(task.toResponse()))
        }
        
        // 获取所有处理任务（分页）
        get("/tasks") {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            val status = call.request.queryParameters["status"]?.let {
                try {
                    MediaProcessingTaskStatus.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            
            val tasks = if (status != null) {
                processingService.getPendingTasks(size)
            } else {
                processingService.getPendingTasks(size)
            }
            
            val total = if (status != null) {
                processingService.countTasksByStatus(status)
            } else {
                tasks.size // 简化，实际应该有一个count方法
            }
            
            call.respond(paged(tasks.map { it.toResponse() }, page, size, total))
        }
        
        // 创建缩略图生成任务
        post("/media/{mediaId}/thumbnail") {
            val mediaId = call.parameters["mediaId"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid media ID")
            )
            
            val request = call.receive<ImageProcessingRequest>()
            
            val parameters = mutableMapOf<String, Any>()
            request.width?.let { parameters["width"] = it }
            request.height?.let { parameters["height"] = it }
            parameters["quality"] = request.quality
            
            val task = processingService.createTask(
                UUID.fromString(mediaId),
                MediaProcessingTaskType.THUMBNAIL_GENERATION,
                parameters
            )
            
            call.respond(HttpStatusCode.Created, success(task.toResponse()))
        }
        
        // 创建图片调整大小任务
        post("/media/{mediaId}/resize") {
            val mediaId = call.parameters["mediaId"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid media ID")
            )
            
            val request = call.receive<ImageProcessingRequest>()
            
            if (request.width == null && request.height == null) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_request", "Width or height must be provided")
                )
            }
            
            val parameters = mutableMapOf<String, Any>()
            request.width?.let { parameters["width"] = it }
            request.height?.let { parameters["height"] = it }
            parameters["crop"] = request.crop
            parameters["quality"] = request.quality
            request.format?.let { parameters["format"] = it }
            
            val task = processingService.createTask(
                UUID.fromString(mediaId),
                MediaProcessingTaskType.IMAGE_RESIZE,
                parameters
            )
            
            call.respond(HttpStatusCode.Created, success(task.toResponse()))
        }
        
        // 创建元数据提取任务
        post("/media/{mediaId}/metadata") {
            val mediaId = call.parameters["mediaId"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid media ID")
            )
            
            val task = processingService.createTask(
                UUID.fromString(mediaId),
                MediaProcessingTaskType.METADATA_EXTRACTION
            )
            
            call.respond(HttpStatusCode.Created, success(task.toResponse()))
        }
        
        // 执行任务
        post("/tasks/{id}/execute") {
            val id = call.parameters["id"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid task ID")
            )
            
            val success = processingService.executeTask(UUID.fromString(id))
            
            if (success) {
                call.respond(successMessage("Task execution started"))
            } else {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    error("execution_failed", "Failed to start task execution")
                )
            }
        }
        
        // 取消任务
        delete("/tasks/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid task ID")
            )
            
            val success = processingService.updateTaskStatus(
                UUID.fromString(id),
                MediaProcessingTaskStatus.FAILED,
                "Task cancelled by user"
            )
            
            if (success) {
                call.respond(successMessage("Task cancelled successfully"))
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    error("task_not_found", "Task not found")
                )
            }
        }
    }
} 