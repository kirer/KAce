package com.kace.system.api.request

import com.kace.system.domain.model.MetricType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 记录单个指标请求
 */
@Serializable
data class MetricRecordRequest(
    val name: String,
    val type: MetricType,
    val value: Double,
    val unit: String = "",
    val serviceId: String = "system",
    val timestamp: Instant? = null,
    val tags: Map<String, String> = emptyMap()
)

/**
 * 批量记录指标请求
 */
@Serializable
data class BatchMetricRecordRequest(
    val metrics: List<MetricRecordRequest>
) 