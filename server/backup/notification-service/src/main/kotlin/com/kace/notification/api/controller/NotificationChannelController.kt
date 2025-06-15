package com.kace.notification.api.controller

import com.kace.notification.api.model.request.CreateChannelRequest
import com.kace.notification.api.model.request.TestChannelRequest
import com.kace.notification.api.model.request.UpdateChannelRequest
import com.kace.notification.api.model.response.ChannelActionResponse
import com.kace.notification.api.model.response.ChannelListResponse
import com.kace.notification.api.model.response.ChannelTestResponse
import com.kace.notification.api.model.response.toResponse
import com.kace.notification.domain.model.ChannelProvider
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.service.NotificationChannelService
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
 * 通知渠道控制器
 */
fun Route.channelRoutes() {
    val channelService: NotificationChannelService by inject()
    
    route("/channels") {
        // 创建通知渠道
        post {
            val request = call.receive<CreateChannelRequest>()
            
            val channel = channelService.createChannel(
                name = request.name,
                type = NotificationType.valueOf(request.type),
                provider = ChannelProvider.valueOf(request.provider),
                config = request.config,
                isDefault = request.isDefault,
                isActive = request.isActive
            )
            
            call.respond(channel.toResponse())
        }
        
        // 更新通知渠道
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<UpdateChannelRequest>()
            
            val updatedChannel = channelService.updateChannel(
                id = UUID.fromString(id),
                name = request.name,
                config = request.config,
                isDefault = request.isDefault,
                isActive = request.isActive
            )
            
            if (updatedChannel != null) {
                call.respond(updatedChannel.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取通知渠道
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val channel = channelService.getChannel(UUID.fromString(id))
            
            if (channel != null) {
                call.respond(channel.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取通知渠道（按名称）
        get("/name/{name}") {
            val name = call.parameters["name"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val channel = channelService.getChannelByName(name)
            
            if (channel != null) {
                call.respond(channel.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取通知渠道列表（按类型）
        get("/type/{type}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val channels = channelService.getChannelsByType(NotificationType.valueOf(type))
            
            call.respond(
                ChannelListResponse(
                    channels = channels.map { it.toResponse() }
                )
            )
        }
        
        // 获取默认通知渠道（按类型）
        get("/default/{type}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            
            val channel = channelService.getDefaultChannelForType(NotificationType.valueOf(type))
            
            if (channel != null) {
                call.respond(channel.toResponse())
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        
        // 获取所有活跃通知渠道
        get("/active") {
            val channels = channelService.getAllActiveChannels()
            
            call.respond(
                ChannelListResponse(
                    channels = channels.map { it.toResponse() }
                )
            )
        }
        
        // 获取所有通知渠道
        get {
            val channels = channelService.getAllChannels()
            
            call.respond(
                ChannelListResponse(
                    channels = channels.map { it.toResponse() }
                )
            )
        }
        
        // 设置为默认通知渠道
        put("/{id}/default") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = channelService.setAsDefault(UUID.fromString(id))
            
            call.respond(
                ChannelActionResponse(
                    success = success,
                    message = if (success) "已设置为默认通知渠道" else "无法设置为默认通知渠道"
                )
            )
        }
        
        // 激活通知渠道
        put("/{id}/activate") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = channelService.activateChannel(UUID.fromString(id))
            
            call.respond(
                ChannelActionResponse(
                    success = success,
                    message = if (success) "通知渠道已激活" else "无法激活通知渠道"
                )
            )
        }
        
        // 停用通知渠道
        put("/{id}/deactivate") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            
            val success = channelService.deactivateChannel(UUID.fromString(id))
            
            call.respond(
                ChannelActionResponse(
                    success = success,
                    message = if (success) "通知渠道已停用" else "无法停用通知渠道"
                )
            )
        }
        
        // 测试通知渠道
        post("/{id}/test") {
            val id = call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val request = call.receive<TestChannelRequest>()
            
            val success = channelService.testChannel(UUID.fromString(id), request.testPayload)
            
            call.respond(
                ChannelTestResponse(
                    success = success,
                    message = if (success) "通知渠道测试成功" else "通知渠道测试失败"
                )
            )
        }
        
        // 删除通知渠道
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            
            val success = channelService.deleteChannel(UUID.fromString(id))
            
            call.respond(
                ChannelActionResponse(
                    success = success,
                    message = if (success) "通知渠道已删除" else "无法删除通知渠道"
                )
            )
        }
    }
} 