package com.kace.content.api.controller

import com.kace.common.response.ApiResponse
import com.kace.content.api.model.ContentTypeListResponse
import com.kace.content.api.model.ContentTypeResponse
import com.kace.content.api.model.CreateContentTypeRequest
import com.kace.content.api.model.UpdateContentTypeRequest
import com.kace.content.domain.service.ContentTypeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 内容类型控制器
 */
class ContentTypeController(private val contentTypeService: ContentTypeService) {
    private val logger = LoggerFactory.getLogger(ContentTypeController::class.java)
    
    /**
     * 注册路由
     */
    fun registerRoutes(route: Route) {
        route.route("/content-types") {
            authenticate {
                post {
                    createContentType(call)
                }
                
                get {
                    getContentTypes(call)
                }
                
                get("/{id}") {
                    getContentType(call)
                }
                
                put("/{id}") {
                    updateContentType(call)
                }
                
                delete("/{id}") {
                    deleteContentType(call)
                }
            }
        }
    }
    
    /**
     * 创建内容类型
     */
    private suspend fun createContentType(call: ApplicationCall) {
        try {
            val request = call.receive<CreateContentTypeRequest>()
            val userId = call.principal<UserPrincipal>()?.id
                ?: throw IllegalArgumentException("未授权的请求")
            
            val contentType = request.toDomain(UUID.fromString(userId))
            val createdContentType = contentTypeService.createContentType(contentType)
            
            call.respond(
                HttpStatusCode.Created,
                ApiResponse.success(ContentTypeResponse.fromDomain(createdContentType))
            )
        } catch (e: Exception) {
            logger.error("创建内容类型失败", e)
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error<Unit>(e.message ?: "创建内容类型失败")
            )
        }
    }
    
    /**
     * 获取内容类型列表
     */
    private suspend fun getContentTypes(call: ApplicationCall) {
        try {
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 100
            
            val contentTypes = contentTypeService.getContentTypes(offset, limit)
            val total = contentTypeService.getContentTypeCount()
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse.success(
                    ContentTypeListResponse(
                        items = contentTypes.map { ContentTypeResponse.fromDomain(it) },
                        total = total,
                        offset = offset,
                        limit = limit
                    )
                )
            )
        } catch (e: Exception) {
            logger.error("获取内容类型列表失败", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse.error<Unit>(e.message ?: "获取内容类型列表失败")
            )
        }
    }
    
    /**
     * 获取内容类型
     */
    private suspend fun getContentType(call: ApplicationCall) {
        try {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("缺少ID参数")
            val contentType = contentTypeService.getContentType(UUID.fromString(id))
                ?: return call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse.error<Unit>("内容类型不存在: $id")
                )
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse.success(ContentTypeResponse.fromDomain(contentType))
            )
        } catch (e: Exception) {
            logger.error("获取内容类型失败", e)
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error<Unit>(e.message ?: "获取内容类型失败")
            )
        }
    }
    
    /**
     * 更新内容类型
     */
    private suspend fun updateContentType(call: ApplicationCall) {
        try {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("缺少ID参数")
            val request = call.receive<UpdateContentTypeRequest>()
            
            val existingContentType = contentTypeService.getContentType(UUID.fromString(id))
                ?: return call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse.error<Unit>("内容类型不存在: $id")
                )
            
            val contentType = request.toDomain(UUID.fromString(id), existingContentType)
            val updatedContentType = contentTypeService.updateContentType(contentType)
            
            call.respond(
                HttpStatusCode.OK,
                ApiResponse.success(ContentTypeResponse.fromDomain(updatedContentType))
            )
        } catch (e: Exception) {
            logger.error("更新内容类型失败", e)
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error<Unit>(e.message ?: "更新内容类型失败")
            )
        }
    }
    
    /**
     * 删除内容类型
     */
    private suspend fun deleteContentType(call: ApplicationCall) {
        try {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("缺少ID参数")
            val result = contentTypeService.deleteContentType(UUID.fromString(id))
            
            if (result) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse.error<Unit>("内容类型不存在: $id")
                )
            }
        } catch (e: Exception) {
            logger.error("删除内容类型失败", e)
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error<Unit>(e.message ?: "删除内容类型失败")
            )
        }
    }
}

/**
 * 用户主体
 */
data class UserPrincipal(val id: String) : Principal 