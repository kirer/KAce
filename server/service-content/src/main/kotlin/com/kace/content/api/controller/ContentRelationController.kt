package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.CreateContentRelationRequest
import com.kace.content.api.response.ContentRelationResponse
import com.kace.content.domain.model.ContentRelation
import com.kace.content.domain.service.ContentRelationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class ContentRelationController {
    
    fun Route.contentRelationRoutes() {
        val contentRelationService by inject<ContentRelationService>()
        
        route("/api/v1/content-relations") {
            // 创建内容关联
            post {
                val request = call.receive<CreateContentRelationRequest>()
                val relation = contentRelationService.createContentRelation(
                    sourceId = request.sourceId,
                    targetId = request.targetId,
                    relationType = request.relationType,
                    metadata = request.metadata
                )
                call.respond(HttpStatusCode.Created, Response.success(relation.toContentRelationResponse()))
            }
            
            // 获取内容关联列表
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val sourceId = call.request.queryParameters["sourceId"]?.let { UUID.fromString(it) }
                val targetId = call.request.queryParameters["targetId"]?.let { UUID.fromString(it) }
                val relationType = call.request.queryParameters["relationType"]
                
                val pageRequest = PageRequest(page, size)
                val relations = contentRelationService.findContentRelations(
                    pageRequest = pageRequest,
                    sourceId = sourceId,
                    targetId = targetId,
                    relationType = relationType
                )
                
                val relationResponses = relations.items.map { it.toContentRelationResponse() }
                val pageResponse = PageResponse(
                    items = relationResponses,
                    page = relations.page,
                    size = relations.size,
                    total = relations.total,
                    totalPages = relations.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 根据ID获取内容关联
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid relation ID"))
                
                val relation = contentRelationService.findContentRelationById(id) 
                    ?: return@get call.respond(HttpStatusCode.NotFound, Response.error("Content relation not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(relation.toContentRelationResponse()))
            }
            
            // 删除内容关联
            delete("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, Response.error("Invalid relation ID"))
                
                val success = contentRelationService.deleteContentRelation(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent, Response.success(null))
                } else {
                    call.respond(HttpStatusCode.NotFound, Response.error("Content relation not found"))
                }
            }
            
            // 获取内容的相关内容
            get("/content/{contentId}/related") {
                val contentId = call.parameters["contentId"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val relationType = call.request.queryParameters["relationType"]
                
                val pageRequest = PageRequest(page, size)
                val relatedContents = contentRelationService.findRelatedContents(
                    contentId = contentId,
                    relationType = relationType,
                    pageRequest = pageRequest
                )
                
                call.respond(HttpStatusCode.OK, Response.success(relatedContents))
            }
        }
    }
    
    private fun ContentRelation.toContentRelationResponse(): ContentRelationResponse {
        return ContentRelationResponse(
            id = id,
            sourceId = sourceId,
            targetId = targetId,
            relationType = relationType,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 