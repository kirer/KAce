package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.*
import com.kace.content.api.response.*
import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentVersion
import com.kace.content.domain.service.ContentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class ContentController {
    
    fun Route.contentRoutes() {
        val contentService by inject<ContentService>()
        
        route("/api/v1/contents") {
            // 创建内容
            post {
                val request = call.receive<CreateContentRequest>()
                val content = contentService.createContent(
                    title = request.title,
                    description = request.description,
                    body = request.body,
                    contentTypeId = request.contentTypeId,
                    authorId = request.authorId,
                    status = request.status,
                    metadata = request.metadata,
                    categoryIds = request.categoryIds,
                    tagIds = request.tagIds
                )
                call.respond(HttpStatusCode.Created, Response.success(content.toContentResponse()))
            }
            
            // 获取内容列表（分页）
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val contentTypeId = call.request.queryParameters["contentTypeId"]?.let { UUID.fromString(it) }
                val authorId = call.request.queryParameters["authorId"]?.let { UUID.fromString(it) }
                val status = call.request.queryParameters["status"]
                val categoryId = call.request.queryParameters["categoryId"]?.let { UUID.fromString(it) }
                val tagId = call.request.queryParameters["tagId"]?.let { UUID.fromString(it) }
                
                val pageRequest = PageRequest(page, size)
                val contents = contentService.findContents(
                    pageRequest = pageRequest,
                    contentTypeId = contentTypeId,
                    authorId = authorId,
                    status = status,
                    categoryId = categoryId,
                    tagId = tagId
                )
                
                val contentResponses = contents.items.map { it.toContentResponse() }
                val pageResponse = PageResponse(
                    items = contentResponses,
                    page = contents.page,
                    size = contents.size,
                    total = contents.total,
                    totalPages = contents.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 根据ID获取内容
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                
                val content = contentService.findContentById(id) 
                    ?: return@get call.respond(HttpStatusCode.NotFound, Response.error("Content not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(content.toContentResponse()))
            }
            
            // 更新内容
            put("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@put call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                
                val request = call.receive<UpdateContentRequest>()
                val content = contentService.updateContent(
                    id = id,
                    title = request.title,
                    description = request.description,
                    body = request.body,
                    status = request.status,
                    metadata = request.metadata,
                    categoryIds = request.categoryIds,
                    tagIds = request.tagIds
                ) ?: return@put call.respond(HttpStatusCode.NotFound, Response.error("Content not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(content.toContentResponse()))
            }
            
            // 删除内容
            delete("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                
                val success = contentService.deleteContent(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent, Response.success(null))
                } else {
                    call.respond(HttpStatusCode.NotFound, Response.error("Content not found"))
                }
            }
            
            // 内容版本相关操作
            route("/{id}/versions") {
                // 获取内容的所有版本
                get {
                    val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                        ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                    
                    val versions = contentService.findContentVersions(id)
                    call.respond(HttpStatusCode.OK, Response.success(versions.map { it.toContentVersionResponse() }))
                }
                
                // 获取特定版本
                get("/{versionId}") {
                    val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                        ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                    
                    val versionId = call.parameters["versionId"]?.let { UUID.fromString(it) } 
                        ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid version ID"))
                    
                    val version = contentService.findContentVersion(id, versionId)
                        ?: return@get call.respond(HttpStatusCode.NotFound, Response.error("Content version not found"))
                    
                    call.respond(HttpStatusCode.OK, Response.success(version.toContentVersionResponse()))
                }
                
                // 恢复到特定版本
                post("/{versionId}/restore") {
                    val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                        ?: return@post call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                    
                    val versionId = call.parameters["versionId"]?.let { UUID.fromString(it) } 
                        ?: return@post call.respond(HttpStatusCode.BadRequest, Response.error("Invalid version ID"))
                    
                    val content = contentService.restoreContentVersion(id, versionId)
                        ?: return@post call.respond(HttpStatusCode.NotFound, Response.error("Content or version not found"))
                    
                    call.respond(HttpStatusCode.OK, Response.success(content.toContentResponse()))
                }
            }
        }
    }
    
    private fun Content.toContentResponse(): ContentResponse {
        return ContentResponse(
            id = id,
            title = title,
            description = description,
            body = body,
            contentTypeId = contentTypeId,
            authorId = authorId,
            status = status,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt,
            publishedAt = publishedAt,
            version = version
        )
    }
    
    private fun ContentVersion.toContentVersionResponse(): ContentVersionResponse {
        return ContentVersionResponse(
            id = id,
            contentId = contentId,
            title = title,
            description = description,
            body = body,
            status = status,
            metadata = metadata,
            version = version,
            createdAt = createdAt,
            createdBy = createdBy
        )
    }
} 