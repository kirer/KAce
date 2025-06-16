package com.kace.system.api.request

import com.kace.system.domain.model.AlertLevel
import com.kace.system.domain.model.AlertRuleOperator
import com.kace.system.domain.model.AlertRuleType
import kotlinx.serialization.Serializable

/**
 * 创建告警规则请求
 */
@Serializable
data class CreateAlertRuleRequest(
    val name: String,
    val metricName: String,
    val type: AlertRuleType,
    val operator: AlertRuleOperator,
    val threshold: Double,
    val level: AlertLevel,
    val servicePattern: String = "*",
    val consecutiveDataPoints: Int = 1,
    val message: String = "",
    val enabled: Boolean = true
)

/**
 * 更新告警规则请求
 */
@Serializable
data class UpdateAlertRuleRequest(
    val id: Long,
    val name: String? = null,
    val metricName: String? = null,
    val type: AlertRuleType? = null,
    val operator: AlertRuleOperator? = null,
    val threshold: Double? = null,
    val level: AlertLevel? = null,
    val servicePattern: String? = null,
    val consecutiveDataPoints: Int? = null,
    val message: String? = null,
    val enabled: Boolean? = null
) 