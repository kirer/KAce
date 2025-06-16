package com.kace.system.api

import com.kace.system.api.controller.SystemConfigController
import com.kace.system.api.controller.SystemLogController
import com.kace.system.api.controller.SystemBackupController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * API 路由常量
 */
object Routes {
    // 系统配置路由
    const val CONFIG_BASE = "/api/v1/system/config"
    const val CONFIG_GET_ALL = "$CONFIG_BASE"
    const val CONFIG_GET = "$CONFIG_BASE/{key}"
    const val CONFIG_CREATE = "$CONFIG_BASE"
    const val CONFIG_UPDATE = "$CONFIG_BASE/{key}"
    const val CONFIG_DELETE = "$CONFIG_BASE/{key}"

    // 系统日志路由
    const val LOG_BASE = "/api/v1/system/logs"
    const val LOG_CREATE = "$LOG_BASE"
    const val LOG_SEARCH = "$LOG_BASE/search"
    const val LOG_GET = "$LOG_BASE/{id}"
    const val LOG_DELETE = "$LOG_BASE/{id}"
    const val LOG_STATISTICS = "$LOG_BASE/statistics"

    // 系统备份路由
    const val BACKUP_BASE = "/api/v1/system/backup"
    const val BACKUP_CREATE = "$BACKUP_BASE"
    const val BACKUP_SEARCH = "$BACKUP_BASE/search"
    const val BACKUP_GET = "$BACKUP_BASE/{id}"
    const val BACKUP_DELETE = "$BACKUP_BASE/{id}"
    const val BACKUP_RESTORE = "$BACKUP_BASE/{id}/restore"
    const val BACKUP_DOWNLOAD = "$BACKUP_BASE/{id}/download"
    
    // 备份策略路由
    const val BACKUP_POLICY_BASE = "/api/v1/system/backup/policy"
    const val BACKUP_POLICY_CREATE = "$BACKUP_POLICY_BASE"
    const val BACKUP_POLICY_GET_ALL = "$BACKUP_POLICY_BASE"
    const val BACKUP_POLICY_GET = "$BACKUP_POLICY_BASE/{id}"
    const val BACKUP_POLICY_UPDATE = "$BACKUP_POLICY_BASE/{id}"
    const val BACKUP_POLICY_DELETE = "$BACKUP_POLICY_BASE/{id}"
    
    // 系统监控路由
    const val MONITORING_BASE = "/api/v1/system/monitoring"
    
    // 指标相关路由
    const val METRIC_RECORD = "$MONITORING_BASE/metrics"
    const val METRIC_BATCH_RECORD = "$MONITORING_BASE/metrics/batch"
    const val METRIC_GET_LATEST = "$MONITORING_BASE/metrics/latest"
    const val METRIC_GET_SERVICE = "$MONITORING_BASE/metrics/service/{serviceId}"
    const val METRIC_GET_ALL = "$MONITORING_BASE/metrics/all"
    const val METRIC_HISTORY = "$MONITORING_BASE/metrics/history"
    const val METRIC_STATISTICS = "$MONITORING_BASE/metrics/statistics"
    
    // 健康状态路由
    const val HEALTH_UPDATE = "$MONITORING_BASE/health"
    const val HEALTH_GET_ALL = "$MONITORING_BASE/health"
    const val HEALTH_GET = "$MONITORING_BASE/health/{serviceId}"
    
    // 告警规则路由
    const val ALERT_RULE_CREATE = "$MONITORING_BASE/alerts/rules"
    const val ALERT_RULE_UPDATE = "$MONITORING_BASE/alerts/rules/{ruleId}"
    const val ALERT_RULE_ENABLE = "$MONITORING_BASE/alerts/rules/{ruleId}/enable"
    const val ALERT_RULE_DISABLE = "$MONITORING_BASE/alerts/rules/{ruleId}/disable"
    const val ALERT_RULE_DELETE = "$MONITORING_BASE/alerts/rules/{ruleId}"
    const val ALERT_RULE_GET_ALL = "$MONITORING_BASE/alerts/rules"
    const val ALERT_RULE_GET_ENABLED = "$MONITORING_BASE/alerts/rules/enabled"
    
    // 告警路由
    const val ALERT_GET_ACTIVE = "$MONITORING_BASE/alerts"
    const val ALERT_ACKNOWLEDGE = "$MONITORING_BASE/alerts/{alertId}/acknowledge"
    const val ALERT_RESOLVE = "$MONITORING_BASE/alerts/{alertId}/resolve"
    const val ALERT_DELETE = "$MONITORING_BASE/alerts/{alertId}"
    
    // 系统维护
    const val SYSTEM_MAINTENANCE = "$MONITORING_BASE/maintenance"
}

/**
 * 配置API路由
 */
fun Application.configureRouting() {
    routing {
        route("/api/system") {
            // 系统配置API
            val systemConfigController by inject<SystemConfigController>()
            systemConfigController.registerRoutes(this)
            
            // 系统日志API
            val systemLogController by inject<SystemLogController>()
            route("/logs") {
                // 获取日志API可以在此添加
                // 注：由于已经在SystemLogController中使用了Spring MVC的注解式路由，
                // 这里不需要额外配置，仅作为说明
            }
            
            // 系统备份API
            val systemBackupController by inject<SystemBackupController>()
            route("/backups") {
                // 备份API可以在此添加
                // 注：由于已经在SystemBackupController中使用了Spring MVC的注解式路由，
                // 这里不需要额外配置，仅作为说明
            }
            
            // 其他系统服务API可以添加在这里
        }
    }
}