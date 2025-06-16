package com.kace.system.domain.repository

import com.kace.system.domain.model.AlertLevel
import com.kace.system.domain.model.AlertRule
import com.kace.system.domain.model.AlertRuleType

/**
 * 告警规则仓库接口
 */
interface AlertRuleRepository {
    /**
     * 保存告警规则
     *
     * @param rule 告警规则
     * @return 保存的规则
     */
    suspend fun save(rule: AlertRule): AlertRule
    
    /**
     * 根据ID查找告警规则
     *
     * @param id 规则ID
     * @return 告警规则，如果不存在则返回null
     */
    suspend fun findById(id: Long): AlertRule?
    
    /**
     * 根据名称查找告警规则
     *
     * @param name 规则名称
     * @return 告警规则，如果不存在则返回null
     */
    suspend fun findByName(name: String): AlertRule?
    
    /**
     * 查找所有告警规则
     *
     * @return 所有告警规则列表
     */
    suspend fun findAll(): List<AlertRule>
    
    /**
     * 根据启用状态查找告警规则
     *
     * @param enabled 是否启用
     * @return 符合条件的告警规则列表
     */
    suspend fun findAllByEnabled(enabled: Boolean): List<AlertRule>
    
    /**
     * 根据指标名称和启用状态查找告警规则
     *
     * @param metricName 指标名称
     * @param enabled 是否启用
     * @return 符合条件的告警规则列表
     */
    suspend fun findAllByMetricNameAndEnabled(metricName: String, enabled: Boolean): List<AlertRule>
    
    /**
     * 根据告警级别查找告警规则
     *
     * @param level 告警级别
     * @return 符合条件的告警规则列表
     */
    suspend fun findAllByLevel(level: String): List<AlertRule>
    
    /**
     * 删除告警规则
     *
     * @param id 规则ID
     * @return 是否删除成功
     */
    suspend fun deleteById(id: Long): Boolean
}