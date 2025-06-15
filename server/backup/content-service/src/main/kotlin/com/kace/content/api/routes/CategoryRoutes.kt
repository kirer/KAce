package com.kace.content.api.routes

import com.kace.content.api.request.CreateCategoryRequest
import com.kace.content.api.request.UpdateCategoryRequest
import com.kace.content.api.response.CategoryListResponse
import com.kace.content.api.response.CategoryResponse
import com.kace.content.api.response.CategoryTreeNode
import com.kace.content.api.response.CategoryTreeResponse
import com.kace.content.domain.service.CategoryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.format.DateTimeFormatter
import java.util.*

fun Route.categoryRoutes(categoryService: CategoryService) {
    route("/categories") {
        // 创建分类
        post {
            val request = call.receive<CreateCategoryRequest>()
            val userId = call.principal<UserPrincipal>()?.id ?: throw IllegalArgumentException("User not authenticated")
            
            val category = categoryService.createCategory(
                name = request.name,
                description = request.description,
                parentId = request.parentId?.let { UUID.fromString(it) },
                slug = request.slug,
                createdBy = UUID.fromString(userId)
            )
            
            val response = CategoryResponse(
                id = category.id.toString(),
                name = category.name,
                description = category.description,
                parentId = category.parentId?.toString(),
                slug = category.slug,
                createdAt = category.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                updatedAt = category.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                createdBy = category.createdBy.toString()
            )
            
            call.respond(HttpStatusCode.Created, response)
        }
        
        // 获取分类列表
        get {
            val page = call.parameters["page"]?.toIntOrNull() ?: 1
            val size = call.parameters["size"]?.toIntOrNull() ?: 10
            val parentId = call.parameters["parentId"]
            
            val categories = if (parentId != null) {
                categoryService.getCategoriesByParentId(UUID.fromString(parentId))
            } else {
                categoryService.getAllCategories(page, size)
            }
            
            val total = categoryService.countCategories()
            
            val items = categories.map { category ->
                CategoryResponse(
                    id = category.id.toString(),
                    name = category.name,
                    description = category.description,
                    parentId = category.parentId?.toString(),
                    slug = category.slug,
                    createdAt = category.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    updatedAt = category.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                    createdBy = category.createdBy.toString()
                )
            }
            
            call.respond(CategoryListResponse(items, total, page, size))
        }
        
        // 获取分类树结构
        get("/tree") {
            // 获取所有分类
            val allCategories = categoryService.getAllCategories(1, Int.MAX_VALUE)
            
            // 构建分类树
            val rootCategories = buildCategoryTree(allCategories)
            
            call.respond(CategoryTreeResponse(rootCategories))
        }
        
        route("/{id}") {
            // 获取单个分类
            get {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val category = categoryService.getCategoryById(UUID.fromString(id))
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Category not found"))
                
                val response = CategoryResponse(
                    id = category.id.toString(),
                    name = category.name,
                    description = category.description,
                    parentId = category.parentId?.toString(),
                    slug = category.slug,
                    createdAt = category.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    updatedAt = category.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                    createdBy = category.createdBy.toString()
                )
                
                call.respond(response)
            }
            
            // 更新分类
            put {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<UpdateCategoryRequest>()
                
                val category = categoryService.updateCategory(
                    id = UUID.fromString(id),
                    name = request.name,
                    description = request.description,
                    parentId = request.parentId?.let { UUID.fromString(it) },
                    slug = request.slug
                )
                
                val response = CategoryResponse(
                    id = category.id.toString(),
                    name = category.name,
                    description = category.description,
                    parentId = category.parentId?.toString(),
                    slug = category.slug,
                    createdAt = category.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    updatedAt = category.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                    createdBy = category.createdBy.toString()
                )
                
                call.respond(response)
            }
            
            // 删除分类
            delete {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val result = categoryService.deleteCategory(UUID.fromString(id))
                
                if (result) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Cannot delete category with children or associated content"))
                }
            }
            
            // 获取分类下的子分类
            get("/children") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val categories = categoryService.getCategoriesByParentId(UUID.fromString(id))
                
                val items = categories.map { category ->
                    CategoryResponse(
                        id = category.id.toString(),
                        name = category.name,
                        description = category.description,
                        parentId = category.parentId?.toString(),
                        slug = category.slug,
                        createdAt = category.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                        updatedAt = category.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                        createdBy = category.createdBy.toString()
                    )
                }
                
                call.respond(items)
            }
        }
        
        // 根据slug获取分类
        get("/by-slug/{slug}") {
            val slug = call.parameters["slug"] ?: throw IllegalArgumentException("Missing slug parameter")
            
            val category = categoryService.getCategoryBySlug(slug)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Category not found"))
            
            val response = CategoryResponse(
                id = category.id.toString(),
                name = category.name,
                description = category.description,
                parentId = category.parentId?.toString(),
                slug = category.slug,
                createdAt = category.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                updatedAt = category.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                createdBy = category.createdBy.toString()
            )
            
            call.respond(response)
        }
    }
}

/**
 * 构建分类树结构
 */
private fun buildCategoryTree(categories: List<com.kace.content.domain.model.Category>): List<CategoryTreeNode> {
    // 按父ID分组
    val categoryMap = categories.groupBy { it.parentId }
    
    // 构建树结构
    fun buildTree(parentId: UUID?): List<CategoryTreeNode> {
        return categoryMap[parentId]?.map { category ->
            CategoryTreeNode(
                id = category.id.toString(),
                name = category.name,
                description = category.description,
                slug = category.slug,
                children = buildTree(category.id)
            )
        } ?: emptyList()
    }
    
    // 从根分类开始构建
    return buildTree(null)
}