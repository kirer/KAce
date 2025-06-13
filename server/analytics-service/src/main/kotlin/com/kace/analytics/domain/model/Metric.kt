package com.kace.analytics.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 分析指标领域模型
 */
@Serializable
data class Metric(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val value: Double,
    val unit: String? = null,
    val timestamp: Instant,
    val dimensions: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList()
)

/**
 * 指标时间序列
 */
@Serializable
data class MetricTimeSeries(
    val name: String,
    val unit: String? = null,
    val dataPoints: List<MetricDataPoint> = emptyList(),
    val dimensions: Map<String, String> = emptyMap()
)

/**
 * 指标数据点
 */
@Serializable
data class MetricDataPoint(
    val timestamp: Instant,
    val value: Double
)

/**
 * 仪表盘指标摘要
 */
@Serializable
data class DashboardMetrics(
    val timestamp: Instant,
    val metrics: Map<String, Double> = emptyMap(),
    val comparisons: Map<String, MetricComparison> = emptyMap()
)

/**
 * 指标比较
 */
@Serializable
data class MetricComparison(
    val current: Double,
    val previous: Double,
    val percentChange: Double
) 