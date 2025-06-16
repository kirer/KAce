package com.kace.system.api.response

import com.kace.system.domain.model.MetricType
import com.kace.system.domain.model.SystemMetric
import com.kace.system.domain.repository.MetricStatistics
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 系统指标响应
 */
@Serializable
data class SystemMetricResponse(
    val id: Long?,
    val name: String,
    val type: MetricType,
    val value: Double,
    val unit: String,
    val serviceId: String,
    val timestamp: Instant,
    val tags: Map<String, String>
) {
    companion object {
        fun fromDomain(metric: SystemMetric): SystemMetricResponse {
            return SystemMetricResponse(
                id = metric.id,
                name = metric.name,
                type = metric.type,
                value = metric.value,
                unit = metric.unit,
                serviceId = metric.serviceId,
                timestamp = metric.timestamp,
                tags = metric.tags
            )
        }
    }
}

/**
 * 系统指标统计响应
 */
@Serializable
data class MetricStatisticsResponse(
    val min: Double,
    val max: Double,
    val avg: Double,
    val sum: Double,
    val count: Long,
    val name: String,
    val serviceId: String,
    val startTime: Instant,
    val endTime: Instant
) {
    companion object {
        fun fromStatistics(
            statistics: MetricStatistics,
            name: String,
            serviceId: String,
            startTime: Instant,
            endTime: Instant
        ): MetricStatisticsResponse {
            return MetricStatisticsResponse(
                min = statistics.min,
                max = statistics.max,
                avg = statistics.avg,
                sum = statistics.sum,
                count = statistics.count,
                name = name,
                serviceId = serviceId,
                startTime = startTime,
                endTime = endTime
            )
        }
    }
} 