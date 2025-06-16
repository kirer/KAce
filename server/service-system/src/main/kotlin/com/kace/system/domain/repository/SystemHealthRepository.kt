package com.kace.system.domain.repository

import com.kace.system.domain.model.HealthStatus
import com.kace.system.domain.model.SystemHealth
import kotlinx.datetime.Instant

/**
 * 系统健康信息仓库接口
 */
interface SystemHealthRepository {
    /**
     * 保存系统健康信息
     *
     * @param health 系统健康信息
     * @return 保存的健康信息
     */
    suspend fun save(health: SystemHealth): SystemHealth
    
    /**
     * 批量保存系统健康信息
     *
     * @param healthRecords 系统健康信息列表
     * @return 保存的健康信息列表
     */
    suspend fun saveAll(healthRecords: List<SystemHealth>): List<SystemHealth>
    
    /**
     * 根据ID查找系统健康信息
     *
     * @param id 健康信息ID
     * @return 系统健康信息，如果不存在则返回null
     */
    suspend fun findById(id: Long): SystemHealth?
    
    /**
     * 根据服务ID查找最新健康信息
     *
     * @param serviceId 服务ID
     * @return 最新健康信息，如果不存在则返回null
     */
    suspend fun findLatestByServiceId(serviceId: String): SystemHealth?
    
    /**
     * 查找所有服务的最新健康信息
     *
     * @return 所有服务的最新健康信息
     */
    suspend fun findAllLatest(): List<SystemHealth>
    
    /**
     * 根据服务ID和时间范围查询健康信息历史记录
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 健康信息历史记录
     */
    suspend fun findByServiceIdAndTimeRange(
        serviceId: String,
        startTime: Instant,
        endTime: Instant,
        limit: Int = 100
    ): List<SystemHealth>
    
    /**
     * 删除指定时间之前的健康信息数据
     *
     * @param timestamp 时间戳
     * @return 删除的记录数
     */
    suspend fun deleteByTimestampBefore(timestamp: Instant): Int
}