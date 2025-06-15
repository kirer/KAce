package com.kace.content.api.controller

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.common.api.Response
import com.kace.content.api.request.*
import com.kace.content.api.response.ContentFeedbackResponse
import com.kace.content.api.response.ContentFeedbackStatsResponse
import com.kace.content.api.response.UserFeedbackStatusResponse
import com.kace.content.domain.model.ContentFeedback
import com.kace.content.domain.service.ContentFeedbackService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

class ContentFeedbackController {
    
    fun Route.feedbackRoutes() {
        val contentFeedbackService by inject<ContentFeedbackService>()
        
        route("/api/v1/feedback") {
            // 创建反馈
            post {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val request = call.receive<CreateFeedbackRequest>()
                
                try {
                    val feedback = contentFeedbackService.createOrUpdateFeedback(
                        contentId = request.contentId,
                        userId = userId,
                        type = request.type,
                        value = request.value,
                        metadata = request.metadata
                    )
                    
                    call.respond(
                        HttpStatusCode.Created,
                        Response.success(feedback.toContentFeedbackResponse())
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error(e.message ?: "创建反馈失败")
                    )
                }
            }
            
            // 获取内容的反馈统计
            get("/stats/{contentId}") {
                val contentId = call.parameters["contentId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的内容ID")
                    )
                
                val stats = contentFeedbackService.getContentFeedbackStats(contentId)
                val statsResponse = createStatsResponse(contentId, stats)
                
                call.respond(HttpStatusCode.OK, Response.success(statsResponse))
            }
            
            // 批量获取多个内容的反馈统计
            post("/stats/batch") {
                val contentIds = call.receive<List<UUID>>()
                
                if (contentIds.isEmpty()) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("内容ID列表不能为空")
                    )
                }
                
