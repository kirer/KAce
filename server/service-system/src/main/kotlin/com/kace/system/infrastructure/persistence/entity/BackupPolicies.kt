package com.kace.system.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 备份策略实体类
 */
@Entity
@Table(
    name = "backup_policies",
    indexes = [
        Index(name = "idx_backup_policies_service_type", columnList = "service_type"),
        Index(name = "idx_backup_policies_service_name", columnList = "service_name"),
        Index(name = "idx_backup_policies_enabled", columnList = "enabled"),
        Index(name = "idx_backup_policies_next_scheduled", columnList = "next_scheduled")
    ]
)
class BackupPolicies(
    /**
     * 策略ID
     */
    @Id
    @Column(name = "id", length = 36, nullable = false)
    val id: String,

    /**
     * 策略名称
     */
    @Column(name = "name", length = 100, nullable = false)
    val name: String,

    /**
     * 策略描述
     */
    @Column(name = "description", length = 500)
    val description: String?,

    /**
     * 备份类型
     */
    @Column(name = "backup_type", length = 20, nullable = false)
    val backupType: String,

    /**
     * 备份服务类型
     */
    @Column(name = "service_type", length = 20, nullable = false)
    val serviceType: String,

    /**
     * 备份的具体服务名称
     */
    @Column(name = "service_name", length = 50, nullable = false)
    val serviceName: String,

    /**
     * 备份计划表达式（Cron表达式）
     */
    @Column(name = "schedule", length = 50, nullable = false)
    val schedule: String,

    /**
     * 备份保留天数
     */
    @Column(name = "retention_days", nullable = false)
    val retentionDays: Int,

    /**
     * 最大保留数量
     */
    @Column(name = "max_backups")
    val maxBackups: Int?,

    /**
     * 备份存储路径模板
     */
    @Column(name = "storage_path_template", length = 500, nullable = false)
    val storagePathTemplate: String,

    /**
     * 是否启用备份策略
     */
    @Column(name = "enabled", nullable = false)
    val enabled: Boolean,

    /**
     * 是否在备份时压缩
     */
    @Column(name = "compress", nullable = false)
    val compress: Boolean,

    /**
     * 是否在备份时加密
     */
    @Column(name = "encrypt", nullable = false)
    val encrypt: Boolean,

    /**
     * 加密算法
     */
    @Column(name = "encryption_algorithm", length = 50)
    val encryptionAlgorithm: String?,

    /**
     * 备份前执行的命令或脚本
     */
    @Column(name = "pre_backup_command", length = 500)
    val preBackupCommand: String?,

    /**
     * 备份后执行的命令或脚本
     */
    @Column(name = "post_backup_command", length = 500)
    val postBackupCommand: String?,

    /**
     * 备份窗口开始时间（每天的时间点）
     */
    @Column(name = "backup_window_start")
    val backupWindowStart: LocalTime?,

    /**
     * 备份窗口结束时间（每天的时间点）
     */
    @Column(name = "backup_window_end")
    val backupWindowEnd: LocalTime?,

    /**
     * 最后执行时间
     */
    @Column(name = "last_executed")
    val lastExecuted: LocalDateTime?,

    /**
     * 下次计划执行时间
     */
    @Column(name = "next_scheduled")
    val nextScheduled: LocalDateTime?,

    /**
     * 创建者ID
     */
    @Column(name = "created_by", length = 36, nullable = false)
    val createdBy: String,

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime,

    /**
     * 额外参数，存储为JSON
     */
    @Column(name = "parameters", columnDefinition = "TEXT")
    @Lob
    val parameters: String?
) 