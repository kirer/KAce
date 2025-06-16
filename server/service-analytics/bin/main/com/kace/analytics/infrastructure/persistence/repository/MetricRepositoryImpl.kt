package com.kace.analytics.infrastructure.persistence.repository

import com.kace.analytics.domain.model.AggregationType
import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.model.MetricFilter
import com.kace.analytics.domain.model.TimeGranularity
import com.kace.analytics.domain.repository.MetricRepository
import com.kace.analytics.infrastructure.persistence.entity.MetricEntity
import com.kace.analytics.infrastructure.persistence.entity.Metrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.hour
import org.jetbrains.exposed.sql.javatime.month
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * 指标仓库实现类
 */
class MetricRepositoryImpl : MetricRepository {

    override suspend fun save(metric: Metric): Metric = withContext(Dispatchers.IO) {
        transaction {
            val entity = MetricEntity.new(metric.id) {
                name = metric.name
                value = metric.value
                unit = metric.unit
                dimensions = metric.dimensions
                tags = metric.tags?.toTypedArray() ?: emptyArray()
                timestamp = metric.timestamp
            }
            entity.toMetric()
        }
    }

    override suspend fun saveAll(metrics: List<Metric>): List<Metric> = withContext(Dispatchers.IO) {
        transaction {
            metrics.map { metric ->
                val entity = MetricEntity.new(metric.id) {
                    name = metric.name
                    value = metric.value
                    unit = metric.unit
                    dimensions = metric.dimensions
                    tags = metric.tags?.toTypedArray() ?: emptyArray()
                    timestamp = metric.timestamp
                }
                entity.toMetric()
            }
        }
    }

    override suspend fun findById(id: UUID): Metric? = withContext(Dispatchers.IO) {
        transaction {
            MetricEntity.findById(id)?.toMetric()
        }
    }

    override suspend fun findByFilter(filter: MetricFilter, page: Int, size: Int): List<Metric> = withContext(Dispatchers.IO) {
        transaction {
            val query = MetricEntity.find {
                buildFilterQuery(filter)
            }
            
            query.orderBy(Metrics.timestamp to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toMetric() }
        }
    }

    override suspend fun findByName(name: String, page: Int, size: Int): List<Metric> = withContext(Dispatchers.IO) {
        transaction {
            MetricEntity.find { Metrics.name eq name }
                .orderBy(Metrics.timestamp to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toMetric() }
        }
    }

    override suspend fun findByTimeRange(startTime: Instant, endTime: Instant, page: Int, size: Int): List<Metric> = withContext(Dispatchers.IO) {
        transaction {
            MetricEntity.find { 
                (Metrics.timestamp greaterEq startTime) and (Metrics.timestamp lessEq endTime) 
            }
            .orderBy(Metrics.timestamp to SortOrder.DESC)
            .limit(size, ((page - 1) * size).toLong())
            .map { it.toMetric() }
        }
    }

    override suspend fun findByDimension(dimension: String, value: String, page: Int, size: Int): List<Metric> = withContext(Dispatchers.IO) {
        transaction {
            // This is a simplified implementation - for a real database, you'd need to use JSON operators to query dimensions
            MetricEntity.all()
                .filter { entity -> 
                    entity.dimensions[dimension] == value
                }
                .sortedByDescending { it.timestamp }
                .drop((page - 1) * size)
                .take(size)
                .map { it.toMetric() }
        }
    }

    override suspend fun findByTag(tag: String, page: Int, size: Int): List<Metric> = withContext(Dispatchers.IO) {
        transaction {
            // This is a simplified implementation - for a real database, you'd need to use array operators to query tags
            MetricEntity.all()
                .filter { entity ->
                    tag in entity.tags
                }
                .sortedByDescending { it.timestamp }
                .drop((page - 1) * size)
                .take(size)
                .map { it.toMetric() }
        }
    }

    override suspend fun count(filter: MetricFilter?): Long = withContext(Dispatchers.IO) {
        transaction {
            if (filter == null) {
                MetricEntity.count()
            } else {
                MetricEntity.find { buildFilterQuery(filter) }.count()
            }
        }
    }

    override suspend fun deleteBeforeTime(time: Instant): Int = withContext(Dispatchers.IO) {
        transaction {
            val entities = MetricEntity.find { Metrics.timestamp lessEq time }
            val count = entities.count().toInt()
            entities.forEach { it.delete() }
            count
        }
    }

    override suspend fun delete(id: UUID): Boolean = withContext(Dispatchers.IO) {
        transaction {
            MetricEntity.findById(id)?.let {
                it.delete()
                true
            } ?: false
        }
    }

