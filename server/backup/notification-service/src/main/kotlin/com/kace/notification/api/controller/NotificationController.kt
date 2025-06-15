package com.kace.notification.api.controller

import com.kace.notification.api.model.request.BatchCreateNotificationFromTemplateRequest
import com.kace.notification.api.model.request.BatchCreateNotificationRequest
import com.kace.notification.api.model.request.CreateNotificationFromTemplateRequest
import com.kace.notification.api.model.request.CreateNotificationRequest
import com.kace.notification.api.model.request.DeleteNotificationRequest
import com.kace.notification.api.model.request.MarkNotificationReadRequest
import com.kace.notification.api.model.response.BatchNotificationCreatedResponse
import com.kace.notification.api.model.response.NotificationActionResponse
import com.kace.notification.api.model.response.NotificationCountResponse
import com.kace.notification.api.model.response.NotificationCreatedResponse
import com.kace.notification.api.model.response.NotificationListResponse
import com.kace.notification.api.model.response.NotificationResponse
import com.kace.notification.api.model.response.toResponse
import com.kace.notification.domain.model.NotificationPriority
import com.kace.notification.domain.model.NotificationStatus
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.service.NotificationService
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
import java.time.Instant
import java.util.UUID

/**
 * 通知控制器
 */
fun Route.notificationRoutes() {
    val notificationService: NotificationService by inject()
    
    route("/notifications") {
        // 创建通知
        post {
            val request = call.receive<CreateNotificationRequest>()
            
            val notification = notificationService.createNotification(
                recipientId = UUID.fromString(request.recipientId),
                type = NotificationType.valueOf(request.type),
                title = request.title,
                content = request.content,
                metadata = request.metadata?.mapValues { it.value as Any },
                priority = request.priority?.let { NotificationPriority.valueOf(it) } ?: NotificationPriority.NORMAL
            )
            
            // 立即发送通知
            val success = notificationService.sendNotification(notification)
            
            call.respond(
                NotificationCreatedResponse(
                    id = notification.id.toString(),
                    status = if (success) "SENT" else "PENDING"
                )
            )
        }
        
        // 批量创建通知
        post("/batch") {
            val request = call.receive<BatchCreateNotificationRequest>()
            
            val results = mutableListOf<NotificationCreatedResponse>()
            var successCount = 0
            var failureCount = 0
            
            for (notificationRequest in request.notifications) {
                val notification = notificationService.createNotification(
                    recipientId = UUID.fromString(notificationRequest.recipientId),
                    type = NotificationType.valueOf(notificationRequest.type),
                    title = notificationRequest.title,
                    content = notificationRequest.content,
                    metadata = notificationRequest.metadata?.mapValues { it.value as Any },
                    priority = notificationRequest.priority?.let { NotificationPriority.valueOf(it) } ?: NotificationPriority.NORMAL
                )
                
                val success = notificationService.sendNotification(notification)
                
                results.add(
                    NotificationCreatedResponse(
                        id = notification.id.toString(),
                        status = if (success) "SENT" else "PENDING"
                    )
                )
                
                if (success) successCount++ else failureCount++
            }
            
            call.respond(
                BatchNotificationCreatedResponse(
                    notifications = results,
                    successCount = successCount,
                    failureCount = failureCount
                )
            )
        }
        
        // 从模板创建通知
        post("/from-template") {
            val request = call.receive<CreateNotificationFromTemplateRequest>()
            
            val notification = notificationService.createAndSendFromTemplate(
                templateName = request.templateName,
                recipientId = UUID.fromString(request.recipientId),
                variables = request.variables.mapValues { it.value as Any },
                priority = request.priority?.let { NotificationPriority.valueOf(it) } ?: NotificationPriority.NORMAL
            )
            
            if (notification != null) {
                call.respond(
                    NotificationCreatedResponse(
                        id = notification.id.toString(),
                        status = notification.status.name
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NotificationActionResponse(
                        success = false,
                        message = "无法创建通知，模板不存在或变量无效"
                    )
                )
            }
        }
        
        // 批量从模板创建通知
        post("/batch-from-template") {
            val request = call.receive<BatchCreateNotificationFromTemplateRequest>()
            
            val results = mutableListOf<NotificationCreatedResponse>()
            var successCount = 0
            var failureCount = 0
            
            for (recipient in request.recipients) {
                val notification = notificationService.createAndSendFromTemplate(
                    templateName = request.templateName,
                    recipientId = UUID.fromString(recipient.recipientId),
                    variables = recipient.variables.mapValues { it.value as Any },
                    priority = request.priority?.let { NotificationPriority.valueOf(it) } ?: NotificationPriority.NORMAL
                )
                
                if (notification != null) {
                    results.add(
                        NotificationCreatedResponse(
                            id = notification.id.toString(),
                            status = notification.status.name
                        )
                    )
                    successCount++
                } else {
                    failureCount++
                }
            }
            
            call.respond(
                BatchNotificationCreatedResponse(
                    notifications = results,
                    successCount = successCount,
                    failureCount = failureCount
                )
            )
        }
        
        // 获取通知
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val notification = notificationService.getNotification(UUID.fromString(id))
            
            if (notification != null) {
                call.respond(notification.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取用户的通知列表
        get("/user/{userId}") {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 20
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0
            val type = call.parameters["type"]?.let { NotificationType.valueOf(it) }
            val status = call.parameters["status"]?.let { NotificationStatus.valueOf(it) }
            
            val notifications = notificationService.getUserNotifications(
                userId = UUID.fromString(userId),
                limit = limit,
                offset = offset,
                type = type,
                status = status
            )
            
            val total = notificationService.getUnreadCount(UUID.fromString(userId))
            
            call.respond(
                NotificationListResponse(
                    notifications = notifications.map { it.toResponse() },
                    total = total,
                    page = offset / limit + 1,
                    pageSize = limit
                )
            )
        }
        
        // 获取用户未读通知数量
        get("/user/{userId}/count") {
            val userId = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val unreadCount = notificationService.getUnreadCount(UUID.fromString(userId))
            
            call.respond(
                NotificationCountResponse(
                    total = unreadCount,
                    unread = unreadCount
                )
            )
        }
        
        // 标记通知为已读
        put("/{id}/read") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = notificationService.markAsRead(UUID.fromString(id))
            
            call.respond(
                NotificationActionResponse(
                    success = success,
                    message = if (success) "通知已标记为已读" else "无法标记通知为已读",
                    affectedCount = if (success) 1 else 0
                )
            )
        }
        
        // 批量标记通知为已读
        put("/read") {
            val request = call.receive<MarkNotificationReadRequest>()
            
            val count = notificationService.markAsRead(request.notificationIds.map { UUID.fromString(it) })
            
            call.respond(
                NotificationActionResponse(
                    success = count > 0,
                    message = "$count 个通知已标记为已读",
                    affectedCount = count
                )
            )
        }
        
        // 标记用户所有通知为已读
        put("/user/{userId}/read-all") {
            val userId = call.parameters["userId"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val count = notificationService.markAllAsRead(UUID.fromString(userId))
            
            call.respond(
                NotificationActionResponse(
                    success = count > 0,
                    message = "$count 个通知已标记为已读",
                    affectedCount = count
                )
            )
        }
        
        // 删除通知
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            
            val success = notificationService.deleteNotification(UUID.fromString(id))
            
            call.respond(
                NotificationActionResponse(
                    success = success,
                    message = if (success) "通知已删除" else "无法删除通知",
                    affectedCount = if (success) 1 else 0
                )
            )
        }
        
        // 批量删除通知
        delete {
            val request = call.receive<DeleteNotificationRequest>()
            
            val count = notificationService.deleteNotifications(request.notificationIds.map { UUID.fromString(it) })
            
            call.respond(
                NotificationActionResponse(
                    success = count > 0,
                    message = "$count 个通知已删除",
                    affectedCount = count
                )
            )
        }
        
        // 删除用户所有通知
        delete("/user/{userId}") {
            val userId = call.parameters["userId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            
            val count = notificationService.deleteAllUserNotifications(UUID.fromString(userId))
            
            call.respond(
                NotificationActionResponse(
                    success = count > 0,
                    message = "$count 个通知已删除",
                    affectedCount = count
                )
            )
        }
        
        // 清理过期通知
        delete("/cleanup") {
            val days = call.parameters["days"]?.toIntOrNull() ?: 30
            val olderThan = Instant.now().minusSeconds(days * 24 * 60 * 60L)
            
            val count = notificationService.cleanupExpiredNotifications(olderThan)
            
            call.respond(
                NotificationActionResponse(
                    success = true,
                    message = "$count 个过期通知已清理",
                    affectedCount = count
                )
            )
        }
    }
} 