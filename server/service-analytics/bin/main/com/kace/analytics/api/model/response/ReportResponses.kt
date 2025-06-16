package com.kace.analytics.api.model.response

import com.kace.analytics.domain.model.Report
import kotlinx.serialization.Serializable

/**
 * 报表响应
 */
@Serializable
data class ReportResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val type: String,
    val query: String,
    val parameters: Map<String, String>? = null,
    val schedule: String? = null,
    val lastRunAt: String? = null,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        /**
         * 从领域模型转换为响应模型
         */
        fun fromReport(report: Report): ReportResponse {
            return ReportResponse(
                id = report.id.toString(),
                name = report.name,
                description = report.description,
                type = report.type,
                query = report.query,
                parameters = report.parameters?.mapValues { it.value.toString() },
                schedule = report.schedule,
                lastRunAt = report.lastRunAt?.toString(),
                createdBy = report.createdBy.toString(),
                createdAt = report.createdAt.toString(),
                updatedAt = report.updatedAt.toString()
            )
        }
    }
}

/**
 * 报表列表响应
 */
@Serializable
data class ReportListResponse(
    val reports: List<ReportResponse>,
    val total: Long,
    val limit: Int,
    val offset: Int
)

/**
 * 报表执行结果响应
 */
@Serializable
data class ReportExecutionResponse(
    val reportId: String,
    val reportName: String,
    val executionTime: String,
    val data: Any
) 