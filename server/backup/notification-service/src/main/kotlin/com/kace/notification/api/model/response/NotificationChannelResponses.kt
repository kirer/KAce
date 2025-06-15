package com.kace.notification.api.model.response

import com.kace.notification.domain.model.NotificationChannel
import kotlinx.serialization.Serializable

/**
 * 通知渠道响应
 */
@Serializable
data class ChannelResponse(
    val id: String,
    val name: String,
    val type: String,
    val provider: String,
    val config: Map<String, String>,
    val isDefault: Boolean,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * 通知渠道列表响应
 */
@Serializable
data class ChannelListResponse(
    val channels: List<ChannelResponse>
)

/**
 * 通知渠道操作响应
 */
@Serializable
data class ChannelActionResponse(
    val success: Boolean,
    val message: String? = null
)

/**
 * 通知渠道测试响应
 */
@Serializable
data class ChannelTestResponse(
    val success: Boolean,
    val message: String? = null
)

/**
 * 将领域模型转换为响应模型
 */
fun NotificationChannel.toResponse(): ChannelResponse {
    return ChannelResponse(
        id = id.toString(),
        name = name,
        type = type.name,
        provider = provider.name,
        config = config.mapValues { it.value }, // 敏感信息可能需要过滤
        isDefault = isDefault,
        isActive = isActive,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
} 