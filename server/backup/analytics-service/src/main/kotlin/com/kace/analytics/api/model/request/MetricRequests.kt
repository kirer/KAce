package com.kace.analytics.api.model.request

import kotlinx.serialization.Serializable

/**
 * 创建指标请求
 */
@Serializable
data class CreateMetricRequest(
    val name: String,
    val description: String? = null,
    val value: Double,
    val unit: String? = null,
    val dimensions: Map<String, String>? = null,
    val timestamp: String? = null // ISO-8601格式的时间字符串，不提供则使用当前时间
)

/**
 * 更新指标请求
 */
@Serializable
data class UpdateMetricRequest(
    val name: String? = null,
    val description: String? = null,
    val value: Double? = null,
    val unit: String? = null,
    val dimensions: Map<String, String>? = null
)

/**
 * 查询指标请求
 */
@Serializable
data class QueryMetricsRequest(
    val name: String? = null,
    val startTime: String? = null, // ISO-8601格式的时间字符串
    val endTime: String? = null, // ISO-8601格式的时间字符串
    val limit: Int = 20,
    val offset: Int = 0
)

/**
 * 聚合指标请求
 */
@Serializable
data class AggregateMetricRequest(
    val name: String,
    val startTime: String, // ISO-8601格式的时间字符串
    val endTime: String, // ISO-8601格式的时间字符串
    val aggregationType: String // sum, avg, min, max, count
) 