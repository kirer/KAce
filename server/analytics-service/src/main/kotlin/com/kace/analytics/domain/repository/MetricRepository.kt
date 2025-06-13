package com.kace.analytics.domain.repository

import com.kace.analytics.domain.model.Metric
import com.kace.analytics.domain.model.MetricTimeSeries
import kotlinx.datetime.Instant

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
    suspend fun findById(id: String): Metric?
    
    /**
     * 根据名称查找最新指标
     */
    suspend fun findLatestByName(name: String, dimensions: Map<String, String> = emptyMap()): Metric?
    
    /**
     * 根据条件查询指标
     */
    suspend fun findAll(
        name: String? = null,
        dimensions: Map<String, String> = emptyMap(),
        startTime: Instant? = null,
        endTime: Instant? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<Metric>
    
    /**
     * 获取指标时间序列
     */
    suspend fun getTimeSeries(
        name: String,
        dimensions: Map<String, String> = emptyMap(),
        startTime: Instant,
        endTime: Instant,
        interval: String = "1h"
    ): MetricTimeSeries
    
    /**
     * 获取仪表盘指标
     */
    suspend fun getDashboardMetrics(
        names: List<String>,
        dimensions: Map<String, String> = emptyMap(),
        currentStartTime: Instant,
        currentEndTime: Instant,
        previousStartTime: Instant,
        previousEndTime: Instant
    ): Map<String, Pair<Double, Double>>
    
    /**
     * 删除指标
     */
    suspend fun delete(id: String): Boolean
    
    /**
     * 删除指定时间之前的指标
     */
    suspend fun deleteOlderThan(timestamp: Instant): Int
} 