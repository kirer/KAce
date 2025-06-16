package com.kace.system.api.response

import com.kace.system.domain.model.HealthStatus
import com.kace.system.domain.model.SystemHealth
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 系统健康状态响应
 */
@Serializable
data class SystemHealthResponse(
    val id: Long?,
    val serviceId: String,
    val status: HealthStatus,
    val details: Map<String, String>,
    val timestamp: Instant
) {
    companion object {
        fun fromDomain(health: SystemHealth): SystemHealthResponse {
            return SystemHealthResponse(
                id = health.id,
                serviceId = health.serviceId,
                status = health.status,
                details = health.details,
                timestamp = health.timestamp
            )
        }
    }
}

/**
 * 系统健康状态概览响应
 */
@Serializable
data class HealthOverviewResponse(
    val totalServices: Int,
    val upServices: Int,
    val degradedServices: Int,
    val downServices: Int,
    val unknownServices: Int,
    val services: List<SystemHealthResponse>,
    val timestamp: Instant
) {
    companion object {
        fun fromHealthList(healthList: List<SystemHealth>): HealthOverviewResponse {
            val upCount = healthList.count { it.status == HealthStatus.UP }
            val degradedCount = healthList.count { it.status == HealthStatus.DEGRADED }
            val downCount = healthList.count { it.status == HealthStatus.DOWN }
            val unknownCount = healthList.count { it.status == HealthStatus.UNKNOWN }
            
            return HealthOverviewResponse(
                totalServices = healthList.size,
                upServices = upCount,
                degradedServices = degradedCount,
                downServices = downCount,
                unknownServices = unknownCount,
                services = healthList.map { SystemHealthResponse.fromDomain(it) },
                timestamp = Instant.fromEpochSeconds(System.currentTimeMillis() / 1000)
            )
        }
    }
} 