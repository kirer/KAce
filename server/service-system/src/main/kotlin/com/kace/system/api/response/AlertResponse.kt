package com.kace.system.api.response

import com.kace.system.domain.model.AlertLevel
import com.kace.system.domain.model.AlertRule
import com.kace.system.domain.model.AlertRuleOperator
import com.kace.system.domain.model.AlertRuleType
import com.kace.system.domain.model.SystemAlert
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 告警规则响应
 */
@Serializable
data class AlertRuleResponse(
    val id: Long?,
    val name: String,
    val metricName: String,
    val type: AlertRuleType,
    val operator: AlertRuleOperator,
    val threshold: Double,
    val level: AlertLevel,
    val enabled: Boolean,
    val servicePattern: String,
    val consecutiveDataPoints: Int,
    val message: String,
    val createdAt: Instant?,
    val updatedAt: Instant?
) {
    companion object {
        fun fromDomain(rule: AlertRule): AlertRuleResponse {
            return AlertRuleResponse(
                id = rule.id,
                name = rule.name,
                metricName = rule.metricName,
                type = rule.type,
                operator = rule.operator,
                threshold = rule.threshold,
                level = rule.level,
                enabled = rule.enabled,
                servicePattern = rule.servicePattern,
                consecutiveDataPoints = rule.consecutiveDataPoints,
                message = rule.message,
                createdAt = rule.createdAt,
                updatedAt = rule.updatedAt
            )
        }
    }
}

/**
 * 系统告警响应
 */
@Serializable
data class SystemAlertResponse(
    val id: Long?,
    val name: String,
    val level: AlertLevel,
    val message: String,
    val serviceId: String,
    val metricName: String,
    val threshold: Double,
    val currentValue: Double,
    val timestamp: Instant,
    val acknowledged: Boolean,
    val resolvedAt: Instant?
) {
    companion object {
        fun fromDomain(alert: SystemAlert): SystemAlertResponse {
            return SystemAlertResponse(
                id = alert.id,
                name = alert.name,
                level = alert.level,
                message = alert.message,
                serviceId = alert.serviceId,
                metricName = alert.metricName,
                threshold = alert.threshold,
                currentValue = alert.currentValue,
                timestamp = alert.timestamp,
                acknowledged = alert.acknowledged,
                resolvedAt = alert.resolvedAt
            )
        }
    }
}

/**
 * 告警统计响应
 */
@Serializable
data class AlertStatisticsResponse(
    val totalAlerts: Int,
    val activeAlerts: Int,
    val acknowledgedAlerts: Int,
    val resolvedAlerts: Int,
    val criticalAlerts: Int,
    val errorAlerts: Int,
    val warningAlerts: Int,
    val infoAlerts: Int,
    val byServiceId: Map<String, Int>
) {
    companion object {
        fun fromAlertList(alerts: List<SystemAlert>): AlertStatisticsResponse {
            val activeAlerts = alerts.count { it.resolvedAt == null }
            val acknowledgedAlerts = alerts.count { it.acknowledged && it.resolvedAt == null }
            val resolvedAlerts = alerts.count { it.resolvedAt != null }
            
            val criticalAlerts = alerts.count { it.level == AlertLevel.CRITICAL }
            val errorAlerts = alerts.count { it.level == AlertLevel.ERROR }
            val warningAlerts = alerts.count { it.level == AlertLevel.WARNING }
            val infoAlerts = alerts.count { it.level == AlertLevel.INFO }
            
            val byServiceId = alerts.groupBy { it.serviceId }.mapValues { it.value.size }
            
            return AlertStatisticsResponse(
                totalAlerts = alerts.size,
                activeAlerts = activeAlerts,
                acknowledgedAlerts = acknowledgedAlerts,
                resolvedAlerts = resolvedAlerts,
                criticalAlerts = criticalAlerts,
                errorAlerts = errorAlerts,
                warningAlerts = warningAlerts,
                infoAlerts = infoAlerts,
                byServiceId = byServiceId
            )
        }
    }
} 