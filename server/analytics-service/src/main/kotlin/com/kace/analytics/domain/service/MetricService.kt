package com.kace.analytics.domain.service

import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.model.MetricTimeSeries
import com.kace.analytics.domain.model.DashboardMetrics
import com.kace.analytics.domain.model.MetricComparison
import com.kace.analytics.domain.repository.MetricRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/**
 * 指标服务
 */
class MetricService(private val metricRepository: MetricRepository) {
    
    /**
     * 记录指标
     */
    suspend fun recordMetric(metric: Metric): Metric {
        return metricRepository.save(metric)
    }
    
    /**
     * 批量记录指标
     */
    suspend fun recordMetrics(metrics: List<Metric>): List<Metric> {
        return metricRepository.saveAll(metrics)
    }
    
    /**
     * 获取指标
     */
    suspend fun getMetric(id: String): Metric? {
        return metricRepository.findById(id)
    }
    
    /**
     * 获取指标时间序列
     */
    suspend fun getMetricTimeSeries(
        name: String,
        dimensions: Map<String, String> = emptyMap(),
        startTime: Instant? = null,
        endTime: Instant? = null,
        interval: String = "1h"
    ): MetricTimeSeries {
        val now = Clock.System.now()
        val actualEndTime = endTime ?: now
        val actualStartTime = startTime ?: (now - 24.hours)
        
        return metricRepository.getTimeSeries(
            name = name,
            dimensions = dimensions,
            startTime = actualStartTime,
            endTime = actualEndTime,
            interval = interval
        )
    }
    
    /**
     * 获取仪表盘指标
     */
    suspend fun getDashboardMetrics(
        names: List<String>,
        dimensions: Map<String, String> = emptyMap(),
        days: Int = 1
    ): DashboardMetrics {
        val now = Clock.System.now()
        val currentEndTime = now
        val currentStartTime = now - days.days
        val previousEndTime = currentStartTime
        val previousStartTime = previousEndTime - days.days
        
        val metricsData = metricRepository.getDashboardMetrics(
            names = names,
            dimensions = dimensions,
            currentStartTime = currentStartTime,
            currentEndTime = currentEndTime,
            previousStartTime = previousStartTime,
            previousEndTime = previousEndTime
        )
        
        val metrics = mutableMapOf<String, Double>()
        val comparisons = mutableMapOf<String, MetricComparison>()
        
        metricsData.forEach { (name, pair) ->
            val (current, previous) = pair
            metrics[name] = current
            
            val percentChange = if (previous != 0.0) {
                ((current - previous) / previous) * 100.0
            } else {
                if (current > 0) 100.0 else 0.0
            }
            
            comparisons[name] = MetricComparison(
                current = current,
                previous = previous,
                percentChange = percentChange
            )
        }
        
        return DashboardMetrics(
            timestamp = now,
            metrics = metrics,
            comparisons = comparisons
        )
    }
    
    /**
     * 清理旧数据
     */
    suspend fun cleanupOldMetrics(retentionDays: Int): Int {
        val cutoffTime = Clock.System.now() - retentionDays.days
        return metricRepository.deleteOlderThan(cutoffTime)
    }
} 