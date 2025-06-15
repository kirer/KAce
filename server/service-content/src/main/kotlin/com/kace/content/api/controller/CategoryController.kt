package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.CreateCategoryRequest
import com.kace.content.api.request.UpdateCategoryRequest
import com.kace.content.api.response.CategoryResponse
import com.kace.content.domain.model.Category
import com.kace.content.domain.service.CategoryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class CategoryController {
    
    fun Route.categoryRoutes() {
        val categoryService by inject<CategoryService>()
        
        route("/api/v1/categories") {
            // 创建分类
            post {
                val request = call.receive<CreateCategoryRequest>()
                val category = categoryService.createCategory(
                    name = request.name,
                    description = request.description,
                    parentId = request.parentId,
                    metadata = request.metadata
                )
                call.respond(HttpStatusCode.Created, Response.success(category.toCategoryResponse()))
            }
            
            // 获取分类列表（分页）
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val parentId = call.request.queryParameters["parentId"]?.let { UUID.fromString(it) }
                
                val pageRequest = PageRequest(page, size)
                val categories = categoryService.findCategories(pageRequest, parentId)
                
                val categoryResponses = categories.items.map { it.toCategoryResponse() }
                val pageResponse = PageResponse(
                    items = categoryResponses,
                    page = categories.page,
                    size = categories.size,
                    total = categories.total,
                    totalPages = categories.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 获取分类树
            get("/tree") {
                val categories = categoryService.getCategoryTree()
                call.respond(HttpStatusCode.OK, Response.success(categories.map { it.toCategoryResponse() }))
            }
            
            // 根据ID获取分类
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid category ID"))
                
                val category = categoryService.findCategoryById(id) 
                    ?: return@get call.respond(HttpStatusCode.NotFound, Response.error("Category not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(category.toCategoryResponse()))
            }
            
            // 更新分类
            put("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@put call.respond(HttpStatusCode.BadRequest, Response.error("Invalid category ID"))
                
                val request = call.receive<UpdateCategoryRequest>()
                val category = categoryService.updateCategory(
                    id = id,
                    name = request.name,
                    description = request.description,
                    parentId = request.parentId,
                    metadata = request.metadata
                ) ?: return@put call.respond(HttpStatusCode.NotFound, Response.error("Category not found"))
                
                call.respond(HttpStatusCode.OK, Response.success(category.toCategoryResponse()))
            }
            
            // 删除分类
            delete("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, Response.error("Invalid category ID"))
                
                val success = categoryService.deleteCategory(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent, Response.success(null))
                } else {
                    call.respond(HttpStatusCode.NotFound, Response.error("Category not found or has children/contents"))
                }
            }
            
            // 获取分类下的内容
            get("/{id}/contents") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) } 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, Response.error("Invalid category ID"))
                
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val pageRequest = PageRequest(page, size)
                val contents = categoryService.findContentsByCategory(id, pageRequest)
                
                call.respond(HttpStatusCode.OK, Response.success(contents))
            }
        }
    }
    
    private fun Category.toCategoryResponse(): CategoryResponse {
        return CategoryResponse(
            id = id,
            name = name,
            description = description,
            parentId = parentId,
            path = path,
            level = level,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 