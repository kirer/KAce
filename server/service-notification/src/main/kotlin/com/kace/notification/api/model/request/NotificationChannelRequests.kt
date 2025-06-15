package com.kace.notification.api.model.request

import kotlinx.serialization.Serializable

/**
 * 创建通知渠道请求
 */
@Serializable
data class CreateChannelRequest(
    val name: String,
    val type: String,
    val provider: String,
    val config: Map<String, String>,
    val isDefault: Boolean = false,
    val isActive: Boolean = true
)

/**
 * 更新通知渠道请求
 */
@Serializable
data class UpdateChannelRequest(
    val name: String? = null,
    val config: Map<String, String>? = null,
    val isDefault: Boolean? = null,
    val isActive: Boolean? = null
)

/**
 * 测试通知渠道请求
 */
@Serializable
data class TestChannelRequest(
    val testPayload: String? = null
) 