                if (contentIds.size > 100) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("最多可请求100个内容的统计信息")
                    )
                }
                
                val batchStats = contentFeedbackService.getBatchContentFeedbackStats(contentIds)
                val responseStats = batchStats.mapValues { (contentId, stats) ->
                    createStatsResponse(contentId, stats)
                }
                
                call.respond(HttpStatusCode.OK, Response.success(responseStats))
            }
            
            // 获取用户对内容的反馈状态
            get("/user-status") {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val contentId = call.request.queryParameters["contentId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("必须指定内容ID")
                    )
                
                // 获取各种类型的反馈
                val likeFeedback = contentFeedbackService.getUserFeedbackForContent(
                    contentId = contentId,
                    userId = userId,
                    type = ContentFeedback.TYPE_LIKE
                )
                
                val ratingFeedback = contentFeedbackService.getUserFeedbackForContent(
                    contentId = contentId,
                    userId = userId,
                    type = ContentFeedback.TYPE_RATING
                )
                
                val helpfulFeedback = contentFeedbackService.getUserFeedbackForContent(
                    contentId = contentId,
                    userId = userId,
                    type = ContentFeedback.TYPE_HELPFUL
                )
                
                val reactionFeedback = contentFeedbackService.getUserFeedbackForContent(
                    contentId = contentId,
                    userId = userId,
                    type = ContentFeedback.TYPE_REACTION
                )
                
                // 构建响应
                val statusResponse = UserFeedbackStatusResponse(
                    contentId = contentId,
                    userId = userId,
                    liked = likeFeedback?.value?.let { it > 0 },
                    rating = ratingFeedback?.value,
                    helpful = helpfulFeedback?.value,
                    reaction = reactionFeedback?.value
                )
                
                call.respond(HttpStatusCode.OK, Response.success(statusResponse))
            }
            
            // 根据ID获取反馈
            get("/{id}") {
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的反馈ID")
                    )
                
                val feedback = contentFeedbackService.findFeedbackById(id)
                    ?: return@get call.respond(
                        HttpStatusCode.NotFound,
                        Response.error("反馈不存在")
                    )
                
                call.respond(
                    HttpStatusCode.OK,
                    Response.success(feedback.toContentFeedbackResponse())
                )
            }
            
            // 更新反馈
            put("/{id}") {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val id = call.parameters["id"]?.let { UUID.fromString(it) }
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的反馈ID")
                    )
                
                val isAdmin = call.request.headers["X-Is-Admin"]?.toBoolean() ?: false
                
                // 检查反馈是否存在
                val existingFeedback = contentFeedbackService.findFeedbackById(id)
                    ?: return@put call.respond(
                        HttpStatusCode.NotFound,
                        Response.error("反馈不存在")
                    )
                
                // 检查权限
                if (!isAdmin && existingFeedback.userId != userId) {
                    return@put call.respond(
                        HttpStatusCode.Forbidden,
                        Response.error("您没有权限更新此反馈")
                    )
                }
                
                // 更新反馈
                val request = call.receive<UpdateFeedbackRequest>()
                
                try {
                    val updatedFeedback = contentFeedbackService.createOrUpdateFeedback(
                        contentId = existingFeedback.contentId,
                        userId = existingFeedback.userId,
                        type = existingFeedback.type,
                        value = request.value,
                        metadata = request.metadata
                    )
                    
                    call.respond(
                        HttpStatusCode.OK,
                        Response.success(updatedFeedback.toContentFeedbackResponse())
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error(e.message ?: "更新反馈失败")
                    )
                }
            }
            
            // 删除反馈
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
                        Response.error("无效的反馈ID")
                    )
                
                try {
                    val success = contentFeedbackService.deleteFeedback(
                        id = id,
                        userId = userId,
                        isAdmin = isAdmin
                    )
                    
                    if (success) {
                        call.respond(HttpStatusCode.NoContent, Response.success(null))
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            Response.error("反馈不存在")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Response.error(e.message ?: "删除反馈失败")
                    )
                }
            }
            
            // 获取内容的所有反馈
            get("/content/{contentId}") {
                val contentId = call.parameters["contentId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的内容ID")
                    )
                
                val type = call.request.queryParameters["type"]
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val pageRequest = PageRequest(page, size)
                val feedbacks = contentFeedbackService.findFeedbacksByContent(
                    contentId = contentId,
                    pageRequest = pageRequest,
                    type = type
                )
                
                val feedbackResponses = feedbacks.items.map { it.toContentFeedbackResponse() }
                val pageResponse = PageResponse(
                    items = feedbackResponses,
                    page = feedbacks.page,
                    size = feedbacks.size,
                    total = feedbacks.total,
                    totalPages = feedbacks.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 获取用户的所有反馈
            get("/user/{userId}") {
                val requestUserId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                val isAdmin = call.request.headers["X-Is-Admin"]?.toBoolean() ?: false
                
                val userId = call.parameters["userId"]?.let { UUID.fromString(it) }
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error("无效的用户ID")
                    )
                
                // 普通用户只能查看自己的反馈
                if (!isAdmin && requestUserId != userId) {
                    return@get call.respond(
                        HttpStatusCode.Forbidden,
                        Response.error("您没有权限查看其他用户的反馈")
                    )
                }
                
                val type = call.request.queryParameters["type"]
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                
                val pageRequest = PageRequest(page, size)
                val feedbacks = contentFeedbackService.findFeedbacksByUser(
                    userId = userId,
                    pageRequest = pageRequest,
                    type = type
                )
                
                val feedbackResponses = feedbacks.items.map { it.toContentFeedbackResponse() }
                val pageResponse = PageResponse(
                    items = feedbackResponses,
                    page = feedbacks.page,
                    size = feedbacks.size,
                    total = feedbacks.total,
                    totalPages = feedbacks.totalPages
                )
                
                call.respond(HttpStatusCode.OK, Response.success(pageResponse))
            }
            
            // 点赞内容（快捷方法）
            post("/like") {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val request = call.receive<LikeContentRequest>()
                
                try {
                    val feedback = contentFeedbackService.likeContent(
                        contentId = request.contentId,
                        userId = userId,
                        isLiked = request.isLiked
                    )
                    
                    call.respond(
                        HttpStatusCode.OK,
                        Response.success(feedback.toContentFeedbackResponse())
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error(e.message ?: "点赞操作失败")
                    )
                }
            }
            
            // 评分内容（快捷方法）
            post("/rate") {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val request = call.receive<RateContentRequest>()
                
                try {
                    val feedback = contentFeedbackService.rateContent(
                        contentId = request.contentId,
                        userId = userId,
                        rating = request.rating
                    )
                    
                    call.respond(
                        HttpStatusCode.OK,
                        Response.success(feedback.toContentFeedbackResponse())
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error(e.message ?: "评分操作失败")
                    )
                }
            }
            
            // 标记内容有用性（快捷方法）
            post("/helpful") {
                val userId = call.request.headers["X-User-ID"]?.let { UUID.fromString(it) }
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        Response.error("用户未认证")
                    )
                
                val request = call.receive<MarkHelpfulRequest>()
                
                try {
                    val feedback = contentFeedbackService.markContentHelpfulness(
                        contentId = request.contentId,
                        userId = userId,
                        value = if (request.isHelpful) 1 else -1
                    )
                    
                    call.respond(
                        HttpStatusCode.OK,
                        Response.success(feedback.toContentFeedbackResponse())
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response.error(e.message ?: "标记操作失败")
                    )
                }
            }
        }
    }
    
    private fun createStatsResponse(contentId: UUID, stats: Map<String, Any>): ContentFeedbackStatsResponse {
        return ContentFeedbackStatsResponse(
            contentId = contentId,
            likes = stats["likes"] as? Int ?: 0,
            ratingAverage = stats["rating_average"] as? Double,
            ratingCount = stats["rating_count"] as? Int,
            ratingDistribution = stats["rating_distribution"] as? Map<Int, Int>,
            helpfulCount = stats["helpful_count"] as? Int,
            notHelpfulCount = stats["not_helpful_count"] as? Int,
            reactions = stats["reactions"] as? Map<Int, Int>
        )
    }
    
    private fun ContentFeedback.toContentFeedbackResponse(): ContentFeedbackResponse {
        return ContentFeedbackResponse(
            id = id,
            contentId = contentId,
            userId = userId,
            type = type,
            value = value,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
} 