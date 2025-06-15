package com.kace.analytics.domain.repository

import com.kace.analytics.domain.model.AggregationType
import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.model.MetricFilter
import com.kace.analytics.domain.model.TimeGranularity
import java.time.Instant
import java.util.UUID

/**
 * 指标仓库接口
 */
interface MetricRepository {
    
    /**
     * 保存指标
     */
    suspend fun save(metric: Metric): Metric
    
    /**
     * 批量保存指标
     */
    suspend fun saveAll(metrics: List<Metric>): List<Metric>
    
    /**
     * 根据ID查找指标
     */
    suspend fun findById(id: UUID): Metric?
    
    /**
     * 根据过滤条件查找指标
     */
    suspend fun findByFilter(filter: MetricFilter, page: Int, size: Int): List<Metric>
    
    /**
     * 根据名称查找指标
     */
    suspend fun findByName(name: String, page: Int, size: Int): List<Metric>
    
    /**
     * 根据时间范围查找指标
     */
    suspend fun findByTimeRange(startTime: Instant, endTime: Instant, page: Int, size: Int): List<Metric>
    
    /**
     * 根据维度查找指标
     */
    suspend fun findByDimension(dimension: String, value: String, page: Int, size: Int): List<Metric>
    
    /**
     * 根据标签查找指标
     */
    suspend fun findByTag(tag: String, page: Int, size: Int): List<Metric>
    
    /**
     * 统计指标数量
     */
    suspend fun count(filter: MetricFilter? = null): Long
    
    /**
     * 删除指定时间之前的指标
     */
    suspend fun deleteBeforeTime(time: Instant): Int
    
    /**
     * 删除指标
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 聚合指标数据
     */
    suspend fun aggregate(
        filter: MetricFilter,
        aggregationType: AggregationType = AggregationType.SUM,
        granularity: TimeGranularity? = null
    ): List<Map<String, Any>>
    
    /**
     * 按时间聚合指标数据
     */
    suspend fun aggregateByTime(
        filter: MetricFilter,
        aggregationType: AggregationType,
        granularity: TimeGranularity
    ): List<Map<String, Any>>
    
    /**
     * 按维度聚合指标数据
     */
    suspend fun aggregateByDimensions(
        filter: MetricFilter,
        aggregationType: AggregationType
    ): List<Map<String, Any>>
    
    /**
     * 获取指标时间序列
     */
    suspend fun getTimeSeries(
        filter: MetricFilter,
        granularity: TimeGranularity
    ): List<Map<String, Any>>
    
    /**
     * 计算指标统计信息
     */
    suspend fun calculateStats(
        filter: MetricFilter
    ): Map<String, Double>
} 