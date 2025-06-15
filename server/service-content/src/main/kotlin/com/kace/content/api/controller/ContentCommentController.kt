package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.*
import com.kace.content.api.response.ContentCommentDetailResponse
import com.kace.content.api.response.ContentCommentResponse
import com.kace.content.api.response.ContentInfo
import com.kace.content.api.response.UserInfo
import com.kace.content.domain.model.ContentComment
import com.kace.content.domain.service.ContentCommentService
import com.kace.content.domain.service.ContentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class ContentCommentController {
    
    fun Route.commentRoutes() {
        val contentCommentService by inject<ContentCommentService>()
        val contentService by inject<ContentService>()
        
        route("/api/v1/comments") {
            // 创建评论
            post {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val request = call.receive<CreateCommentRequest>()
                
                try {
                    val comment = contentCommentService.createComment(
                        contentId = request.contentId,
                        userId = userId,
                        content = request.content,
                        parentId = request.parentId,
                        metadata = request.metadata
                    )
                    
                    call.respond(
                        HttpStatusCode.Created,
                        Response.success(comment.toContentCommentResponse())
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error(e.message ?: "创建评论失败")
                    )
                }
            }
            
            // 获取评论列表（分页）
            get {
                val contentId = call.request.queryParameters["contentId"]?.let { UUID.fromString(it) }
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val parentId = call.request.queryParameters["parentId"]?.let { UUID.fromString(it) }
                val status = call.request.queryParameters["status"] ?: ContentComment.STATUS_PUBLISHED
                
                if (contentId == null) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("必须指定内容ID")
                    )
                }
                
                val pageRequest = PageRequest(page, size)
                val comments = contentCommentService.findCommentsByContent(
                    contentId = contentId,
                    pageRequest = pageRequest,
                    parentId = parentId,
                    status = status
                )
                
                // 为每个评论加载回复数量
                val commentResponses = comments.items.map { comment ->
                    val replyCount = if (comment.parentId == null) {
                        contentCommentService.countCommentsByContent(
                            contentId = comment.contentId,
                            status = ContentComment.STATUS_PUBLISHED
                        )
                    } else {
                        0L
                    }
                    
                    comment.toContentCommentResponse(replyCount = replyCount)
                }
                
                val pageResponse = PageResponse(
                    items = commentResponses,
                    page = comments.page,
                    size = comments.size,
                    total = comments.total,
                    totalPages = comments.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 根据ID获取评论
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的评论ID")
                    )
                
                val includeReplies = call.request.queryParameters["includeReplies"]?.toBoolean() ?: false
                val repliesLimit = call.request.queryParameters["repliesLimit"]?.toIntOrNull() ?: 5
                
                val comment = contentCommentService.findCommentById(id)
                    ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        Response.error("评论不存在")
                    )
                
                // 如果是根评论，同时请求了包含回复，则加载回复
                val replies = if (comment.parentId == null && includeReplies) {
                    val pageRequest = PageRequest(0, repliesLimit)
                    contentCommentService.findReplies(
                        commentId = comment.id,
                        pageRequest = pageRequest,
                        status = ContentComment.STATUS_PUBLISHED
                    ).items.map { it.toContentCommentResponse() }
                } else {
                    null
                }
                
                // 查询内容信息
                val content = contentService.findContentById(comment.contentId)
                val contentInfo = content?.let {
                    ContentInfo(
                        id = it.id,
                        title = it.title,
                        type = it.contentTypeId.toString()
                    )
                }
                
                // 构建详细响应
                val detailResponse = ContentCommentDetailResponse(
                    id = comment.id,
                    contentId = comment.contentId,
                    userId = comment.userId,
                    parentId = comment.parentId,
                    content = comment.content,
                    status = comment.status,
                    metadata = comment.metadata,
                    createdAt = comment.createdAt,
                    updatedAt = comment.updatedAt,
                    replies = replies,
                    userInfo = null, // 用户服务集成时填充
                    contentInfo = contentInfo
                )
                
                call.respond(HttpStatusCode.OK, Response.success(detailResponse))
            }
            
            // 更新评论
            put("/{id}") {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的评论ID")
                    )
                
                val request = call.receive<UpdateCommentRequest>()
                
                try {
                    val updatedComment = contentCommentService.updateComment(
                        id = id,
                        userId = userId,
                        content = request.content
                    ) ?: return@put call.respond(
                        HttpStatusCode.NotFound,
                        Response.error("评论不存在")
                    )
                    
                    call.respond(
                        HttpStatusCode.OK,
                        Response.success(updatedComment.toContentCommentResponse())
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error(e.message ?: "更新评论失败")
                    )
                }
            }
            
            // 更新评论状态（管理员）
            put("/{id}/status") {
                val isAdmin = call.request.headers["X-Is-Admin"]?.toBoolean() ?: false
                if (!isAdmin) {
                    return@put call.respond(
                        HttpStatusCode.Forbidden,
                        Response.error("没有权限执行此操作")
                    )
                }
                
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的评论ID")
                    )
                
                val request = call.receive<UpdateCommentStatusRequest>()
                
                val updatedComment = contentCommentService.updateCommentStatus(
                    id = id,
                    status = request.status
                ) ?: return@put call.respond(
                    HttpStatusCode.NotFound,
                    Response.error("评论不存在")
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    Response.success(updatedComment.toContentCommentResponse())
                )
            }
            
            // 删除评论
            delete("/{id}") {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@delete call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val isAdmin = call.request.headers["X-Is-Admin"]?.toBoolean() ?: false
                
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的评论ID")
                    )
                
                try {
                    val success = contentCommentService.deleteComment(
                        id = id,
                        userId = userId,
                        isAdmin = isAdmin
                    )
                    
                    if (success) {
                        call.respond(HttpStatusCode.NoContent, Response.success(null))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            Response.error("评论不存在")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Response.error(e.message ?: "删除评论失败")
                    )
                }
            }
            
            // 批量更新评论状态（管理员）
            put("/batch/status") {
                val isAdmin = call.request.headers["X-Is-Admin"]?.toBoolean() ?: false
                if (!isAdmin) {
                    return@put call.respond(
                        HttpStatusCode.Forbidden,
                        Response.error("没有权限执行此操作")
                    )
                }
                
                val request = call.receive<BatchUpdateCommentStatusRequest>()
                
                val updatedCount = contentCommentService.batchUpdateStatus(
                    ids = request.ids,
                    status = request.status
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    Response.success(mapOf("updatedCount" to updatedCount))
                )
            }
            
            // 获取用户的评论
            get("/user/{userId}") {
                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的用户ID")
                    )
                
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val status = call.request.queryParameters["status"]
                
                val pageRequest = PageRequest(page, size)
                val comments = contentCommentService.findCommentsByUser(
                    userId = userId,
                    pageRequest = pageRequest,
                    status = status
                )
                
                val commentResponses = comments.items.map { it.toContentCommentResponse() }
                val pageResponse = PageResponse(
                    items = commentResponses,
                    page = comments.page,
                    size = comments.size,
                    total = comments.total,
                    totalPages = comments.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 获取评论的回复
            get("/{id}/replies") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的评论ID")
                    )
                
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val status = call.request.queryParameters["status"] ?: ContentComment.STATUS_PUBLISHED
                
                val pageRequest = PageRequest(page, size)
                
                // 检查评论是否存在
                val comment = contentCommentService.findCommentById(id)
                    ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        Response.error("评论不存在")
                    )
                
                // 只有根评论可以有回复
                if (comment.parentId != null) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("只有根评论可以有回复")
                    )
                }
                
                val replies = contentCommentService.findReplies(
                    commentId = id,
                    pageRequest = pageRequest,
                    status = status
                )
                
                val replyResponses = replies.items.map { it.toContentCommentResponse() }
                val pageResponse = PageResponse(
                    items = replyResponses,
                    page = replies.page,
                    size = replies.size,
                    total = replies.total,
                    totalPages = replies.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 获取最近的评论
            get("/recent") {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                val status = call.request.queryParameters["status"] ?: ContentComment.STATUS_PUBLISHED
                
                val pageRequest = PageRequest(page, size)
                val comments = contentCommentService.getRecentComments(pageRequest, status)
                
                val commentResponses = comments.items.map { it.toContentCommentResponse() }
                val pageResponse = PageResponse(
                    items = commentResponses,
                    page = comments.page,
                    size = comments.size,
                    total = comments.total,
                    totalPages = comments.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
        }
    }
    
    private fun ContentComment.toContentCommentResponse(replyCount: Long = 0): ContentCommentResponse {
        return ContentCommentResponse(
            id = id,
            contentId = contentId,
            userId = userId,
            parentId = parentId,
            content = content,
            status = status,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt,
            replyCount = replyCount,
            userInfo = null // 用户服务集成时填充
        )
    }
} 