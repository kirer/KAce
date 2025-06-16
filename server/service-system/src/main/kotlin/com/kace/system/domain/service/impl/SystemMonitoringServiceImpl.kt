package com.kace.system.domain.service.impl

import com.kace.system.domain.model.*
import com.kace.system.domain.repository.MetricStatistics
import com.kace.system.domain.repository.SystemMetricRepository
import com.kace.system.domain.repository.SystemHealthRepository
import com.kace.system.domain.repository.AlertRuleRepository
import com.kace.system.domain.repository.SystemAlertRepository
import com.kace.system.domain.service.SystemMonitoringService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 系统监控服务实现类
 */
@Service
class SystemMonitoringServiceImpl(
    private val metricRepository: SystemMetricRepository,
    private val healthRepository: SystemHealthRepository,
    private val alertRuleRepository: AlertRuleRepository,
    private val alertRepository: SystemAlertRepository
) : SystemMonitoringService {

    /**
     * 记录系统指标
     */
    override suspend fun recordMetric(metric: SystemMetric): SystemMetric {
        val metricWithTimestamp = if (metric.timestamp == Instant.DISTANT_PAST) {
            metric.copy(timestamp = Clock.System.now())
        } else {
            metric
        }
        
        val savedMetric = metricRepository.save(metricWithTimestamp)
        evaluateAlertRules(savedMetric)
        return savedMetric
    }

    /**
     * 批量记录系统指标
     */
    override suspend fun recordMetrics(metrics: List<SystemMetric>): List<SystemMetric> {
        val now = Clock.System.now()
        val metricsWithTimestamp = metrics.map { metric ->
            if (metric.timestamp == Instant.DISTANT_PAST) {
                metric.copy(timestamp = now)
            } else {
                metric
            }
        }
        
        val savedMetrics = metricRepository.saveAll(metricsWithTimestamp)
        savedMetrics.forEach { evaluateAlertRules(it) }
        return savedMetrics
    }

    /**
     * 获取指定名称的最新指标
     */
    override suspend fun getLatestMetric(name: String, serviceId: String): SystemMetric? {
        return metricRepository.findLatestByNameAndServiceId(name, serviceId)
    }

    /**
     * 获取指定服务的所有最新指标
     */
    override suspend fun getLatestMetrics(serviceId: String): List<SystemMetric> {
        return metricRepository.findLatestByServiceId(serviceId)
    }

    /**
     * 获取所有服务的最新指标
     */
    override suspend fun getAllLatestMetrics(): Map<String, List<SystemMetric>> {
        return metricRepository.findAllLatestGroupByServiceId()
    }

    /**
     * 查询指标历史数据
     */
    override suspend fun queryMetricHistory(
        name: String,
        serviceId: String,
        startTime: Instant,
        endTime: Instant,
        limit: Int
    ): List<SystemMetric> {
        return metricRepository.findByNameAndServiceIdAndTimeRange(name, serviceId, startTime, endTime, limit)
    }

    /**
     * 获取指标统计信息
     */
    override suspend fun getMetricStatistics(
        name: String,
        serviceId: String,
        startTime: Instant,
        endTime: Instant
    ): MetricStatistics {
        return metricRepository.getStatistics(name, serviceId, startTime, endTime)
    }

    /**
     * 更新服务健康状态
     */
    override suspend fun updateHealthStatus(health: SystemHealth): SystemHealth {
        val healthWithTimestamp = if (health.timestamp == Instant.DISTANT_PAST) {
            health.copy(timestamp = Clock.System.now())
        } else {
            health
        }
        return healthRepository.save(healthWithTimestamp)
    }

    /**
     * 获取所有服务的健康状态
     */
    override suspend fun getAllHealthStatus(): List<SystemHealth> {
        return healthRepository.findAllLatest()
    }

    /**
     * 获取指定服务的健康状态
     */
    override suspend fun getHealthStatus(serviceId: String): SystemHealth? {
        return healthRepository.findLatestByServiceId(serviceId)
    }

    /**
     * 创建告警规则
     */
    @Transactional
    override suspend fun createAlertRule(rule: AlertRule): AlertRule {
        val now = Clock.System.now()
        val ruleWithTimestamp = rule.copy(
            createdAt = now,
            updatedAt = now
        )
        return alertRuleRepository.save(ruleWithTimestamp)
    }

    /**
     * 更新告警规则
     */
    @Transactional
    override suspend fun updateAlertRule(rule: AlertRule): AlertRule {
        val existingRule = rule.id?.let { alertRuleRepository.findById(it) }
            ?: throw IllegalArgumentException("规则不存在")
        
        val updatedRule = rule.copy(
            createdAt = existingRule.createdAt,
            updatedAt = Clock.System.now()
        )
        
        return alertRuleRepository.save(updatedRule)
    }

    /**
     * 启用告警规则
     */
    @Transactional
    override suspend fun enableAlertRule(ruleId: Long): AlertRule? {
        val rule = alertRuleRepository.findById(ruleId) ?: return null
        val updatedRule = rule.copy(
            enabled = true,
            updatedAt = Clock.System.now()
        )
        return alertRuleRepository.save(updatedRule)
    }

    /**
     * 禁用告警规则
     */
    @Transactional
    override suspend fun disableAlertRule(ruleId: Long): AlertRule? {
        val rule = alertRuleRepository.findById(ruleId) ?: return null
        val updatedRule = rule.copy(
            enabled = false,
            updatedAt = Clock.System.now()
        )
        return alertRuleRepository.save(updatedRule)
    }

    /**
     * 删除告警规则
     */
    @Transactional
    override suspend fun deleteAlertRule(ruleId: Long): Boolean {
        return alertRuleRepository.deleteById(ruleId)
    }

    /**
     * 获取所有告警规则
     */
    override suspend fun getAllAlertRules(): List<AlertRule> {
        return alertRuleRepository.findAll()
    }

    /**
     * 获取所有启用的告警规则
     */
    override suspend fun getEnabledAlertRules(): List<AlertRule> {
        return alertRuleRepository.findAllByEnabled(true)
    }

    /**
     * 获取所有未解决的告警
     */
    override suspend fun getActiveAlerts(): List<SystemAlert> {
        return alertRepository.findActiveAlerts()
    }

    /**
     * 确认告警
     */
    @Transactional
    override suspend fun acknowledgeAlert(alertId: Long): SystemAlert? {
        val alert = alertRepository.findById(alertId) ?: return null
        val updatedAlert = alert.copy(
            acknowledged = true
        )
        return alertRepository.save(updatedAlert)
    }

    /**
     * 解决告警
     */
    @Transactional
    override suspend fun resolveAlert(alertId: Long): SystemAlert? {
        val alert = alertRepository.findById(alertId) ?: return null
        val updatedAlert = alert.copy(
            resolvedAt = Clock.System.now()
        )
        return alertRepository.save(updatedAlert)
    }

    /**
     * 删除告警
     */
    @Transactional
    override suspend fun deleteAlert(alertId: Long): Boolean {
        return alertRepository.deleteById(alertId)
    }

    /**
     * 根据指标评估告警规则
     */
    override suspend fun evaluateAlertRules(metric: SystemMetric): List<SystemAlert> {
        val rules = alertRuleRepository.findAllByMetricNameAndEnabled(metric.name, true)
        val alerts = mutableListOf<SystemAlert>()
        
        val now = Clock.System.now()
        
        for (rule in rules) {
            // 检查服务ID是否匹配规则模式
            if (!isServiceMatch(metric.serviceId, rule.servicePattern)) {
                continue
            }
            
            // 检查指标值是否超过阈值
            if (isThresholdViolated(metric.value, rule.threshold, rule.operator)) {
                val alert = SystemAlert(
                    name = rule.name,
                    level = rule.level,
                    message = formatAlertMessage(rule.message, metric, rule),
                    serviceId = metric.serviceId,
                    metricName = metric.name,
                    threshold = rule.threshold,
                    currentValue = metric.value,
                    timestamp = now
                )
                alerts.add(alertRepository.save(alert))
            }
        }
        
        return alerts
    }

    /**
     * 检查服务ID是否匹配规则模式（支持通配符）
     */
    private fun isServiceMatch(serviceId: String, pattern: String): Boolean {
        if (pattern == "*") return true
        
        if (pattern.contains("*")) {
            val regex = pattern.replace("*", ".*").toRegex()
            return serviceId.matches(regex)
        }
        
        return serviceId == pattern
    }

    /**
     * 检查是否违反阈值规则
     */
    private fun isThresholdViolated(value: Double, threshold: Double, operator: AlertRuleOperator): Boolean {
        return when (operator) {
            AlertRuleOperator.GREATER_THAN -> value > threshold
            AlertRuleOperator.LESS_THAN -> value < threshold
            AlertRuleOperator.EQUAL_TO -> value == threshold
            AlertRuleOperator.NOT_EQUAL_TO -> value != threshold
            AlertRuleOperator.GREATER_THAN_OR_EQUAL -> value >= threshold
            AlertRuleOperator.LESS_THAN_OR_EQUAL -> value <= threshold
        }
    }

    /**
     * 格式化告警消息
     */
    private fun formatAlertMessage(template: String, metric: SystemMetric, rule: AlertRule): String {
        if (template.isNotEmpty()) {
            return template
                .replace("{metric}", metric.name)
                .replace("{value}", metric.value.toString())
                .replace("{threshold}", rule.threshold.toString())
                .replace("{service}", metric.serviceId)
                .replace("{operator}", operatorToString(rule.operator))
                .replace("{unit}", metric.unit)
        }
        
        return "${metric.serviceId}服务的${metric.name}指标值为${metric.value}${metric.unit}，${operatorToString(rule.operator)}阈值${rule.threshold}"
    }
    
    /**
     * 将操作符转换为字符串描述
     */
    private fun operatorToString(operator: AlertRuleOperator): String {
        return when (operator) {
            AlertRuleOperator.GREATER_THAN -> "大于"
            AlertRuleOperator.LESS_THAN -> "小于"
            AlertRuleOperator.EQUAL_TO -> "等于"
            AlertRuleOperator.NOT_EQUAL_TO -> "不等于"
            AlertRuleOperator.GREATER_THAN_OR_EQUAL -> "大于等于"
            AlertRuleOperator.LESS_THAN_OR_EQUAL -> "小于等于"
        }
    }

    /**
     * 维护清理任务：删除过期指标和告警数据
     */
    @Transactional
    override suspend fun performMaintenance(metricRetentionDays: Int, alertRetentionDays: Int): Int {
        val metricThreshold = Clock.System.now().minus(kotlinx.datetime.DateTimePeriod.days(metricRetentionDays))
        val alertThreshold = Clock.System.now().minus(kotlinx.datetime.DateTimePeriod.days(alertRetentionDays))
        
        val deletedMetrics = metricRepository.deleteByTimestampBefore(metricThreshold)
        val deletedAlerts = alertRepository.deleteByTimestampBefore(alertThreshold)
        
        return deletedMetrics + deletedAlerts
    }
} 