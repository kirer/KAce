package com.kace.analytics.infrastructure.service

import com.kace.analytics.domain.model.AggregationType
import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.model.MetricFilter
import com.kace.analytics.domain.model.TimeGranularity
import com.kace.analytics.domain.repository.MetricRepository
import com.kace.analytics.domain.service.MetricService
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * 指标服务实现类
 */
class MetricServiceImpl(private val metricRepository: MetricRepository) : MetricService {
    
    private val logger = LoggerFactory.getLogger(MetricServiceImpl::class.java)
    
    override suspend fun recordMetric(metric: Metric): Metric {
        logger.info("Recording metric: ${metric.name}")
        return metricRepository.save(metric)
    }
    
    override suspend fun recordMetrics(metrics: List<Metric>): List<Metric> {
        logger.info("Recording ${metrics.size} metrics")
        return metricRepository.saveAll(metrics)
    }
    
    override suspend fun getMetric(id: UUID): Metric? {
        logger.debug("Getting metric with id: $id")
        return metricRepository.findById(id)
    }
    
    override suspend fun queryMetrics(filter: MetricFilter, page: Int, size: Int): List<Metric> {
        logger.debug("Querying metrics with filter: $filter, page: $page, size: $size")
        return metricRepository.findByFilter(filter, page, size)
    }
    
    override suspend fun countMetrics(filter: MetricFilter?): Long {
        logger.debug("Counting metrics with filter: $filter")
        return metricRepository.count(filter)
    }
    
    override suspend fun aggregateMetrics(
        filter: MetricFilter,
        aggregationType: AggregationType,
        granularity: TimeGranularity?
    ): List<Map<String, Any>> {
        logger.debug("Aggregating metrics with filter: $filter, type: $aggregationType, granularity: $granularity")
        
        // If granularity is provided, perform time-based aggregation
        if (granularity != null) {
            return metricRepository.aggregateByTime(filter, aggregationType, granularity)
        }
        
        // Otherwise, perform dimension-based aggregation
        return metricRepository.aggregateByDimensions(filter, aggregationType)
    }
    
    override suspend fun getMetricStats(filter: MetricFilter): Map<String, Double> {
        logger.debug("Getting metric stats for filter: $filter")
        
        val metrics = metricRepository.findByFilter(filter, 1, 1000)
        
        if (metrics.isEmpty()) {
            return mapOf(
                "count" to 0.0,
                "sum" to 0.0,
                "avg" to 0.0,
                "min" to 0.0,
                "max" to 0.0
            )
        }
        
        val values = metrics.map { it.value }
        
        return mapOf(
            "count" to values.size.toDouble(),
            "sum" to values.sum(),
            "avg" to values.average(),
            "min" to values.minOrNull() ?: 0.0,
            "max" to values.maxOrNull() ?: 0.0
        )
    }
    
    override suspend fun getMetricTimeSeries(
        name: String,
        dimensions: Map<String, String>?,
        startTime: Instant,
        endTime: Instant,
        granularity: TimeGranularity
    ): List<Map<String, Any>> {
        logger.debug("Getting metric time series for name: $name, dimensions: $dimensions, timeRange: $startTime - $endTime, granularity: $granularity")
        
        val filter = MetricFilter(
            names = listOf(name),
            startTime = startTime,
            endTime = endTime,
            dimensions = dimensions
        )
        
        return metricRepository.getTimeSeries(filter, granularity)
    }
    
    override suspend fun getDashboardMetrics(
        names: List<String>,
        dimensions: Map<String, String>?,
        currentStartTime: Instant,
        currentEndTime: Instant,
        previousStartTime: Instant?,
        previousEndTime: Instant?
    ): Map<String, Map<String, Any>> {
        logger.debug("Getting dashboard metrics for names: $names, dimensions: $dimensions, currentTimeRange: $currentStartTime - $currentEndTime")
        
        val result = mutableMapOf<String, Map<String, Any>>()
        
        // Process each metric name
        names.forEach { name ->
            // Get current metrics
            val currentFilter = MetricFilter(
                names = listOf(name),
                startTime = currentStartTime,
                endTime = currentEndTime,
                dimensions = dimensions
            )
            
            val currentMetrics = metricRepository.findByFilter(currentFilter, 1, 1000)
            
            // Calculate current value
            val currentValue = if (currentMetrics.isEmpty()) 0.0 else {
                currentMetrics.map { it.value }.average()
            }
            
            // If previous time range is specified, calculate comparison
            val metricData = if (previousStartTime != null && previousEndTime != null) {
                val previousFilter = MetricFilter(
                    names = listOf(name),
                    startTime = previousStartTime,
                    endTime = previousEndTime,
                    dimensions = dimensions
                )
                
                val previousMetrics = metricRepository.findByFilter(previousFilter, 1, 1000)
                val previousValue = if (previousMetrics.isEmpty()) 0.0 else {
                    previousMetrics.map { it.value }.average()
                }
                
                val change = if (previousValue == 0.0) 0.0 else {
                    ((currentValue - previousValue) / previousValue) * 100.0
                }
                
                mapOf(
                    "current" to currentValue,
                    "previous" to previousValue,
                    "change" to change
                )
            } else {
                mapOf("current" to currentValue)
            }
            
            result[name] = metricData
        }
        
        return result
    }
    
    override suspend fun cleanupOldMetrics(retentionDays: Int): Int {
        val cutoffDate = Instant.now().minus(retentionDays.toLong(), ChronoUnit.DAYS)
        logger.info("Cleaning up metrics older than: $cutoffDate (retention: $retentionDays days)")
        return metricRepository.deleteBeforeTime(cutoffDate)
    }
} 