package com.kace.analytics.domain.model

import java.time.Instant
import java.util.UUID

/**
 * 报表领域模型
 * 表示系统中的各种报表，用于数据可视化和分析
 */
data class Report(
    val id: UUID? = null,
    val name: String,               // 报表名称
    val description: String? = null, // 报表描述
    val createdBy: UUID,            // 创建者ID
    val createdAt: Instant? = null, // 创建时间
    val updatedAt: Instant? = null, // 更新时间
    val schedule: ReportSchedule? = null, // 报表计划执行时间
    val parameters: ReportParameters, // 报表参数
    val visualizations: List<Visualization> = emptyList() // 可视化配置
)

/**
 * 报表计划执行时间
 */
data class ReportSchedule(
    val enabled: Boolean = false,  // 是否启用计划
    val cronExpression: String? = null, // Cron表达式
    val recipients: List<String> = emptyList(), // 接收者邮箱
    val format: ReportFormat = ReportFormat.PDF // 报表格式
)

/**
 * 报表格式
 */
enum class ReportFormat {
    PDF,
    EXCEL,
    CSV,
    HTML
}

/**
 * 报表参数
 */
data class ReportParameters(
    val dataSource: DataSource, // 数据源
    val filters: List<Filter> = emptyList(), // 过滤条件
    val timeRange: TimeRange? = null, // 时间范围
    val groupBy: List<String> = emptyList(), // 分组字段
    val orderBy: List<OrderBy> = emptyList(), // 排序字段
    val limit: Int? = null // 结果限制
)

/**
 * 数据源
 */
data class DataSource(
    val type: DataSourceType, // 数据源类型
    val query: String? = null, // SQL查询或其他查询语句
    val metrics: List<String>? = null, // 指标名称列表
    val events: List<String>? = null // 事件名称列表
)

/**
 * 数据源类型
 */
enum class DataSourceType {
    EVENTS,     // 事件数据
    METRICS,    // 指标数据
    SQL,        // SQL查询
    API         // API数据
}

/**
 * 过滤条件
 */
data class Filter(
    val field: String, // 字段名
    val operator: FilterOperator, // 操作符
    val value: Any // 值
)

/**
 * 过滤操作符
 */
enum class FilterOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUALS,
    LESS_THAN_OR_EQUALS,
    CONTAINS,
    NOT_CONTAINS,
    IN,
    NOT_IN,
    BETWEEN
}

/**
 * 时间范围
 */
data class TimeRange(
    val start: Instant? = null, // 开始时间
    val end: Instant? = null, // 结束时间
    val preset: TimeRangePreset? = null // 预设时间范围
)

/**
 * 预设时间范围
 */
enum class TimeRangePreset {
    TODAY,
    YESTERDAY,
    LAST_7_DAYS,
    LAST_30_DAYS,
    THIS_MONTH,
    LAST_MONTH,
    THIS_QUARTER,
    LAST_QUARTER,
    THIS_YEAR,
    LAST_YEAR,
    CUSTOM
}

/**
 * 排序字段
 */
data class OrderBy(
    val field: String, // 字段名
    val direction: SortDirection // 排序方向
)

/**
 * 排序方向
 */
enum class SortDirection {
    ASC,
    DESC
}

/**
 * 可视化配置
 */
data class Visualization(
    val id: UUID? = null,
    val type: VisualizationType, // 可视化类型
    val title: String, // 标题
    val description: String? = null, // 描述
    val config: Map<String, Any> = emptyMap() // 配置参数
)

/**
 * 可视化类型
 */
enum class VisualizationType {
    TABLE,      // 表格
    LINE_CHART, // 折线图
    BAR_CHART,  // 柱状图
    PIE_CHART,  // 饼图
    AREA_CHART, // 面积图
    SCATTER_PLOT, // 散点图
    HEATMAP,    // 热力图
    GAUGE,      // 仪表盘
    COUNTER,    // 计数器
    MAP         // 地图
} 