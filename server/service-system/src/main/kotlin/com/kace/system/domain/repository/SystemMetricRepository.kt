package com.kace.system.domain.repository

import com.kace.system.domain.model.MetricType
import com.kace.system.domain.model.SystemMetric
import kotlinx.datetime.Instant

/**
 * 系统指标仓库接口
 */
interface SystemMetricRepository {
    /**
     * 保存一条指标记录
     *
     * @param metric 指标记录
     * @return 保存后的指标记录
     */
    suspend fun saveMetric(metric: SystemMetric): SystemMetric
    
    /**
     * 批量保存指标记录
     *
     * @param metrics 指标记录列表
     * @return 保存后的指标记录列表
     */
    suspend fun saveMetrics(metrics: List<SystemMetric>): List<SystemMetric>
    
    /**
     * 根据ID获取指标记录
     *
     * @param id 指标ID
     * @return 指标记录，如果不存在则返回null
     */
    suspend fun findById(id: Long): SystemMetric?
    
    /**
     * 根据指标名称和时间范围查询
     *
     * @param name 指标名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量，默认为100
     * @return 指标记录列表
     */
    suspend fun findByNameAndTimeRange(
        name: String, 
        startTime: Instant, 
        endTime: Instant,
        limit: Int = 100
    ): List<SystemMetric>
    
    /**
     * 根据服务ID和时间范围查询
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量，默认为100
     * @return 指标记录列表
     */
    suspend fun findByServiceIdAndTimeRange(
        serviceId: String, 
        startTime: Instant, 
        endTime: Instant,
        limit: Int = 100
    ): List<SystemMetric>
    
    /**
     * 根据指标类型和时间范围查询
     *
     * @param type 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量，默认为100
     * @return 指标记录列表
     */
    suspend fun findByTypeAndTimeRange(
        type: MetricType, 
        startTime: Instant, 
        endTime: Instant,
        limit: Int = 100
    ): List<SystemMetric>
    
    /**
     * 获取指标最新值
     *
     * @param name 指标名称
     * @param serviceId 服务ID，默认为"system"
     * @return 最新的指标记录，如果不存在则返回null
     */
    suspend fun findLatestByName(name: String, serviceId: String = "system"): SystemMetric?
    
    /**
     * 获取某个服务的所有最新指标
     *
     * @param serviceId 服务ID
     * @return 最新的指标记录列表
     */
    suspend fun findLatestByServiceId(serviceId: String): List<SystemMetric>
    
    /**
     * 删除过期数据
     *
     * @param before 截止时间，删除该时间之前的数据
     * @return 删除的记录数
     */
    suspend fun deleteExpiredMetrics(before: Instant): Int
    
    /**
     * 获取指标的聚合统计（如平均值、最大值、最小值）
     *
     * @param name 指标名称
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聚合统计结果，包括平均值、最大值、最小值
     */
    suspend fun getMetricStatistics(
        name: String,
        serviceId: String,
        startTime: Instant,
        endTime: Instant
    ): MetricStatistics
}

/**
 * 指标统计数据
 *
 * @property count 数据点数量
 * @property avg 平均值
 * @property max 最大值
 * @property min 最小值
 * @property sum 总和
 */
data class MetricStatistics(
    val count: Long = 0,
    val avg: Double = 0.0,
    val max: Double = Double.MIN_VALUE,
    val min: Double = Double.MAX_VALUE,
    val sum: Double = 0.0
)