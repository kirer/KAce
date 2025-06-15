package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.ContentSearchRequest
import com.kace.content.domain.service.ContentSearchService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class ContentSearchController {
    
    fun Route.contentSearchRoutes() {
        val contentSearchService by inject<ContentSearchService>()
        
        route("/api/v1/content-search") {
            // 基本搜索
            get {
                val query = call.request.queryParameters["query"] ?: ""
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val contentTypeId = call.request.queryParameters["contentTypeId"]?.let { UUID.fromString(it) }
                
                val pageRequest = PageRequest(page, size)
                val searchResults = contentSearchService.searchContent(
                    query = query,
                    pageRequest = pageRequest,
                    contentTypeId = contentTypeId
                )
                
                call.respond(HttpStatusCode.OK, Response.success(searchResults))
            }
            
            // 高级搜索
            post {
                val request = call.receive<ContentSearchRequest>()
                val pageRequest = PageRequest(request.page ?: 0, request.size ?: 20)
                
                val searchResults = contentSearchService.advancedSearch(
                    query = request.query,
                    pageRequest = pageRequest,
                    contentTypeId = request.contentTypeId,
                    authorId = request.authorId,
                    categoryId = request.categoryId,
                    tagId = request.tagId,
                    status = request.status,
                    fromDate = request.fromDate,
                    toDate = request.toDate,
                    sortBy = request.sortBy,
                    sortDirection = request.sortDirection
                )
                
                call.respond(HttpStatusCode.OK, Response.success(searchResults))
            }
            
            // 相似内容搜索
            get("/similar/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid content ID"))
                
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 5
                
                val similarContents = contentSearchService.findSimilarContent(
                    contentId = id,
                    limit = limit
                )
                
                call.respond(HttpStatusCode.OK, Response.success(similarContents))
            }
            
            // 全文搜索
            get("/fulltext") {
                val query = call.request.queryParameters["query"] ?: ""
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val fields = call.request.queryParameters.getAll("field")?.toList() ?: listOf("title", "body", "description")
                
                val pageRequest = PageRequest(page, size)
                val searchResults = contentSearchService.fulltextSearch(
                    query = query,
                    fields = fields,
                    pageRequest = pageRequest
                )
                
                call.respond(HttpStatusCode.OK, Response.success(searchResults))
            }
            
            // 分面搜索（获取搜索结果的分类、标签等聚合信息）
            get("/facets") {
                val query = call.request.queryParameters["query"] ?: ""
                
                val facets = contentSearchService.getSearchFacets(query)
                
                call.respond(HttpStatusCode.OK, Response.success(facets))
            }
            
            // 自动补全
            get("/autocomplete") {
                val prefix = call.request.queryParameters["prefix"] ?: ""
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                
                val suggestions = contentSearchService.getAutocompleteSuggestions(
                    prefix = prefix,
                    limit = limit
                )
                
                call.respond(HttpStatusCode.OK, Response.success(suggestions))
            }
        }
    }
} 