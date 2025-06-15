package com.kace.system.domain.repository

import com.kace.system.domain.model.AlertLevel
import com.kace.system.domain.model.SystemAlert
import kotlinx.datetime.Instant

/**
 * 系统告警仓库接口
 */
interface SystemAlertRepository {
    /**
     * 保存告警记录
     *
     * @param alert 告警记录
     * @return 保存后的告警记录
     */
    suspend fun saveAlert(alert: SystemAlert): SystemAlert
    
    /**
     * 根据ID获取告警记录
     *
     * @param id 告警ID
     * @return 告警记录，如果不存在则返回null
     */
    suspend fun findById(id: Long): SystemAlert?
    
    /**
     * 查找未解决的告警
     *
     * @return 未解决的告警列表
     */
    suspend fun findUnresolved(): List<SystemAlert>
    
    /**
     * 查找指定服务的未解决告警
     *
     * @param serviceId 服务ID
     * @return 未解决的告警列表
     */
    suspend fun findUnresolvedByServiceId(serviceId: String): List<SystemAlert>
    
    /**
     * 根据告警级别查找未解决告警
     *
     * @param level 告警级别
     * @return 未解决的告警列表
     */
    suspend fun findUnresolvedByLevel(level: AlertLevel): List<SystemAlert>
    
    /**
     * 查找指定时间范围内的告警
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量，默认为100
     * @return 告警列表
     */
    suspend fun findByTimeRange(
        startTime: Instant,
        endTime: Instant,
        limit: Int = 100
    ): List<SystemAlert>
    
    /**
     * 确认告警
     *
     * @param id 告警ID
     * @return 更新后的告警记录，如果不存在则返回null
     */
    suspend fun acknowledgeAlert(id: Long): SystemAlert?
    
    /**
     * 解决告警
     *
     * @param id 告警ID
     * @return 更新后的告警记录，如果不存在则返回null
     */
    suspend fun resolveAlert(id: Long): SystemAlert?
    
    /**
     * 删除告警记录
     *
     * @param id 告警ID
     * @return 是否删除成功
     */
    suspend fun deleteAlert(id: Long): Boolean
    
    /**
     * 删除已解决的过期告警
     *
     * @param before 截止时间，删除该时间之前解决的告警
     * @return 删除的记录数
     */
    suspend fun deleteExpiredAlerts(before: Instant): Int
}