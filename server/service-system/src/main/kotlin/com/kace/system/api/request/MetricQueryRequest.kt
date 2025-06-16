package com.kace.system.api.request

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 指标查询请求
 */
@Serializable
data class MetricQueryRequest(
    val name: String,
    val serviceId: String,
    val startTime: Instant,
    val endTime: Instant,
    val limit: Int = 100
)

/**
 * 指标统计请求
 */
@Serializable
data class MetricStatisticsRequest(
    val name: String,
    val serviceId: String,
    val startTime: Instant,
    val endTime: Instant
) 