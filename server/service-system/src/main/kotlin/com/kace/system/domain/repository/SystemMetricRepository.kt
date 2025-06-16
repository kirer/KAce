package com.kace.system.domain.repository

import com.kace.system.domain.model.MetricType
import com.kace.system.domain.model.SystemMetric
import kotlinx.datetime.Instant

/**
 * 指标统计信息
 *
 * @property min 最小值
 * @property max 最大值
 * @property avg 平均值
 * @property sum 总和
 * @property count 计数
 */
data class MetricStatistics(
    val min: Double,
    val max: Double,
    val avg: Double,
    val sum: Double,
    val count: Long
)

/**
 * 系统指标仓库接口
 */
interface SystemMetricRepository {
    /**
     * 保存系统指标
     *
     * @param metric 系统指标
     * @return 保存的指标
     */
    suspend fun save(metric: SystemMetric): SystemMetric
    
    /**
     * 批量保存系统指标
     *
     * @param metrics 系统指标列表
     * @return 保存的指标列表
     */
    suspend fun saveAll(metrics: List<SystemMetric>): List<SystemMetric>
    
    /**
     * 根据ID查找系统指标
     *
     * @param id 指标ID
     * @return 系统指标，如果不存在则返回null
     */
    suspend fun findById(id: Long): SystemMetric?
    
    /**
     * 根据指标名称和服务ID查找最新指标
     *
     * @param name 指标名称
     * @param serviceId 服务ID
     * @return 最新指标，如果不存在则返回null
     */
    suspend fun findLatestByNameAndServiceId(name: String, serviceId: String): SystemMetric?
    
    /**
     * 根据服务ID查找所有最新指标
     *
     * @param serviceId 服务ID
     * @return 最新指标列表
     */
    suspend fun findLatestByServiceId(serviceId: String): List<SystemMetric>
    
    /**
     * 查找所有服务的最新指标，并按服务ID分组
     *
     * @return 按服务ID分组的最新指标
     */
    suspend fun findAllLatestGroupByServiceId(): Map<String, List<SystemMetric>>
    
    /**
     * 根据指标名称、服务ID和时间范围查询指标历史数据
     *
     * @param name 指标名称
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 指标历史数据
     */
    suspend fun findByNameAndServiceIdAndTimeRange(
        name: String,
        serviceId: String,
        startTime: Instant,
        endTime: Instant,
        limit: Int = 100
    ): List<SystemMetric>
    
    /**
     * 获取指标统计信息
     *
     * @param name 指标名称
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标统计信息
     */
    suspend fun getStatistics(
        name: String,
        serviceId: String,
        startTime: Instant,
        endTime: Instant
    ): MetricStatistics
    
    /**
     * 删除指定时间之前的指标数据
     *
     * @param timestamp 时间戳
     * @return 删除的记录数
     */
    suspend fun deleteByTimestampBefore(timestamp: Instant): Int
}