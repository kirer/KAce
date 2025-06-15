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
     * @return 保存后的告警规则
     */
    suspend fun saveRule(rule: AlertRule): AlertRule
    
    /**
     * 根据ID获取告警规则
     *
     * @param id 规则ID
     * @return 告警规则，如果不存在则返回null
     */
    suspend fun findById(id: Long): AlertRule?
    
    /**
     * 获取所有告警规则
     *
     * @return 告警规则列表
     */
    suspend fun findAll(): List<AlertRule>
    
    /**
     * 获取所有启用的告警规则
     *
     * @return 启用的告警规则列表
     */
    suspend fun findAllEnabled(): List<AlertRule>
    
    /**
     * 根据指标名称查找规则
     *
     * @param metricName 指标名称
     * @return 告警规则列表
     */
    suspend fun findByMetricName(metricName: String): List<AlertRule>
    
    /**
     * 根据告警级别查找规则
     *
     * @param level 告警级别
     * @return 告警规则列表
     */
    suspend fun findByLevel(level: AlertLevel): List<AlertRule>
    
    /**
     * 根据规则类型查找规则
     *
     * @param type 规则类型
     * @return 告警规则列表
     */
    suspend fun findByType(type: AlertRuleType): List<AlertRule>
    
    /**
     * 启用规则
     *
     * @param id 规则ID
     * @return 更新后的规则，如果不存在则返回null
     */
    suspend fun enableRule(id: Long): AlertRule?
    
    /**
     * 禁用规则
     *
     * @param id 规则ID
     * @return 更新后的规则，如果不存在则返回null
     */
    suspend fun disableRule(id: Long): AlertRule?
    
    /**
     * 删除规则
     *
     * @param id 规则ID
     * @return 是否删除成功
     */
    suspend fun deleteRule(id: Long): Boolean
}