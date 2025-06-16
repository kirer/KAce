package com.kace.system.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.LocalDateTime

/**
 * 系统日志实体类
 */
@Entity
@Table(
    name = "system_logs",
    indexes = [
        Index(name = "idx_system_logs_type", columnList = "type"),
        Index(name = "idx_system_logs_module", columnList = "module"),
        Index(name = "idx_system_logs_user_id", columnList = "user_id"),
        Index(name = "idx_system_logs_created_at", columnList = "created_at")
    ]
)
class SystemLogs(

    /**
     * 日志唯一标识符
     */
    @Id
    @Column(name = "id", length = 36, nullable = false)
    val id: String,

    /**
     * 日志类型，如INFO、WARNING、ERROR、DEBUG等
     */
    @Column(name = "type", length = 20, nullable = false)
    val type: String,

    /**
     * 所属模块，如USER、AUTH、CONTENT等
     */
    @Column(name = "module", length = 50, nullable = false)
    val module: String,

    /**
     * 操作描述，简短说明事件
     */
    @Column(name = "operation", length = 100, nullable = false)
    val operation: String,

    /**
     * 详细日志内容，可包含JSON、错误堆栈等详细信息
     */
    @Column(name = "content", columnDefinition = "TEXT")
    @Lob
    val content: String,

    /**
     * 执行操作的用户ID，如果是系统操作可为空
     */
    @Column(name = "user_id", length = 36)
    val userId: String?,

    /**
     * 客户端IP地址，记录请求来源
     */
    @Column(name = "client_ip", length = 50)
    val clientIp: String?,

    /**
     * 执行时间，记录操作耗时（毫秒）
     */
    @Column(name = "execution_time")
    val executionTime: Long?,

    /**
     * 事件状态，如SUCCESS、FAILED等
     */
    @Column(name = "status", length = 20)
    val status: String?,

    /**
     * 额外参数，存储不适合放入标准字段的扩展信息
     */
    @Column(name = "extra_params", columnDefinition = "TEXT")
    val extraParams: String?,

    /**
     * 日志创建时间
     */
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
) 