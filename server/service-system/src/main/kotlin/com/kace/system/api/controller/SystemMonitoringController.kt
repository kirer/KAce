package com.kace.system.api.controller

import com.kace.common.api.response.ApiResponse
import com.kace.common.api.response.DataResponse
import com.kace.common.api.response.ListResponse
import com.kace.common.api.response.PageResponse
import com.kace.system.api.request.*
import com.kace.system.api.response.*
import com.kace.system.domain.model.SystemAlert
import com.kace.system.domain.model.SystemHealth
import com.kace.system.domain.model.SystemMetric
import com.kace.system.domain.service.SystemMonitoringService
import kotlinx.datetime.Clock
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*

/**
 * 系统监控控制器
 */
@RestController
@RequestMapping("/api/v1/system/monitoring")
class SystemMonitoringController(
    private val monitoringService: SystemMonitoringService
) {
    /**
     * 记录系统指标
     */
    @PostMapping("/metrics")
    suspend fun recordMetric(@RequestBody request: MetricRecordRequest): DataResponse<SystemMetricResponse> {
        val metric = SystemMetric(
            name = request.name,
            type = request.type,
            value = request.value,
            unit = request.unit,
            serviceId = request.serviceId,
            timestamp = request.timestamp ?: Clock.System.now(),
            tags = request.tags
        )
        
        val savedMetric = monitoringService.recordMetric(metric)
        return DataResponse.success(SystemMetricResponse.fromDomain(savedMetric))
    }
    
    /**
     * 批量记录系统指标
     */
    @PostMapping("/metrics/batch")
    suspend fun recordMetrics(@RequestBody request: BatchMetricRecordRequest): ListResponse<SystemMetricResponse> {
        val metrics = request.metrics.map { metricRequest ->
            SystemMetric(
                name = metricRequest.name,
                type = metricRequest.type,
                value = metricRequest.value,
                unit = metricRequest.unit,
                serviceId = metricRequest.serviceId,
                timestamp = metricRequest.timestamp ?: Clock.System.now(),
                tags = metricRequest.tags
            )
        }
        
        val savedMetrics = monitoringService.recordMetrics(metrics)
        return ListResponse.success(savedMetrics.map { SystemMetricResponse.fromDomain(it) })
    }
    
    /**
     * 获取最新指标
     */
    @GetMapping("/metrics/latest")
    suspend fun getLatestMetric(
        @RequestParam name: String,
        @RequestParam(defaultValue = "system") serviceId: String
    ): DataResponse<SystemMetricResponse> {
        val metric = monitoringService.getLatestMetric(name, serviceId)
            ?: return DataResponse.error("指标不存在")
        
        return DataResponse.success(SystemMetricResponse.fromDomain(metric))
    }
    
    /**
     * 获取服务的所有最新指标
     */
    @GetMapping("/metrics/service/{serviceId}")
    suspend fun getLatestMetricsByService(@PathVariable serviceId: String): ListResponse<SystemMetricResponse> {
        val metrics = monitoringService.getLatestMetrics(serviceId)
        return ListResponse.success(metrics.map { SystemMetricResponse.fromDomain(it) })
    }
    
    /**
     * 获取所有服务的最新指标
     */
    @GetMapping("/metrics/all")
    suspend fun getAllLatestMetrics(): DataResponse<Map<String, List<SystemMetricResponse>>> {
        val metricsMap = monitoringService.getAllLatestMetrics()
        val responseMap = metricsMap.mapValues { (_, metrics) -> metrics.map { SystemMetricResponse.fromDomain(it) } }
        return DataResponse.success(responseMap)
    }
    
    /**
     * 查询指标历史
     */
    @PostMapping("/metrics/history")
    suspend fun queryMetricHistory(@RequestBody request: MetricQueryRequest): ListResponse<SystemMetricResponse> {
        val metrics = monitoringService.queryMetricHistory(
            name = request.name,
            serviceId = request.serviceId,
            startTime = request.startTime,
            endTime = request.endTime,
            limit = request.limit
        )
        
        return ListResponse.success(metrics.map { SystemMetricResponse.fromDomain(it) })
    }
    
    /**
     * 获取指标统计
     */
    @PostMapping("/metrics/statistics")
    suspend fun getMetricStatistics(@RequestBody request: MetricStatisticsRequest): DataResponse<MetricStatisticsResponse> {
        val statistics = monitoringService.getMetricStatistics(
            name = request.name,
            serviceId = request.serviceId,
            startTime = request.startTime,
            endTime = request.endTime
        )
        
        val response = MetricStatisticsResponse.fromStatistics(
            statistics = statistics,
            name = request.name,
            serviceId = request.serviceId,
            startTime = request.startTime,
            endTime = request.endTime
        )
        
        return DataResponse.success(response)
    }
    
    /**
     * 更新健康状态
     */
    @PostMapping("/health")
    suspend fun updateHealthStatus(@RequestBody request: SystemHealthUpdateRequest): DataResponse<SystemHealthResponse> {
        val health = SystemHealth(
            serviceId = request.serviceId,
            status = request.status,
            details = request.details,
            timestamp = request.timestamp ?: Clock.System.now()
        )
        
        val savedHealth = monitoringService.updateHealthStatus(health)
        return DataResponse.success(SystemHealthResponse.fromDomain(savedHealth))
    }
    
    /**
     * 获取所有健康状态
     */
    @GetMapping("/health")
    suspend fun getAllHealthStatus(): DataResponse<HealthOverviewResponse> {
        val healthList = monitoringService.getAllHealthStatus()
        return DataResponse.success(HealthOverviewResponse.fromHealthList(healthList))
    }
    
    /**
     * 获取指定服务的健康状态
     */
    @GetMapping("/health/{serviceId}")
    suspend fun getHealthStatus(@PathVariable serviceId: String): DataResponse<SystemHealthResponse> {
        val health = monitoringService.getHealthStatus(serviceId)
            ?: return DataResponse.error("服务健康状态不存在")
        
        return DataResponse.success(SystemHealthResponse.fromDomain(health))
    }
    
    /**
     * 创建告警规则
     */
    @PostMapping("/alerts/rules")
    suspend fun createAlertRule(@RequestBody request: CreateAlertRuleRequest): DataResponse<AlertRuleResponse> {
        val rule = monitoringService.createAlertRule(
            com.kace.system.domain.model.AlertRule(
                name = request.name,
                metricName = request.metricName,
                type = request.type,
                operator = request.operator,
                threshold = request.threshold,
                level = request.level,
                servicePattern = request.servicePattern,
                consecutiveDataPoints = request.consecutiveDataPoints,
                message = request.message,
                enabled = request.enabled
            )
        )
        
        return DataResponse.success(AlertRuleResponse.fromDomain(rule))
    }
    
    /**
     * 更新告警规则
     */
    @PutMapping("/alerts/rules/{ruleId}")
    suspend fun updateAlertRule(
        @PathVariable ruleId: Long,
        @RequestBody request: UpdateAlertRuleRequest
    ): DataResponse<AlertRuleResponse> {
        val existingRule = monitoringService.getEnabledAlertRules().find { it.id == ruleId }
            ?: return DataResponse.error("告警规则不存在")
        
        val updatedRule = existingRule.copy(
            name = request.name ?: existingRule.name,
            metricName = request.metricName ?: existingRule.metricName,
            type = request.type ?: existingRule.type,
            operator = request.operator ?: existingRule.operator,
            threshold = request.threshold ?: existingRule.threshold,
            level = request.level ?: existingRule.level,
            servicePattern = request.servicePattern ?: existingRule.servicePattern,
            consecutiveDataPoints = request.consecutiveDataPoints ?: existingRule.consecutiveDataPoints,
            message = request.message ?: existingRule.message,
            enabled = request.enabled ?: existingRule.enabled
        )
        
        val savedRule = monitoringService.updateAlertRule(updatedRule)
        return DataResponse.success(AlertRuleResponse.fromDomain(savedRule))
    }
    
    /**
     * 启用告警规则
     */
    @PutMapping("/alerts/rules/{ruleId}/enable")
    suspend fun enableAlertRule(@PathVariable ruleId: Long): ApiResponse {
        val rule = monitoringService.enableAlertRule(ruleId)
            ?: return ApiResponse.error("告警规则不存在")
        
        return ApiResponse.success("告警规则已启用")
    }
    
    /**
     * 禁用告警规则
     */
    @PutMapping("/alerts/rules/{ruleId}/disable")
    suspend fun disableAlertRule(@PathVariable ruleId: Long): ApiResponse {
        val rule = monitoringService.disableAlertRule(ruleId)
            ?: return ApiResponse.error("告警规则不存在")
        
        return ApiResponse.success("告警规则已禁用")
    }
    
    /**
     * 删除告警规则
     */
    @DeleteMapping("/alerts/rules/{ruleId}")
    suspend fun deleteAlertRule(@PathVariable ruleId: Long): ApiResponse {
        val success = monitoringService.deleteAlertRule(ruleId)
        return if (success) {
            ApiResponse.success("告警规则已删除")
        } else {
            ApiResponse.error("告警规则不存在或删除失败")
        }
    }
    
    /**
     * 获取所有告警规则
     */
    @GetMapping("/alerts/rules")
    suspend fun getAllAlertRules(): ListResponse<AlertRuleResponse> {
        val rules = monitoringService.getAllAlertRules()
        return ListResponse.success(rules.map { AlertRuleResponse.fromDomain(it) })
    }
    
    /**
     * 获取所有启用的告警规则
     */
    @GetMapping("/alerts/rules/enabled")
    suspend fun getEnabledAlertRules(): ListResponse<AlertRuleResponse> {
        val rules = monitoringService.getEnabledAlertRules()
        return ListResponse.success(rules.map { AlertRuleResponse.fromDomain(it) })
    }
    
    /**
     * 获取所有活跃告警
     */
    @GetMapping("/alerts")
    suspend fun getActiveAlerts(): ListResponse<SystemAlertResponse> {
        val alerts = monitoringService.getActiveAlerts()
        return ListResponse.success(alerts.map { SystemAlertResponse.fromDomain(it) })
    }
    
    /**
     * 确认告警
     */
    @PutMapping("/alerts/{alertId}/acknowledge")
    suspend fun acknowledgeAlert(@PathVariable alertId: Long): ApiResponse {
        val alert = monitoringService.acknowledgeAlert(alertId)
            ?: return ApiResponse.error("告警不存在")
        
        return ApiResponse.success("告警已确认")
    }
    
    /**
     * 解决告警
     */
    @PutMapping("/alerts/{alertId}/resolve")
    suspend fun resolveAlert(@PathVariable alertId: Long): ApiResponse {
        val alert = monitoringService.resolveAlert(alertId)
            ?: return ApiResponse.error("告警不存在")
        
        return ApiResponse.success("告警已解决")
    }
    
    /**
     * 删除告警
     */
    @DeleteMapping("/alerts/{alertId}")
    suspend fun deleteAlert(@PathVariable alertId: Long): ApiResponse {
        val success = monitoringService.deleteAlert(alertId)
        return if (success) {
            ApiResponse.success("告警已删除")
        } else {
            ApiResponse.error("告警不存在或删除失败")
        }
    }
    
    /**
     * 系统维护
     */
    @PostMapping("/maintenance")
    suspend fun performMaintenance(
        @RequestParam(defaultValue = "30") metricRetentionDays: Int,
        @RequestParam(defaultValue = "90") alertRetentionDays: Int
    ): ApiResponse {
        val deletedCount = monitoringService.performMaintenance(metricRetentionDays, alertRetentionDays)
        return ApiResponse.success("系统维护成功完成，删除了 $deletedCount 条过期记录")
    }
} 