package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.CreateTagRequest
import com.kace.content.api.request.UpdateTagRequest
import com.kace.content.api.response.TagResponse
import com.kace.content.domain.model.Tag
import com.kace.content.domain.service.TagService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class TagController {
    
    fun Route.tagRoutes() {
        val tagService by inject<TagService>()
        
        route("/api/v1/tags") {
            // 创建标签
            post {
                val request = call.receive<CreateTagRequest>()
                val tag = tagService.createTag(
                    name = request.name,
                    description = request.description
                )
                call.respond(HttpStatusCode.Created, Response.success(tag.toTagResponse()))
            }
            
            // 获取标签列表（分页）
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val pageRequest = PageRequest(page, size)
                val tags = tagService.findTags(pageRequest)
                
                val tagResponses = tags.items.map { it.toTagResponse() }
                val pageResponse = PageResponse(
                    items = tagResponses,
                    page = tags.page,
                    size = tags.size,
                    total = tags.total,
                    totalPages = tags.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 根据ID获取标签
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid tag ID"))
                
                val tag = tagService.findTagById(id) 
                    ?: return@get call.respond(HttpStatusCode.NotFound, Response.error("Tag not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(tag.toTagResponse()))
            }
            
            // 更新标签
            put("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@put call.respond(HttpStatusCode.BadRequest, Response.error("Invalid tag ID"))
                
                val request = call.receive<UpdateTagRequest>()
                val tag = tagService.updateTag(
                    id = id,
                    name = request.name,
                    description = request.description
                ) ?: return@put call.respond(HttpStatusCode.NotFound, Response.error("Tag not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(tag.toTagResponse()))
            }
            
            // 删除标签
            delete("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, Response.error("Invalid tag ID"))
                
                val success = tagService.deleteTag(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent, Response.success(null))
                } else {
                    call.respond(HttpStatusCode.NotFound, Response.error("Tag not found"))
                }
            }
            
            // 获取标签下的内容
            get("/{id}/contents") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid tag ID"))
                
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val pageRequest = PageRequest(page, size)
                val contents = tagService.findContentsByTag(id, pageRequest)
                
                call.respond(HttpStatusCode.OK, Response.success(contents))
            }
        }
    }
    
    private fun Tag.toTagResponse(): TagResponse {
        return TagResponse(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 