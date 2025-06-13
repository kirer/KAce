package com.kace.analytics.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 分析事件领域模型
 */
@Serializable
data class Event(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val name: String,
    val userId: String? = null,
    val sessionId: String? = null,
    val properties: Map<String, String> = emptyMap(),
    val timestamp: Instant,
    val appVersion: String? = null,
    val deviceInfo: DeviceInfo? = null,
    val source: String? = null
)

/**
 * 设备信息
 */
@Serializable
data class DeviceInfo(
    val type: DeviceType,
    val os: String? = null,
    val osVersion: String? = null,
    val browser: String? = null,
    val browserVersion: String? = null,
    val screenSize: String? = null,
    val locale: String? = null
)

/**
 * 设备类型
 */
@Serializable
enum class DeviceType {
    DESKTOP,
    MOBILE,
    TABLET,
    TV,
    OTHER
} 