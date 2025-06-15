package com.kace.content.api.routes

import com.kace.content.api.request.*
import com.kace.content.api.response.*
import com.kace.content.domain.model.ContentStatus
import com.kace.content.domain.service.CategoryService
import com.kace.content.domain.service.ContentService
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
 * 内容路由
 */
fun Route.contentRoutes(
    contentService: ContentService,
    categoryService: CategoryService,
    tagService: TagService
) {
    route("/contents") {
        // 创建内容
        post {
            val request = call.receive<CreateContentRequest>()
            val userId = call.principal<UserPrincipal>()?.id ?: throw IllegalArgumentException("User not authenticated")
            
            val content = contentService.createContent(
                contentTypeId = UUID.fromString(request.contentTypeId),
                title = request.title,
                slug = request.slug,
                fields = request.fields,
                status = ContentStatus.valueOf(request.status),
                createdBy = UUID.fromString(userId),
                languageCode = request.languageCode
            )
            
            val response = ContentResponse(
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
            
            call.respond(HttpStatusCode.Created, response)
        }
        
        // 获取内容列表（按内容类型）
        get {
            val contentTypeId = call.parameters["contentTypeId"]
            val page = call.parameters["page"]?.toIntOrNull() ?: 1
            val size = call.parameters["size"]?.toIntOrNull() ?: 10
            
            val contents = if (contentTypeId != null) {
                contentService.getContentsByContentType(UUID.fromString(contentTypeId), page, size)
            } else {
                contentService.getContentsByStatus(ContentStatus.PUBLISHED, page, size)
            }
            
            val total = contentService.countContents(contentTypeId?.let { UUID.fromString(it) })
            
            val items = contents.map { content ->
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
            
            call.respond(ContentListResponse(items, total, page, size))
        }
        
        route("/{id}") {
            // 获取单个内容
            get {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val content = contentService.getContentById(UUID.fromString(id))
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Content not found"))
                
                val response = ContentResponse(
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
                
                call.respond(response)
            }
            
            // 更新内容
            put {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<UpdateContentRequest>()
                val userId = call.principal<UserPrincipal>()?.id ?: throw IllegalArgumentException("User not authenticated")
                
                val content = contentService.updateContent(
                    id = UUID.fromString(id),
                    title = request.title,
                    slug = request.slug,
                    fields = request.fields,
                    status = ContentStatus.valueOf(request.status),
                    updatedBy = UUID.fromString(userId),
                    comment = request.comment
                )
                
                val response = ContentResponse(
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
                
                call.respond(response)
            }
            
            // 删除内容
            delete {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val result = contentService.deleteContent(UUID.fromString(id))
                
                if (result) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Content not found"))
                }
            }
            
            // 发布内容
            post("/publish") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<PublishContentRequest>()
                val userId = call.principal<UserPrincipal>()?.id ?: throw IllegalArgumentException("User not authenticated")
                
                val content = contentService.publishContent(
                    id = UUID.fromString(id),
                    updatedBy = UUID.fromString(userId)
                )
                
                val response = ContentResponse(
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
                
                call.respond(response)
            }
            
            // 取消发布内容
            post("/unpublish") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<UnpublishContentRequest>()
                val userId = call.principal<UserPrincipal>()?.id ?: throw IllegalArgumentException("User not authenticated")
                
                val content = contentService.unpublishContent(
                    id = UUID.fromString(id),
                    updatedBy = UUID.fromString(userId)
                )
                
                val response = ContentResponse(
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
                
                call.respond(response)
            }
            
            // 获取内容版本历史
            get("/versions") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val versions = contentService.getContentVersions(UUID.fromString(id))
                
                val items = versions.map { version ->
                    ContentVersionResponse(
                        id = version.id.toString(),
                        contentId = version.contentId.toString(),
                        version = version.version,
                        fieldsJson = version.fieldsJson,
                        createdBy = version.createdBy.toString(),
                        createdAt = version.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                        comment = version.comment
                    )
                }
                
                call.respond(ContentVersionListResponse(items))
            }
            
            // 获取特定版本的内容
            get("/versions/{version}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val version = call.parameters["version"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid version parameter")
                
                val contentVersion = contentService.getContentVersion(UUID.fromString(id), version)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Content version not found"))
                
                val response = ContentVersionResponse(
                    id = contentVersion.id.toString(),
                    contentId = contentVersion.contentId.toString(),
                    version = contentVersion.version,
                    fieldsJson = contentVersion.fieldsJson,
                    createdBy = contentVersion.createdBy.toString(),
                    createdAt = contentVersion.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                    comment = contentVersion.comment
                )
                
                call.respond(response)
            }
            
            // 添加内容到分类
            post("/categories") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<AddContentCategoryRequest>()
                
                val result = categoryService.addContentToCategory(
                    contentId = UUID.fromString(id),
                    categoryId = UUID.fromString(request.categoryId)
                )
                
                if (result) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to add content to category"))
                }
            }
            
            // 从分类中移除内容
            delete("/categories/{categoryId}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val categoryId = call.parameters["categoryId"] ?: throw IllegalArgumentException("Missing categoryId parameter")
                
                val result = categoryService.removeContentFromCategory(
                    contentId = UUID.fromString(id),
                    categoryId = UUID.fromString(categoryId)
                )
                
                if (result) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Content or category not found"))
                }
            }
            
            // 获取内容的所有分类
            get("/categories") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val categories = categoryService.getCategoriesByContentId(UUID.fromString(id))
                
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
            
            // 添加标签到内容
            post("/tags") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<AddContentTagRequest>()
                
                val result = tagService.addContentToTag(
                    contentId = UUID.fromString(id),
                    tagId = UUID.fromString(request.tagId)
                )
                
                if (result) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to add tag to content"))
                }
            }
            
            // 批量添加标签到内容
            post("/tags/batch") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val request = call.receive<AddContentTagsRequest>()
                val userId = call.principal<UserPrincipal>()?.id ?: throw IllegalArgumentException("User not authenticated")
                
                val addedTags = mutableListOf<TagResponse>()
                
                for (tagName in request.tags) {
                    val tag = tagService.findOrCreateTag(tagName, UUID.fromString(userId))
                    tagService.addContentToTag(UUID.fromString(id), tag.id)
                    
                    addedTags.add(
                        TagResponse(
                            id = tag.id.toString(),
                            name = tag.name,
                            slug = tag.slug,
                            createdAt = tag.createdAt.format(DateTimeFormatter.ISO_INSTANT),
                            updatedAt = tag.updatedAt.format(DateTimeFormatter.ISO_INSTANT),
                            createdBy = tag.createdBy.toString()
                        )
                    )
                }
                
                call.respond(addedTags)
            }
            
            // 从内容中移除标签
            delete("/tags/{tagId}") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                val tagId = call.parameters["tagId"] ?: throw IllegalArgumentException("Missing tagId parameter")
                
                val result = tagService.removeContentFromTag(
                    contentId = UUID.fromString(id),
                    tagId = UUID.fromString(tagId)
                )
                
                if (result) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Content or tag not found"))
                }
            }
            
            // 获取内容的所有标签
            get("/tags") {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing id parameter")
                
                val tags = tagService.getTagsByContentId(UUID.fromString(id))
                
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
                
                call.respond(items)
            }
        }
        
        // 根据slug获取内容
        get("/by-slug/{slug}") {
            val slug = call.parameters["slug"] ?: throw IllegalArgumentException("Missing slug parameter")
            
            val content = contentService.getContentBySlug(slug)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Content not found"))
            
            val response = ContentResponse(
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
            
            call.respond(response)
        }
    }
}

data class UserPrincipal(val id: String) : Principal