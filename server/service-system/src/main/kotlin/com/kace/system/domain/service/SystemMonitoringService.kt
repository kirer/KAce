package com.kace.system.domain.service

import com.kace.system.domain.model.*
import com.kace.system.domain.repository.MetricStatistics
import kotlinx.datetime.Instant

/**
 * 系统监控服务接口
 */
interface SystemMonitoringService {
    /**
     * 记录系统指标
     *
     * @param metric 系统指标
     * @return 保存的指标
     */
    suspend fun recordMetric(metric: SystemMetric): SystemMetric
    
    /**
     * 批量记录系统指标
     *
     * @param metrics 系统指标列表
     * @return 保存的指标列表
     */
    suspend fun recordMetrics(metrics: List<SystemMetric>): List<SystemMetric>
    
    /**
     * 获取指定名称的最新指标
     *
     * @param name 指标名称
     * @param serviceId 服务ID
     * @return 最新指标，如果不存在则返回null
     */
    suspend fun getLatestMetric(name: String, serviceId: String = "system"): SystemMetric?
    
    /**
     * 获取指定服务的所有最新指标
     *
     * @param serviceId 服务ID
     * @return 最新指标列表
     */
    suspend fun getLatestMetrics(serviceId: String): List<SystemMetric>
    
    /**
     * 获取所有服务的最新指标
     *
     * @return 所有服务的最新指标列表，按服务ID分组
     */
    suspend fun getAllLatestMetrics(): Map<String, List<SystemMetric>>
    
    /**
     * 查询指标历史数据
     *
     * @param name 指标名称
     * @param serviceId 服务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 指标历史数据
     */
    suspend fun queryMetricHistory(
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
    suspend fun getMetricStatistics(
        name: String,
        serviceId: String,
        startTime: Instant,
        endTime: Instant
    ): MetricStatistics
    
    /**
     * 更新服务健康状态
     *
     * @param health 健康信息
     * @return 更新后的健康信息
     */
    suspend fun updateHealthStatus(health: SystemHealth): SystemHealth
    
    /**
     * 获取所有服务的健康状态
     *
     * @return 所有服务的最新健康状态
     */
    suspend fun getAllHealthStatus(): List<SystemHealth>
    
    /**
     * 获取指定服务的健康状态
     *
     * @param serviceId 服务ID
     * @return 指定服务的最新健康状态，如果不存在则返回null
     */
    suspend fun getHealthStatus(serviceId: String): SystemHealth?
    
    /**
     * 创建告警规则
     *
     * @param rule 告警规则
     * @return 创建的规则
     */
    suspend fun createAlertRule(rule: AlertRule): AlertRule
    
    /**
     * 更新告警规则
     *
     * @param rule 告警规则
     * @return 更新后的规则
     */
    suspend fun updateAlertRule(rule: AlertRule): AlertRule
    
    /**
     * 启用告警规则
     *
     * @param ruleId 规则ID
     * @return 更新后的规则
     */
    suspend fun enableAlertRule(ruleId: Long): AlertRule?
    
    /**
     * 禁用告警规则
     *
     * @param ruleId 规则ID
     * @return 更新后的规则
     */
    suspend fun disableAlertRule(ruleId: Long): AlertRule?
    
    /**
     * 删除告警规则
     *
     * @param ruleId 规则ID
     * @return 是否删除成功
     */
    suspend fun deleteAlertRule(ruleId: Long): Boolean
    
    /**
     * 获取所有告警规则
     *
     * @return 所有告警规则列表
     */
    suspend fun getAllAlertRules(): List<AlertRule>
    
    /**
     * 获取所有启用的告警规则
     *
     * @return 所有启用的告警规则列表
     */
    suspend fun getEnabledAlertRules(): List<AlertRule>
    
    /**
     * 获取所有未解决的告警
     *
     * @return 所有未解决的告警列表
     */
    suspend fun getActiveAlerts(): List<SystemAlert>
    
    /**
     * 确认告警
     *
     * @param alertId 告警ID
     * @return 更新后的告警
     */
    suspend fun acknowledgeAlert(alertId: Long): SystemAlert?
    
    /**
     * 解决告警
     *
     * @param alertId 告警ID
     * @return 更新后的告警
     */
    suspend fun resolveAlert(alertId: Long): SystemAlert?
    
    /**
     * 删除告警
     *
     * @param alertId 告警ID
     * @return 是否删除成功
     */
    suspend fun deleteAlert(alertId: Long): Boolean
    
    /**
     * 根据指标评估告警规则
     * 
     * @param metric 系统指标
     * @return 触发的告警列表
     */
    suspend fun evaluateAlertRules(metric: SystemMetric): List<SystemAlert>
    
    /**
     * 维护清理任务：删除过期指标和告警数据
     *
     * @param metricRetentionDays 指标保留天数
     * @param alertRetentionDays 告警保留天数
     * @return 清理的记录总数
     */
    suspend fun performMaintenance(metricRetentionDays: Int = 30, alertRetentionDays: Int = 90): Int
}