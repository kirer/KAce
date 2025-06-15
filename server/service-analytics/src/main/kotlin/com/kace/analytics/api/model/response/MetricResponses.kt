package com.kace.analytics.api.model.response

import com.kace.analytics.domain.model.Metric
import kotlinx.serialization.Serializable

/**
 * 指标响应
 */
@Serializable
data class MetricResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val value: Double,
    val unit: String? = null,
    val dimensions: Map<String, String>? = null,
    val timestamp: String,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        /**
         * 从领域模型转换为响应模型
         */
        fun fromMetric(metric: Metric): MetricResponse {
            return MetricResponse(
                id = metric.id.toString(),
                name = metric.name,
                description = metric.description,
                value = metric.value,
                unit = metric.unit,
                dimensions = metric.dimensions,
                timestamp = metric.timestamp.toString(),
                createdAt = metric.createdAt.toString(),
                updatedAt = metric.updatedAt.toString()
            )
        }
    }
}

/**
 * 指标列表响应
 */
@Serializable
data class MetricListResponse(
    val metrics: List<MetricResponse>,
    val total: Long,
    val limit: Int,
    val offset: Int
)

/**
 * 聚合指标响应
 */
@Serializable
data class AggregateMetricResponse(
    val name: String,
    val value: Double,
    val unit: String? = null,
    val aggregationType: String,
    val startTime: String,
    val endTime: String,
    val timestamp: String
) 