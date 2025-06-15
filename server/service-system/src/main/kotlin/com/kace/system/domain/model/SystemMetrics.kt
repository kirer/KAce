package com.kace.system.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 系统指标类型
 */
@Serializable
enum class MetricType {
    CPU_USAGE,       // CPU使用率
    MEMORY_USAGE,    // 内存使用率
    DISK_USAGE,      // 磁盘使用率
    NETWORK_IO,      // 网络IO
    REQUEST_COUNT,   // 请求数量
    ERROR_COUNT,     // 错误数量
    RESPONSE_TIME,   // 响应时间
    DATABASE_CONNECTIONS, // 数据库连接数
    ACTIVE_USERS,    // 活跃用户数
    CUSTOM           // 自定义指标
}

/**
 * 系统指标领域模型
 *
 * @property id 指标ID
 * @property name 指标名称
 * @property type 指标类型
 * @property value 指标值
 * @property unit 单位
 * @property serviceId 服务ID
 * @property timestamp 记录时间戳
 * @property tags 指标标签
 */
@Serializable
data class SystemMetric(
    val id: Long? = null,
    val name: String,
    val type: MetricType,
    val value: Double,
    val unit: String = "",
    val serviceId: String = "system",
    val timestamp: Instant = Instant.DISTANT_PAST,
    val tags: Map<String, String> = emptyMap()
)

/**
 * 系统健康状态
 */
@Serializable
enum class HealthStatus {
    UP,         // 系统正常
    DOWN,       // 系统宕机
    DEGRADED,   // 系统性能下降
    UNKNOWN     // 状态未知
}

/**
 * 系统健康信息领域模型
 *
 * @property id 健康信息ID
 * @property serviceId 服务ID
 * @property status 健康状态
 * @property details 详细信息
 * @property timestamp 记录时间戳
 */
@Serializable
data class SystemHealth(
    val id: Long? = null,
    val serviceId: String,
    val status: HealthStatus,
    val details: Map<String, String> = emptyMap(),
    val timestamp: Instant = Instant.DISTANT_PAST
)

/**
 * 告警级别
 */
@Serializable
enum class AlertLevel {
    INFO,       // 信息
    WARNING,    // 警告
    ERROR,      // 错误
    CRITICAL    // 严重
}

/**
 * 系统告警领域模型
 *
 * @property id 告警ID
 * @property name 告警名称
 * @property level 告警级别
 * @property message 告警消息
 * @property serviceId 服务ID
 * @property metricName 关联的指标名称
 * @property threshold 阈值
 * @property currentValue 当前值
 * @property timestamp 记录时间戳
 * @property acknowledged 是否已确认
 * @property resolvedAt 解决时间
 */
@Serializable
data class SystemAlert(
    val id: Long? = null,
    val name: String,
    val level: AlertLevel,
    val message: String,
    val serviceId: String,
    val metricName: String,
    val threshold: Double,
    val currentValue: Double,
    val timestamp: Instant = Instant.DISTANT_PAST,
    val acknowledged: Boolean = false,
    val resolvedAt: Instant? = null
)

/**
 * 告警规则类型
 */
@Serializable
enum class AlertRuleType {
    THRESHOLD,      // 阈值规则
    RATE_OF_CHANGE, // 变化率规则
    ANOMALY,        // 异常检测规则
    PATTERN         // 模式匹配规则
}

/**
 * 告警规则操作符
 */
@Serializable
enum class AlertRuleOperator {
    GREATER_THAN,           // 大于
    LESS_THAN,              // 小于
    EQUAL_TO,               // 等于
    NOT_EQUAL_TO,           // 不等于
    GREATER_THAN_OR_EQUAL,  // 大于等于
    LESS_THAN_OR_EQUAL      // 小于等于
}

/**
 * 系统告警规则领域模型
 *
 * @property id 规则ID
 * @property name 规则名称
 * @property metricName 关联的指标名称
 * @property type 规则类型
 * @property operator 操作符
 * @property threshold 阈值
 * @property level 告警级别
 * @property enabled 是否启用
 * @property servicePattern 服务ID匹配模式
 * @property consecutiveDataPoints 连续数据点数
 * @property message 告警消息模板
 * @property createdAt 创建时间
 * @property updatedAt 更新时间
 */
@Serializable
data class AlertRule(
    val id: Long? = null,
    val name: String,
    val metricName: String,
    val type: AlertRuleType,
    val operator: AlertRuleOperator,
    val threshold: Double,
    val level: AlertLevel,
    val enabled: Boolean = true,
    val servicePattern: String = "*", // 支持通配符
    val consecutiveDataPoints: Int = 1,
    val message: String = "",
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)