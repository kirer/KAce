package com.kace.analytics.infrastructure.persistence.repository

import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.repository.MetricRepository
import com.kace.analytics.infrastructure.persistence.entity.MetricEntity
import com.kace.analytics.infrastructure.persistence.entity.Metrics
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

/**
 * 指标仓库实现类
 */
class MetricRepositoryImpl : MetricRepository {

    override fun create(metric: Metric): Metric = transaction {
        val entity = MetricEntity.new {
            name = metric.name
            description = metric.description
            value = metric.value
            unit = metric.unit
            dimensions = metric.dimensions
            timestamp = metric.timestamp
            createdAt = Instant.now()
            updatedAt = Instant.now()
        }
        entity.toMetric()
    }

    override fun update(metric: Metric): Metric = transaction {
        val entity = MetricEntity.findById(metric.id) ?: throw IllegalArgumentException("Metric not found with id: ${metric.id}")
        entity.name = metric.name
        entity.description = metric.description
        entity.value = metric.value
        entity.unit = metric.unit
        entity.dimensions = metric.dimensions
        entity.timestamp = metric.timestamp
        entity.updatedAt = Instant.now()
        entity.toMetric()
    }

    override fun findById(id: UUID): Metric? = transaction {
        MetricEntity.findById(id)?.toMetric()
    }

    override fun findByName(name: String, limit: Int, offset: Int): List<Metric> = transaction {
        MetricEntity.find { Metrics.name eq name }
            .orderBy(Metrics.timestamp to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toMetric() }
    }

    override fun findByTimeRange(startTime: Instant, endTime: Instant, limit: Int, offset: Int): List<Metric> = transaction {
        MetricEntity.find { 
            (Metrics.timestamp greaterEq startTime) and (Metrics.timestamp lessEq endTime) 
        }
        .orderBy(Metrics.timestamp to SortOrder.DESC)
        .limit(limit, offset.toLong())
        .map { it.toMetric() }
    }

    override fun findAll(limit: Int, offset: Int): List<Metric> = transaction {
        MetricEntity.all()
            .orderBy(Metrics.timestamp to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toMetric() }
    }

    override fun countByName(name: String): Long = transaction {
        MetricEntity.find { Metrics.name eq name }.count()
    }

    override fun deleteById(id: UUID): Boolean = transaction {
        MetricEntity.findById(id)?.let {
            it.delete()
            true
        } ?: false
    }

    override fun deleteByTimeRange(startTime: Instant, endTime: Instant): Int = transaction {
        val entities = MetricEntity.find { 
            (Metrics.timestamp greaterEq startTime) and (Metrics.timestamp lessEq endTime) 
        }
        val count = entities.count().toInt()
        entities.forEach { it.delete() }
        count
    }
} 