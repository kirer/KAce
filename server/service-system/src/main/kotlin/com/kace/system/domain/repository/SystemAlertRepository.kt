package com.kace.system.domain.repository

import com.kace.system.domain.model.AlertLevel
import com.kace.system.domain.model.SystemAlert
import kotlinx.datetime.Instant

/**
 * 系统告警仓库接口
 */
interface SystemAlertRepository {
    /**
     * 保存系统告警
     *
     * @param alert 系统告警
     * @return 保存的告警
     */
    suspend fun save(alert: SystemAlert): SystemAlert
    
    /**
     * 批量保存系统告警
     *
     * @param alerts 系统告警列表
     * @return 保存的告警列表
     */
    suspend fun saveAll(alerts: List<SystemAlert>): List<SystemAlert>
    
    /**
     * 根据ID查找系统告警
     *
     * @param id 告警ID
     * @return 系统告警，如果不存在则返回null
     */
    suspend fun findById(id: Long): SystemAlert?
    
    /**
     * 查找所有活跃（未解决）的告警
     *
     * @return 活跃告警列表
     */
    suspend fun findActiveAlerts(): List<SystemAlert>
    
    /**
     * 根据服务ID查找活跃告警
     *
     * @param serviceId 服务ID
     * @return 活跃告警列表
     */
    suspend fun findActiveAlertsByServiceId(serviceId: String): List<SystemAlert>
    
    /**
     * 根据告警级别查找活跃告警
     *
     * @param level 告警级别
     * @return 活跃告警列表
     */
    suspend fun findActiveAlertsByLevel(level: AlertLevel): List<SystemAlert>
    
    /**
     * 根据指标名称查找活跃告警
     *
     * @param metricName 指标名称
     * @return 活跃告警列表
     */
    suspend fun findActiveAlertsByMetricName(metricName: String): List<SystemAlert>
    
    /**
     * 根据时间范围查找告警
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 告警列表
     */
    suspend fun findByTimeRange(
        startTime: Instant,
        endTime: Instant,
        limit: Int = 100
    ): List<SystemAlert>
    
    /**
     * 根据服务ID和时间范围查找告警
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 告警列表
     */
    suspend fun findByServiceIdAndTimeRange(
        serviceId: String,
        startTime: Instant,
        endTime: Instant,
        limit: Int = 100
    ): List<SystemAlert>
    
    /**
     * 确认告警
     *
     * @param id 告警ID
     * @param acknowledged 是否已确认
     * @return 更新后的告警，如果不存在则返回null
     */
    suspend fun updateAcknowledged(id: Long, acknowledged: Boolean): SystemAlert?
    
    /**
     * 解决告警
     *
     * @param id 告警ID
     * @param resolvedAt 解决时间
     * @return 更新后的告警，如果不存在则返回null
     */
    suspend fun updateResolved(id: Long, resolvedAt: Instant): SystemAlert?
    
    /**
     * 删除告警
     *
     * @param id 告警ID
     * @return 是否删除成功
     */
    suspend fun deleteById(id: Long): Boolean
    
    /**
     * 删除指定时间之前的告警数据
     *
     * @param timestamp 时间戳
     * @return 删除的记录数
     */
    suspend fun deleteByTimestampBefore(timestamp: Instant): Int
}