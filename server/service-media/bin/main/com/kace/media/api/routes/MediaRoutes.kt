package com.kace.media.api.routes

import com.kace.media.api.request.AddMediaTagRequest
import com.kace.media.api.request.BatchDeleteMediaRequest
import com.kace.media.api.request.BatchUpdateMediaRequest
import com.kace.media.api.request.CreateMediaRequest
import com.kace.media.api.request.ImageProcessingRequest
import com.kace.media.api.request.MediaSearchRequest
import com.kace.media.api.request.UpdateMediaRequest
import com.kace.media.api.response.error
import com.kace.media.api.response.paged
import com.kace.media.api.response.success
import com.kace.media.api.response.successMessage
import com.kace.media.api.response.toResponse
import com.kace.media.domain.model.MediaType
import com.kace.media.domain.service.MediaService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.UUID

/**
 * 配置媒体路由
 */
fun Application.configureMediaRoutes() {
    val mediaService: MediaService by inject()
    
    routing {
        route("/api/v1/media") {
            // 获取所有媒体（分页）
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val media = mediaService.getAllMedia(page, size)
                val total = mediaService.countMedia()
                
                call.respond(paged(media.map { it.toResponse() }, page, size, total))
            }
            
            // 根据ID获取媒体
            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_id", "Invalid media ID")
                )
                
                val media = mediaService.getMediaById(UUID.fromString(id)) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    error("media_not_found", "Media not found")
                )
                
                call.respond(success(media.toResponse()))
            }
            
            // 上传媒体文件
            post {
                val multipart = call.receiveMultipart()
                var fileName = ""
                var contentType = ""
                var description: String? = null
                var folderId: UUID? = null
                var tags = listOf<String>()
                var fileBytes: ByteArray? = null
                
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "description" -> description = part.value
                                "folderId" -> folderId = part.value.takeIf { it.isNotBlank() }?.let { UUID.fromString(it) }
                                "tags" -> tags = part.value.split(",").filter { it.isNotBlank() }
                            }
                        }
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "unnamed"
                            contentType = part.contentType?.toString() ?: "application/octet-stream"
                            fileBytes = part.streamProvider().readBytes()
                        }
                        else -> {}
                    }
                    part.dispose()
                }
                
                if (fileBytes == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        error("no_file", "No file was uploaded")
                    )
                    return@post
                }
                
                val createdBy = UUID.randomUUID() // 在实际应用中，从JWT中获取用户ID
                
                val media = mediaService.uploadMedia(
                    fileBytes!!.inputStream(),
                    fileName,
                    contentType,
                    description,
                    folderId,
                    tags,
                    createdBy
                )
                
                call.respond(HttpStatusCode.Created, success(media.toResponse()))
            }
            
            // 更新媒体信息
            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_id", "Invalid media ID")
                )
                
                val request = call.receive<UpdateMediaRequest>()
                
                val folderId = request.folderId?.let { UUID.fromString(it) }
                
                val media = mediaService.updateMedia(
                    UUID.fromString(id),
                    request.name,
                    request.description,
                    folderId,
                    request.tags
                ) ?: return@put call.respond(
                    HttpStatusCode.NotFound,
                    error("media_not_found", "Media not found")
                )
                
                call.respond(success(media.toResponse()))
            }
            
            // 删除媒体
            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_id", "Invalid media ID")
                )
                
                val deleted = mediaService.deleteMedia(UUID.fromString(id))
                
                if (deleted) {
                    call.respond(successMessage("Media deleted successfully"))
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        error("media_not_found", "Media not found")
                    )
                }
            }
            
            // 获取媒体文件
            get("/{id}/file") {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_id", "Invalid media ID")
                )
                
                val media = mediaService.getMediaById(UUID.fromString(id)) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    error("media_not_found", "Media not found")
                )
                
                val inputStream = mediaService.getMediaFile(UUID.fromString(id))
                
                call.respondOutputStream(ContentType.parse(media.mimeType)) {
                    inputStream.copyTo(this)
                }
            }
            
            // 获取媒体缩略图
            get("/{id}/thumbnail") {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_id", "Invalid media ID")
                )
                
                val media = mediaService.getMediaById(UUID.fromString(id)) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    error("media_not_found", "Media not found")
                )
                
                val thumbnailStream = mediaService.getMediaThumbnail(UUID.fromString(id)) ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    error("thumbnail_not_found", "Thumbnail not found")
                )
                
                call.respondOutputStream(ContentType.Image.JPEG) {
                    thumbnailStream.copyTo(this)
                }
            }
            
            // 添加标签到媒体
            post("/{id}/tags") {
                val id = call.parameters["id"] ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_id", "Invalid media ID")
                )
                
                val request = call.receive<AddMediaTagRequest>()
                
                val added = mediaService.addTagToMedia(UUID.fromString(id), request.tag)
                
                if (added) {
                    call.respond(successMessage("Tag added successfully"))
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        error("media_not_found", "Media not found")
                    )
                }
            }
            
            // 从媒体中移除标签
            delete("/{id}/tags/{tag}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_id", "Invalid media ID")
                )
                
                val tag = call.parameters["tag"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_tag", "Invalid tag")
                )
                
                val removed = mediaService.removeTagFromMedia(UUID.fromString(id), tag)
                
                if (removed) {
                    call.respond(successMessage("Tag removed successfully"))
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        error("not_found", "Media or tag not found")
                    )
                }
            }
            
            // 搜索媒体
            post("/search") {
                val request = call.receive<MediaSearchRequest>()
                
                val page = request.page
                val size = request.size
                
                val media = mediaService.searchMedia(request.query ?: "", page, size)
                val total = mediaService.countMedia()
                
                call.respond(paged(media.map { it.toResponse() }, page, size, total))
            }
            
            // 根据文件夹获取媒体
            get("/folder/{folderId}") {
                val folderId = call.parameters["folderId"]?.let { UUID.fromString(it) }
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val media = mediaService.getMediaByFolder(folderId, page, size)
                val total = mediaService.countMediaByFolder(folderId)
                
                call.respond(paged(media.map { it.toResponse() }, page, size, total))
            }
            
            // 根据类型获取媒体
            get("/type/{type}") {
                val typeStr = call.parameters["type"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_type", "Invalid media type")
                )
                
                val type = try {
                    MediaType.valueOf(typeStr.uppercase())
                } catch (e: IllegalArgumentException) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        error("invalid_type", "Invalid media type")
                    )
                }
                
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val media = mediaService.getMediaByType(type, page, size)
                val total = mediaService.countMediaByType(type)
                
                call.respond(paged(media.map { it.toResponse() }, page, size, total))
            }
            
            // 根据标签获取媒体
            get("/tag/{tag}") {
                val tag = call.parameters["tag"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    error("invalid_tag", "Invalid tag")
                )
                
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val media = mediaService.getMediaByTag(tag, page, size)
                val total = mediaService.countMedia() // 这里应该有一个countByTag方法，暂时使用总数
                
                call.respond(paged(media.map { it.toResponse() }, page, size, total))
            }
            
            // 批量更新媒体
            put("/batch") {
                val request = call.receive<BatchUpdateMediaRequest>()
                
                val ids = request.ids.map { UUID.fromString(it) }
                val folderId = request.update.folderId?.let { UUID.fromString(it) }
                
                val updatedMedia = mutableListOf<UUID>()
                
                ids.forEach { id ->
                    try {
                        mediaService.updateMedia(
                            id,
                            request.update.name,
                            request.update.description,
                            folderId,
                            request.update.tags
                        )?.let {
                            updatedMedia.add(id)
                        }
                    } catch (e: Exception) {
                        // 记录错误但继续处理
                    }
                }
                
                call.respond(success(mapOf("updatedCount" to updatedMedia.size, "updatedIds" to updatedMedia.map { it.toString() })))
            }
            
            // 批量删除媒体
            delete("/batch") {
                val request = call.receive<BatchDeleteMediaRequest>()
                
                val ids = request.ids.map { UUID.fromString(it) }
                val deletedMedia = mutableListOf<UUID>()
                
                ids.forEach { id ->
                    try {
                        if (mediaService.deleteMedia(id)) {
                            deletedMedia.add(id)
                        }
                    } catch (e: Exception) {
                        // 记录错误但继续处理
                    }
                }
                
                call.respond(success(mapOf("deletedCount" to deletedMedia.size, "deletedIds" to deletedMedia.map { it.toString() })))
            }
        }
    }
} 