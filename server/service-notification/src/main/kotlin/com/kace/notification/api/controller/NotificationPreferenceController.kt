package com.kace.notification.api.controller

import com.kace.notification.api.model.request.BatchCreatePreferenceRequest
import com.kace.notification.api.model.request.CreatePreferenceRequest
import com.kace.notification.api.model.request.UpdatePreferenceRequest
import com.kace.notification.api.model.response.PreferenceActionResponse
import com.kace.notification.api.model.response.PreferenceListResponse
import com.kace.notification.api.model.response.toResponse
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.service.NotificationPreferenceService
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
 * 通知偏好控制器
 */
fun Route.preferenceRoutes() {
    val preferenceService: NotificationPreferenceService by inject()
    
    route("/preferences") {
        // 创建用户通知偏好
        post("/user/{userId}") {
            val userId = call.parameters["userId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<CreatePreferenceRequest>()
            
            val preference = preferenceService.createPreference(
                userId = UUID.fromString(userId),
                eventType = request.eventType,
                channels = request.channels.map { NotificationType.valueOf(it) }.toSet(),
                enabled = request.enabled,
                quietHoursStart = request.quietHoursStart,
                quietHoursEnd = request.quietHoursEnd
            )
            
            call.respond(preference.toResponse())
        }
        
        // 批量创建用户通知偏好
        post("/user/{userId}/batch") {
            val userId = call.parameters["userId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<BatchCreatePreferenceRequest>()
            
            val preferences = mutableListOf()
            
            for (preferenceRequest in request.preferences) {
                val preference = preferenceService.createPreference(
                    userId = UUID.fromString(userId),
                    eventType = preferenceRequest.eventType,
                    channels = preferenceRequest.channels.map { NotificationType.valueOf(it) }.toSet(),
                    enabled = preferenceRequest.enabled,
                    quietHoursStart = preferenceRequest.quietHoursStart,
                    quietHoursEnd = preferenceRequest.quietHoursEnd
                )
                
                preferences.add(preference)
            }
            
            call.respond(
                PreferenceListResponse(
                    preferences = preferences.map { it.toResponse() }
                )
            )
        }
        
        // 更新通知偏好
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<UpdatePreferenceRequest>()
            
            val updatedPreference = preferenceService.updatePreference(
                id = UUID.fromString(id),
                channels = request.channels?.map { NotificationType.valueOf(it) }?.toSet(),
                enabled = request.enabled,
                quietHoursStart = request.quietHoursStart,
                quietHoursEnd = request.quietHoursEnd
            )
            
            if (updatedPreference != null) {
                call.respond(updatedPreference.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取通知偏好
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val preference = preferenceService.getPreference(UUID.fromString(id))
            
            if (preference != null) {
                call.respond(preference.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取用户所有通知偏好
        get("/user/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val preferences = preferenceService.getUserPreferences(UUID.fromString(userId))
            
            call.respond(
                PreferenceListResponse(
                    preferences = preferences.map { it.toResponse() }
                )
            )
        }
        
        // 获取用户特定事件类型的通知偏好
        get("/user/{userId}/event/{eventType}") {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val eventType = call.parameters["eventType"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val preference = preferenceService.getUserPreferenceByEventType(UUID.fromString(userId), eventType)
            
            if (preference != null) {
                call.respond(preference.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 启用通知偏好
        put("/{id}/enable") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = preferenceService.enablePreference(UUID.fromString(id))
            
            call.respond(
                PreferenceActionResponse(
                    success = success,
                    message = if (success) "通知偏好已启用" else "无法启用通知偏好",
                    affectedCount = if (success) 1 else 0
                )
            )
        }
        
        // 禁用通知偏好
        put("/{id}/disable") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = preferenceService.disablePreference(UUID.fromString(id))
            
            call.respond(
                PreferenceActionResponse(
                    success = success,
                    message = if (success) "通知偏好已禁用" else "无法禁用通知偏好",
                    affectedCount = if (success) 1 else 0
                )
            )
        }
        
        // 删除通知偏好
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            
            val success = preferenceService.deletePreference(UUID.fromString(id))
            
            call.respond(
                PreferenceActionResponse(
                    success = success,
                    message = if (success) "通知偏好已删除" else "无法删除通知偏好",
                    affectedCount = if (success) 1 else 0
                )
            )
        }
        
        // 删除用户所有通知偏好
        delete("/user/{userId}") {
            val userId = call.parameters["userId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            
            val count = preferenceService.deleteUserPreferences(UUID.fromString(userId))
            
            call.respond(
                PreferenceActionResponse(
                    success = count > 0,
                    message = "$count 个通知偏好已删除",
                    affectedCount = count
                )
            )
        }
    }
} 