package com.kace.analytics.api.model.request

import kotlinx.serialization.Serializable

/**
 * 创建报表请求
 */
@Serializable
data class CreateReportRequest(
    val name: String,
    val description: String? = null,
    val type: String,
    val query: String,
    val parameters: Map<String, String>? = null,
    val schedule: String? = null // cron表达式，如 "0 0 * * * ?" 表示每小时执行一次
)

/**
 * 更新报表请求
 */
@Serializable
data class UpdateReportRequest(
    val name: String? = null,
    val description: String? = null,
    val type: String? = null,
    val query: String? = null,
    val parameters: Map<String, String>? = null,
    val schedule: String? = null
)

/**
 * 查询报表请求
 */
@Serializable
data class QueryReportsRequest(
    val type: String? = null,
    val createdBy: String? = null,
    val limit: Int = 20,
    val offset: Int = 0
)

/**
 * 设置报表计划请求
 */
@Serializable
data class ScheduleReportRequest(
    val schedule: String // cron表达式
) 