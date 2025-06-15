package com.kace.system.domain.repository

import com.kace.system.domain.model.HealthStatus
import com.kace.system.domain.model.SystemHealth
import kotlinx.datetime.Instant

/**
 * 系统健康信息仓库接口
 */
interface SystemHealthRepository {
    /**
     * 保存健康信息记录
     *
     * @param health 健康信息记录
     * @return 保存后的健康信息记录
     */
    suspend fun saveHealth(health: SystemHealth): SystemHealth
    
    /**
     * 根据ID获取健康信息记录
     *
     * @param id 健康信息ID
     * @return 健康信息记录，如果不存在则返回null
     */
    suspend fun findById(id: Long): SystemHealth?
    
    /**
     * 获取指定服务的最新健康信息
     *
     * @param serviceId 服务ID
     * @return 最新的健康信息记录，如果不存在则返回null
     */
    suspend fun findLatestByServiceId(serviceId: String): SystemHealth?
    
    /**
     * 获取所有服务的最新健康信息
     *
     * @return 所有服务的最新健康信息记录列表
     */
    suspend fun findAllLatest(): List<SystemHealth>
    
    /**
     * 根据状态获取最新健康信息
     *
     * @param status 健康状态
     * @return 指定状态的最新健康信息记录列表
     */
    suspend fun findLatestByStatus(status: HealthStatus): List<SystemHealth>
    
    /**
     * 获取指定服务的健康历史记录
     *
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量，默认为100
     * @return 健康信息记录列表
     */
    suspend fun findHistoryByServiceId(
        serviceId: String,
        startTime: Instant,
        endTime: Instant,
        limit: Int = 100
    ): List<SystemHealth>
    
    /**
     * 删除历史健康信息记录
     *
     * @param before 截止时间，删除该时间之前的数据
     * @return 删除的记录数
     */
    suspend fun deleteExpiredHealth(before: Instant): Int
}