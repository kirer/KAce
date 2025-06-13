package com.kace.analytics.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 分析报表领域模型
 */
@Serializable
data class Report(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val createdBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val schedule: ReportSchedule? = null,
    val parameters: ReportParameters,
    val visualizations: List<ReportVisualization> = emptyList()
)

/**
 * 报表参数
 */
@Serializable
data class ReportParameters(
    val startDate: Instant,
    val endDate: Instant,
    val filters: Map<String, String> = emptyMap(),
    val groupBy: List<String> = emptyList(),
    val metrics: List<String> = emptyList()
)

/**
 * 报表可视化
 */
@Serializable
data class ReportVisualization(
    val id: String = UUID.randomUUID().toString(),
    val type: VisualizationType,
    val title: String,
    val description: String? = null,
    val config: Map<String, String> = emptyMap(),
    val data: ReportData? = null
)

/**
 * 报表数据
 */
@Serializable
data class ReportData(
    val columns: List<String>,
    val rows: List<List<String>>
)

/**
 * 可视化类型
 */
@Serializable
enum class VisualizationType {
    LINE_CHART,
    BAR_CHART,
    PIE_CHART,
    TABLE,
    NUMBER,
    GAUGE
}

/**
 * 报表计划
 */
@Serializable
data class ReportSchedule(
    val frequency: ScheduleFrequency,
    val dayOfWeek: Int? = null,
    val dayOfMonth: Int? = null,
    val hour: Int = 0,
    val minute: Int = 0,
    val recipients: List<String> = emptyList(),
    val format: ReportFormat = ReportFormat.PDF
)

/**
 * 计划频率
 */
@Serializable
enum class ScheduleFrequency {
    DAILY,
    WEEKLY,
    MONTHLY
}

/**
 * 报表格式
 */
@Serializable
enum class ReportFormat {
    PDF,
    CSV,
    EXCEL,
    HTML
} 