package com.kace.content.api.routes

import com.kace.content.api.response.ContentListResponse
import com.kace.content.api.response.ContentResponse
import com.kace.content.domain.service.ContentSearchService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.format.DateTimeFormatter

/**
 * 搜索路由
 */
fun Route.searchRoutes(contentSearchService: ContentSearchService) {
    route("/search") {
        // 搜索内容
        get {
            val query = call.parameters["q"] ?: return@get call.respond(
                ContentListResponse(emptyList(), 0, 1, 0)
            )
            val page = call.parameters["page"]?.toIntOrNull() ?: 1
            val size = call.parameters["size"]?.toIntOrNull() ?: 10
            val contentTypeId = call.parameters["contentTypeId"]
            
            val searchResult = contentSearchService.search(query, contentTypeId, page, size)
            
            val items = searchResult.contents.map { content ->
                ContentResponse(
                    id = content.id.toString(),
                    contentTypeId = content.contentTypeId.toString(),
                    title = content.title,
                    slug = content.slug,
                    status = content.status.name,
                    createdBy = content.createdBy.toString(),
                    createdAt = content.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    updatedAt = content.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                    publishedAt = content.publishedAt?.format(DateTimeFormatter.ISO_INSTANT),
                    version = content.version,
                    languageCode = content.languageCode,
                    fields = content.fields
                )
            }
            
            call.respond(ContentListResponse(items, searchResult.total, page, size))
        }
    }
} 