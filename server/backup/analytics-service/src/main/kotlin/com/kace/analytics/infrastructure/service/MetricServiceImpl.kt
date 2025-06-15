package com.kace.analytics.infrastructure.service

import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.repository.MetricRepository
import com.kace.analytics.domain.service.MetricService
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 指标服务实现类
 */
class MetricServiceImpl(private val metricRepository: MetricRepository) : MetricService {
    
    private val logger = LoggerFactory.getLogger(MetricServiceImpl::class.java)
    
    override fun createMetric(metric: Metric): Metric {
        logger.info("Creating metric: ${metric.name}")
        return metricRepository.create(metric)
    }
    
    override fun updateMetric(metric: Metric): Metric {
        logger.info("Updating metric: ${metric.id} - ${metric.name}")
        return metricRepository.update(metric)
    }
    
    override fun getMetric(id: UUID): Metric? {
        logger.debug("Getting metric with id: $id")
        return metricRepository.findById(id)
    }
    
    override fun getMetricsByName(name: String, limit: Int, offset: Int): List<Metric> {
        logger.debug("Getting metrics by name: $name, limit: $limit, offset: $offset")
        return metricRepository.findByName(name, limit, offset)
    }
    
    override fun getMetricsByTimeRange(startTime: Instant, endTime: Instant, limit: Int, offset: Int): List<Metric> {
        logger.debug("Getting metrics by time range: $startTime - $endTime, limit: $limit, offset: $offset")
        return metricRepository.findByTimeRange(startTime, endTime, limit, offset)
    }
    
    override fun getAllMetrics(limit: Int, offset: Int): List<Metric> {
        logger.debug("Getting all metrics, limit: $limit, offset: $offset")
        return metricRepository.findAll(limit, offset)
    }
    
    override fun countMetricsByName(name: String): Long {
        logger.debug("Counting metrics by name: $name")
        return metricRepository.countByName(name)
    }
    
    override fun deleteMetric(id: UUID): Boolean {
        logger.info("Deleting metric with id: $id")
        return metricRepository.deleteById(id)
    }
    
    override fun deleteMetricsByTimeRange(startTime: Instant, endTime: Instant): Int {
        logger.info("Deleting metrics by time range: $startTime - $endTime")
        return metricRepository.deleteByTimeRange(startTime, endTime)
    }
    
    override fun calculateAggregatedMetric(name: String, startTime: Instant, endTime: Instant, aggregationType: String): Metric {
        logger.info("Calculating aggregated metric: $name, type: $aggregationType, time range: $startTime - $endTime")
        
        val metrics = metricRepository.findByName(name, 1000, 0)
            .filter { it.timestamp in startTime..endTime }
        
        if (metrics.isEmpty()) {
            throw IllegalArgumentException("No metrics found for name $name in the specified time range")
        }
        
        val value = when (aggregationType.lowercase()) {
            "sum" -> metrics.sumOf { it.value }
            "avg", "average" -> metrics.map { it.value }.average()
            "min" -> metrics.minOf { it.value }
            "max" -> metrics.maxOf { it.value }
            "count" -> metrics.size.toDouble()
            else -> throw IllegalArgumentException("Unsupported aggregation type: $aggregationType")
        }
        
        return Metric(
            id = UUID.randomUUID(),
            name = "$name:$aggregationType",
            description = "Aggregated $aggregationType for $name from $startTime to $endTime",
            value = value,
            unit = metrics.firstOrNull()?.unit,
            dimensions = mapOf("aggregationType" to aggregationType),
            timestamp = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
} 