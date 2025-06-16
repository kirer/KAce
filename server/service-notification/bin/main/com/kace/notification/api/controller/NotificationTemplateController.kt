package com.kace.notification.api.controller

import com.kace.notification.api.model.request.CreateTemplateRequest
import com.kace.notification.api.model.request.RenderTemplateRequest
import com.kace.notification.api.model.request.UpdateTemplateRequest
import com.kace.notification.api.model.response.TemplateActionResponse
import com.kace.notification.api.model.response.TemplateListResponse
import com.kace.notification.api.model.response.TemplateRenderResponse
import com.kace.notification.api.model.response.toResponse
import com.kace.notification.domain.model.TemplateType
import com.kace.notification.domain.service.NotificationTemplateService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import java.util.UUID

/**
 * 通知模板控制器
 */
fun Route.templateRoutes() {
    val templateService: NotificationTemplateService by inject()
    
    route("/templates") {
        // 创建模板
        post {
            val request = call.receive<CreateTemplateRequest>()
            
            val template = templateService.createTemplate(
                name = request.name,
                type = TemplateType.valueOf(request.type),
                content = request.content,
                subject = request.subject,
                description = request.description,
                variables = request.variables,
                isActive = request.isActive
            )
            
            call.respond(template.toResponse())
        }
        
        // 更新模板
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<UpdateTemplateRequest>()
            
            val updatedTemplate = templateService.updateTemplate(
                id = UUID.fromString(id),
                name = request.name,
                type = request.type?.let { TemplateType.valueOf(it) },
                content = request.content,
                subject = request.subject,
                description = request.description,
                variables = request.variables,
                isActive = request.isActive
            )
            
            if (updatedTemplate != null) {
                call.respond(updatedTemplate.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取模板
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val template = templateService.getTemplate(UUID.fromString(id))
            
            if (template != null) {
                call.respond(template.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取模板（按名称）
        get("/name/{name}") {
            val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val template = templateService.getTemplateByName(name)
            
            if (template != null) {
                call.respond(template.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取模板列表（按类型）
        get("/type/{type}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
            
            val templates = templateService.getTemplatesByType(
                type = TemplateType.valueOf(type),
                limit = limit,
                offset = offset
            )
            
            call.respond(
                TemplateListResponse(
                    templates = templates.map { it.toResponse() },
                    total = templates.size.toLong(),
                    page = offset / limit + 1,
                    pageSize = limit
                )
            )
        }
        
        // 获取所有活跃模板
        get("/active") {
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
            
            val templates = templateService.getAllActiveTemplates(limit, offset)
            
            call.respond(
                TemplateListResponse(
                    templates = templates.map { it.toResponse() },
                    total = templates.size.toLong(),
                    page = offset / limit + 1,
                    pageSize = limit
                )
            )
        }
        
        // 获取所有模板
        get {
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
            
            val templates = templateService.getAllTemplates(limit, offset)
            
            call.respond(
                TemplateListResponse(
                    templates = templates.map { it.toResponse() },
                    total = templates.size.toLong(),
                    page = offset / limit + 1,
                    pageSize = limit
                )
            )
        }
        
        // 激活模板
        put("/{id}/activate") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = templateService.activateTemplate(UUID.fromString(id))
            
            call.respond(
                TemplateActionResponse(
                    success = success,
                    message = if (success) "模板已激活" else "无法激活模板"
                )
            )
        }
        
        // 停用模板
        put("/{id}/deactivate") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = templateService.deactivateTemplate(UUID.fromString(id))
            
            call.respond(
                TemplateActionResponse(
                    success = success,
                    message = if (success) "模板已停用" else "无法停用模板"
                )
            )
        }
        
        // 删除模板
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            
            val success = templateService.deleteTemplate(UUID.fromString(id))
            
            call.respond(
                TemplateActionResponse(
                    success = success,
                    message = if (success) "模板已删除" else "无法删除模板"
                )
            )
        }
        
        // 渲染模板
        post("/{id}/render") {
            val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<RenderTemplateRequest>()
            
            val renderedContent = templateService.renderTemplate(
                templateId = UUID.fromString(id),
                variables = request.variables.mapValues { it.value as Any }
            )
            
            if (renderedContent != null) {
                call.respond(
                    TemplateRenderResponse(
                        renderedContent = renderedContent
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TemplateActionResponse(
                        success = false,
                        message = "无法渲染模板，模板不存在或变量无效"
                    )
                )
            }
        }
        
        // 渲染模板（按名称）
        post("/name/{name}/render") {
            val name = call.parameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<RenderTemplateRequest>()
            
            val renderedContent = templateService.renderTemplateByName(
                templateName = name,
                variables = request.variables.mapValues { it.value as Any }
            )
            
            if (renderedContent != null) {
                call.respond(
                    TemplateRenderResponse(
                        renderedContent = renderedContent
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    TemplateActionResponse(
                        success = false,
                        message = "无法渲染模板，模板不存在或变量无效"
                    )
                )
            }
        }
    }
} 