package com.kace.content.api.routes

import com.kace.content.api.request.CreateContentTypeRequest
import com.kace.content.api.request.UpdateContentTypeRequest
import com.kace.content.api.response.ContentTypeResponse
import com.kace.content.api.response.PageResponse
import com.kace.content.domain.model.ContentField
import com.kace.content.domain.service.ContentTypeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

/**
 * 内容类型路由
 */
fun Route.contentTypeRoutes() {
    val contentTypeService: ContentTypeService by inject()
    
    route("/content-types") {
        // 获取所有内容类型
        get {
            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 100
            
            val contentTypes = contentTypeService.getAllContentTypes(offset, limit)
            val total = contentTypeService.getContentTypeCount()
            
            val response = PageResponse(
                data = contentTypes.map { ContentTypeResponse.fromDomain(it) },
                total = total,
                offset = offset,
                limit = limit
            )
            
            call.respond(response)
        }
        
        // 获取单个内容类型
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "缺少ID参数")
            
            try {
                val uuid = UUID.fromString(id)
                val contentType = contentTypeService.getContentType(uuid)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "内容类型不存在")
                
                call.respond(ContentTypeResponse.fromDomain(contentType))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "无效的ID格式")
            }
        }
        
        // 认证路由
        authenticate("jwt") {
            // 创建内容类型
            post {
                val request = call.receive<CreateContentTypeRequest>()
                
                try {
                    val fields = request.fields.map { field ->
                        ContentField.create(
                            name = field.name,
                            type = field.type,
                            required = field.required,
                            defaultValue = field.defaultValue,
                            validations = field.validations
                        )
                    }
                    
                    val contentType = contentTypeService.createContentType(
                        name = request.name,
                        description = request.description,
                        fields = fields
                    )
                    
                    call.respond(HttpStatusCode.Created, ContentTypeResponse.fromDomain(contentType))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "创建内容类型失败")
                }
            }
            
            // 更新内容类型
            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, "缺少ID参数")
                val request = call.receive<UpdateContentTypeRequest>()
                
                try {
                    val uuid = UUID.fromString(id)
                    
                    val fields = request.fields?.map { field ->
                        ContentField.create(
                            name = field.name,
                            type = field.type,
                            required = field.required,
                            defaultValue = field.defaultValue,
                            validations = field.validations
                        )
                    }
                    
                    val contentType = contentTypeService.updateContentType(
                        id = uuid,
                        name = request.name,
                        description = request.description,
                        fields = fields
                    ) ?: return@put call.respond(HttpStatusCode.NotFound, "内容类型不存在")
                    
                    call.respond(ContentTypeResponse.fromDomain(contentType))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "更新内容类型失败")
                }
            }
            
            // 删除内容类型
            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "缺少ID参数")
                
                try {
                    val uuid = UUID.fromString(id)
                    val deleted = contentTypeService.deleteContentType(uuid)
                    
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "内容类型不存在")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, "无效的ID格式")
                }
            }
        }
    }
} 