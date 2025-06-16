package com.kace.analytics.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 指标领域模型
 * 表示系统中的各种指标，如页面访问量、用户活跃度、内容点击率等
 */
data class Metric(
    val id: UUID? = null,
    val name: String,           // 指标名称，如 "page_views", "active_users", "click_through_rate" 等
    val value: Double,          // 指标值
    val unit: String? = null,   // 单位，如 "count", "percentage", "seconds" 等
    val timestamp: Instant = Instant.now(), // 指标记录时间
    val dimensions: Map<String, String> = emptyMap(), // 指标维度，如 {"page": "home", "device": "mobile"}
    val tags: List<String> = emptyList() // 标签，用于分类和过滤
)

/**
 * 指标聚合类型
 */
enum class AggregationType {
    SUM,        // 求和
    AVERAGE,    // 平均值
    MIN,        // 最小值
    MAX,        // 最大值
    COUNT,      // 计数
    PERCENTILE  // 百分位数
}

/**
 * 指标时间粒度
 */
enum class TimeGranularity {
    MINUTE,     // 分钟级
    HOUR,       // 小时级
    DAY,        // 天级
    WEEK,       // 周级
    MONTH,      // 月级
    QUARTER,    // 季度级
    YEAR        // 年级
}

/**
 * 指标查询过滤器
 */
data class MetricFilter(
    val names: List<String>? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val dimensions: Map<String, String>? = null,
    val tags: List<String>? = null,
    val aggregation: AggregationType = AggregationType.SUM,
    val granularity: TimeGranularity? = null
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