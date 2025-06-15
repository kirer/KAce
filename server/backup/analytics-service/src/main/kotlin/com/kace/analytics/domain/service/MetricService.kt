package com.kace.analytics.domain.service

import com.kace.analytics.domain.model.AggregationType
import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.model.MetricFilter
import com.kace.analytics.domain.model.TimeGranularity
import java.time.Instant
import java.util.UUID

/**
 * 指标服务接口
 */
interface MetricService {
    
    /**
     * 记录指标
     */
    suspend fun recordMetric(metric: Metric): Metric
    
    /**
     * 批量记录指标
     */
    suspend fun recordMetrics(metrics: List<Metric>): List<Metric>
    
    /**
     * 获取指标
     */
    suspend fun getMetric(id: UUID): Metric?
    
    /**
     * 查询指标
     */
    suspend fun queryMetrics(filter: MetricFilter, page: Int, size: Int): List<Metric>
    
    /**
     * 获取指标数量
     */
    suspend fun countMetrics(filter: MetricFilter? = null): Long
    
    /**
     * 聚合指标数据
     */
    suspend fun aggregateMetrics(
        filter: MetricFilter,
        aggregationType: AggregationType = AggregationType.SUM,
        granularity: TimeGranularity? = null
    ): List<Map<String, Any>>
    
    /**
     * 获取指标统计信息
     */
    suspend fun getMetricStats(filter: MetricFilter): Map<String, Double>
    
    /**
     * 获取指标时间序列
     */
    suspend fun getMetricTimeSeries(
        name: String,
        dimensions: Map<String, String>? = null,
        startTime: Instant,
        endTime: Instant,
        granularity: TimeGranularity = TimeGranularity.HOUR
    ): List<Map<String, Any>>
    
    /**
     * 获取仪表盘指标
     */
    suspend fun getDashboardMetrics(
        names: List<String>,
        dimensions: Map<String, String>? = null,
        currentStartTime: Instant,
        currentEndTime: Instant,
        previousStartTime: Instant? = null,
        previousEndTime: Instant? = null
    ): Map<String, Map<String, Any>>
    
    /**
     * 清理旧指标数据
     */
    suspend fun cleanupOldMetrics(retentionDays: Int): Int
} 