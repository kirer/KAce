package com.kace.media.api.routes

import com.kace.media.api.request.CreateFolderRequest
import com.kace.media.api.request.FolderQueryRequest
import com.kace.media.api.request.MoveFolderRequest
import com.kace.media.api.request.UpdateFolderRequest
import com.kace.media.api.response.MediaPathResponse
import com.kace.media.api.response.error
import com.kace.media.api.response.paged
import com.kace.media.api.response.success
import com.kace.media.api.response.successMessage
import com.kace.media.api.response.toResponse
import com.kace.media.domain.service.MediaFolderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * 配置媒体文件夹路由
 */
fun Route.configureMediaFolderRoutes(folderService: MediaFolderService) {
    route("/folders") {
        // 获取根文件夹（分页）
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            
            val folders = folderService.getRootFolders(page, size)
            val total = folderService.countSubFolders(null)
            
            call.respond(paged(folders.map { it.toResponse() }, page, size, total))
        }
        
        // 根据ID获取文件夹
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid folder ID")
            )
            
            val folder = folderService.getFolderById(UUID.fromString(id)) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                error("folder_not_found", "Folder not found")
            )
            
            call.respond(success(folder.toResponse()))
        }
        
        // 创建文件夹
        post {
            val request = call.receive<CreateFolderRequest>()
            
            val parentId = request.parentId?.let { UUID.fromString(it) }
            val createdBy = UUID.randomUUID() // 在实际应用中，从JWT中获取用户ID
            
            val folder = folderService.createFolder(
                request.name,
                request.description,
                parentId,
                createdBy
            )
            
            call.respond(HttpStatusCode.Created, success(folder.toResponse()))
        }
        
        // 更新文件夹
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid folder ID")
            )
            
            val request = call.receive<UpdateFolderRequest>()
            
            val folder = folderService.updateFolder(
                UUID.fromString(id),
                request.name,
                request.description
            ) ?: return@put call.respond(
                HttpStatusCode.NotFound,
                error("folder_not_found", "Folder not found")
            )
            
            call.respond(success(folder.toResponse()))
        }
        
        // 删除文件夹
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid folder ID")
            )
            
            val recursive = call.request.queryParameters["recursive"]?.toBoolean() ?: false
            
            val deleted = folderService.deleteFolder(UUID.fromString(id), recursive)
            
            if (deleted) {
                call.respond(successMessage("Folder deleted successfully"))
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    error("folder_not_found", "Folder not found or not empty")
                )
            }
        }
        
        // 获取子文件夹（分页）
        get("/{id}/children") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid folder ID")
            )
            
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            
            val folders = folderService.getSubFolders(UUID.fromString(id), page, size)
            val total = folderService.countSubFolders(UUID.fromString(id))
            
            call.respond(paged(folders.map { it.toResponse() }, page, size, total))
        }
        
        // 获取文件夹路径
        get("/{id}/path") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid folder ID")
            )
            
            val path = folderService.getFolderPath(UUID.fromString(id))
            
            call.respond(success(MediaPathResponse(path.map { it.toResponse() })))
        }
        
        // 检查文件夹是否为空
        get("/{id}/empty") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid folder ID")
            )
            
            val isEmpty = folderService.isFolderEmpty(UUID.fromString(id))
            
            call.respond(success(mapOf("empty" to isEmpty)))
        }
        
        // 移动文件夹
        put("/{id}/move") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                error("invalid_id", "Invalid folder ID")
            )
            
            val request = call.receive<MoveFolderRequest>()
            
            val newParentId = request.newParentId?.let { UUID.fromString(it) }
            
            val folder = folderService.moveFolder(UUID.fromString(id), newParentId) ?: return@put call.respond(
                HttpStatusCode.NotFound,
                error("folder_not_found", "Folder not found")
            )
            
            call.respond(success(folder.toResponse()))
        }
    }
} 