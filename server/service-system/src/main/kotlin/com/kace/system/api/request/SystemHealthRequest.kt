package com.kace.system.api.request

import com.kace.system.domain.model.HealthStatus
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 系统健康状态更新请求
 */
@Serializable
data class SystemHealthUpdateRequest(
    val serviceId: String,
    val status: HealthStatus,
    val details: Map<String, String> = emptyMap(),
    val timestamp: Instant? = null
)

/**
 * 系统健康历史查询请求
 */
@Serializable
data class HealthHistoryRequest(
    val serviceId: String,
    val startTime: Instant,
    val endTime: Instant,
    val limit: Int = 100
) 