    override suspend fun aggregate(
        filter: MetricFilter,
        aggregationType: AggregationType,
        granularity: TimeGranularity?
    ): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        if (granularity != null) {
            aggregateByTime(filter, aggregationType, granularity)
        } else {
            aggregateByDimensions(filter, aggregationType)
        }
    }

    override suspend fun aggregateByTime(
        filter: MetricFilter,
        aggregationType: AggregationType,
        granularity: TimeGranularity
    ): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        transaction {
            // Define the time group by expression based on granularity
            val timeGroupExpr = when (granularity) {
                TimeGranularity.MINUTE -> Metrics.timestamp.castTo<String>(VarCharColumnType()).substring(1, 16)
                TimeGranularity.HOUR -> Metrics.timestamp.hour()
                TimeGranularity.DAY -> Metrics.timestamp.date()
                TimeGranularity.WEEK -> date_trunc("week", Metrics.timestamp)
                TimeGranularity.MONTH -> Metrics.timestamp.month()
                TimeGranularity.QUARTER -> date_trunc("quarter", Metrics.timestamp)
                TimeGranularity.YEAR -> date_trunc("year", Metrics.timestamp)
            }.alias("time_group")
            
            // Define the aggregate expression based on aggregationType
            val aggregateExpr = when (aggregationType) {
                AggregationType.SUM -> Sum(Metrics.value)
                AggregationType.AVERAGE -> Avg(Metrics.value)
                AggregationType.MIN -> Min(Metrics.value)
                AggregationType.MAX -> Max(Metrics.value)
                AggregationType.COUNT -> Count(Metrics.value)
                AggregationType.PERCENTILE -> Sum(Metrics.value) // Percentile is more complex and would require a custom implementation
            }.alias("value")
            
            // Execute the query
            val query = Metrics
                .select(timeGroupExpr, aggregateExpr)
                .where { buildFilterQuery(filter) }
                .groupBy(timeGroupExpr)
                .orderBy(timeGroupExpr)
                
            // Map the results
            query.map { row ->
                mapOf(
                    "time" to row[timeGroupExpr].toString(),
                    "value" to row[aggregateExpr],
                    "aggregationType" to aggregationType.toString()
                )
            }
        }
    }

    override suspend fun aggregateByDimensions(
        filter: MetricFilter,
        aggregationType: AggregationType
    ): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        transaction {
            // This is a simplified implementation - in a real database, you'd need to extract dimensions 
            // from the JSON column and group by them
            
            // Get all metrics matching the filter
            val metrics = MetricEntity.find { buildFilterQuery(filter) }
                .map { it.toMetric() }
                
            // Group by dimensions
            val grouped = metrics.groupBy { it.dimensions.toString() }
            
            // Aggregate each group based on aggregationType
            grouped.map { (dimensionKey, metricsInGroup) ->
                val aggregatedValue = when (aggregationType) {
                    AggregationType.SUM -> metricsInGroup.sumOf { it.value }
                    AggregationType.AVERAGE -> metricsInGroup.map { it.value }.average()
                    AggregationType.MIN -> metricsInGroup.minOfOrNull { it.value } ?: 0.0
                    AggregationType.MAX -> metricsInGroup.maxOfOrNull { it.value } ?: 0.0
                    AggregationType.COUNT -> metricsInGroup.size.toDouble()
                    AggregationType.PERCENTILE -> metricsInGroup.map { it.value }.average() // Simplified
                }
                
                mapOf(
                    "dimensions" to metricsInGroup.first().dimensions,
                    "value" to aggregatedValue,
                    "aggregationType" to aggregationType.toString(),
                    "count" to metricsInGroup.size
                )
            }
        }
    }

    override suspend fun getTimeSeries(
        filter: MetricFilter,
        granularity: TimeGranularity
    ): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        // This is essentially the same as aggregateByTime with SUM as the aggregation type
        aggregateByTime(filter, AggregationType.SUM, granularity)
    }

    override suspend fun calculateStats(filter: MetricFilter): Map<String, Double> = withContext(Dispatchers.IO) {
        transaction {
            val metrics = MetricEntity.find { buildFilterQuery(filter) }
                .map { it.toMetric() }
                
            if (metrics.isEmpty()) {
                mapOf(
                    "count" to 0.0,
                    "sum" to 0.0,
                    "avg" to 0.0,
                    "min" to 0.0,
                    "max" to 0.0
                )
            } else {
                val values = metrics.map { it.value }
                
                mapOf(
                    "count" to values.size.toDouble(),
                    "sum" to values.sum(),
                    "avg" to values.average(),
                    "min" to values.minOrNull() ?: 0.0,
                    "max" to values.maxOrNull() ?: 0.0
                )
            }
        }
    }
    
    /**
     * Helper function to build filter query
     */
    private fun buildFilterQuery(filter: MetricFilter): Op<Boolean> {
        var query: Op<Boolean> = Op.TRUE
        
        filter.names?.let { names ->
            if (names.isNotEmpty()) {
                query = query and (Metrics.name inList names)
            }
        }
        
        filter.startTime?.let { startTime ->
            query = query and (Metrics.timestamp greaterEq startTime)
        }
        
        filter.endTime?.let { endTime ->
            query = query and (Metrics.timestamp lessEq endTime)
        }
        
        // Handle dimensions and tags filters - this is a simplification
        // In a real implementation, you'd need JSON/array operators specific to your database
        
        return query
    }
    
    /**
     * Helper function for PostgreSQL date_trunc
     */
    private fun date_trunc(unit: String, column: Column<Instant>): Expression<LocalDate> {
        return CustomFunction<LocalDate>("date_trunc", VarCharColumnType(), LiteralOp(TextColumnType(), unit), column)
    }
} 