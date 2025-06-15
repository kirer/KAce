package com.kace.content.api.routes

import com.kace.content.api.request.CreateTagRequest
import com.kace.content.api.request.UpdateTagRequest
import com.kace.content.api.response.TagListResponse
import com.kace.content.api.response.TagResponse
import com.kace.content.domain.service.TagService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 标签路由
 */
fun Route.tagRoutes(tagService: TagService) {
    route("/tags") {
        // 创建标签
        post {
            val request = call.receive<CreateTagRequest>()
            val userId = call.principal<UserPrincipal>()?.id ?: throw IllegalArgumentException("User not authenticated")
            
            val tag = tagService.createTag(
                name = request.name,
                slug = request.slug,
                createdBy = UUID.fromString(userId)
            )
            
            val response = TagResponse(
                id = tag.id.toString(),
                name = tag.name,
                slug = tag.slug,
                createdAt = tag.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                updatedAt = tag.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                createdBy = tag.createdBy.toString()
            )
            
            call.respond(HttpStatusCode.Created, response)
        }
        
        // 获取标签列表
        get {
            val page = call.parameters["page"]?.toIntOrNull() ?: 1
            val size = call.parameters["size"]?.toIntOrNull() ?: 10
            
            val tags = tagService.getAllTags(page, size)
            val total = tagService.countTags()
            
            val items = tags.map { tag ->
                TagResponse(
                    id = tag.id.toString(),
                    name = tag.name,
                    slug = tag.slug,
                    createdAt = tag.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    updatedAt = tag.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                    createdBy = tag.createdBy.toString()
                )
            }
            
            call.respond(TagListResponse(items, total, page, size))
        }
        
        route("/{id}") {
            // 获取单个标签
            get {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val tag = tagService.getTagById(UUID.fromString(id))
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Tag not found"))
                
                val response = TagResponse(
                    id = tag.id.toString(),
                    name = tag.name,
                    slug = tag.slug,
                    createdAt = tag.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    updatedAt = tag.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                    createdBy = tag.createdBy.toString()
                )
                
                call.respond(response)
            }
            
            // 更新标签
            put {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<UpdateTagRequest>()
                
                val tag = tagService.updateTag(
                    id = UUID.fromString(id),
                    name = request.name,
                    slug = request.slug
                )
                
                val response = TagResponse(
                    id = tag.id.toString(),
                    name = tag.name,
                    slug = tag.slug,
                    createdAt = tag.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    updatedAt = tag.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                    createdBy = tag.createdBy.toString()
                )
                
                call.respond(response)
            }
            
            // 删除标签
            delete {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val result = tagService.deleteTag(UUID.fromString(id))
                
                if (result) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Tag not found"))
                }
            }
        }
        
        // 根据slug获取标签
        get("/by-slug/{slug}") {
            val slug = call.parameters["slug"] ?: throw IllegalArgumentException("Missing slug parameter")
            
            val tag = tagService.getTagBySlug(slug)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Tag not found"))
            
            val response = TagResponse(
                id = tag.id.toString(),
                name = tag.name,
                slug = tag.slug,
                createdAt = tag.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                updatedAt = tag.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                createdBy = tag.createdBy.toString()
            )
            
            call.respond(response)
        }
        
        // 根据名称获取标签
        get("/by-name/{name}") {
            val name = call.parameters["name"] ?: throw IllegalArgumentException("Missing name parameter")
            
            val tag = tagService.getTagByName(name)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Tag not found"))
            
            val response = TagResponse(
                id = tag.id.toString(),
                name = tag.name,
                slug = tag.slug,
                createdAt = tag.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                updatedAt = tag.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                createdBy = tag.createdBy.toString()
            )
            
            call.respond(response)
        }
    }
} 