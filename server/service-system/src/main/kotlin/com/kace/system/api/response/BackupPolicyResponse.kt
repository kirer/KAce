package com.kace.system.api.response

import com.kace.system.domain.model.BackupPolicy
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 备份策略响应模型
 */
data class BackupPolicyResponse(
    @Schema(description = "策略ID")
    val id: String,

    @Schema(description = "策略名称")
    val name: String,

    @Schema(description = "策略描述")
    val description: String?,

    @Schema(description = "备份类型")
    val backupType: String,

    @Schema(description = "备份服务类型")
    val serviceType: String,

    @Schema(description = "备份的具体服务名称")
    val serviceName: String,

    @Schema(description = "备份计划表达式（Cron表达式）")
    val schedule: String,

    @Schema(description = "备份保留天数")
    val retentionDays: Int,

    @Schema(description = "最大保留数量")
    val maxBackups: Int?,

    @Schema(description = "备份存储路径模板")
    val storagePathTemplate: String,

    @Schema(description = "是否启用备份策略")
    val enabled: Boolean,

    @Schema(description = "是否在备份时压缩")
    val compress: Boolean,

    @Schema(description = "是否在备份时加密")
    val encrypt: Boolean,

    @Schema(description = "加密算法")
    val encryptionAlgorithm: String?,

    @Schema(description = "备份前执行的命令或脚本")
    val preBackupCommand: String?,

    @Schema(description = "备份后执行的命令或脚本")
    val postBackupCommand: String?,

    @Schema(description = "备份窗口开始时间（每天的时间点）")
    val backupWindowStart: LocalTime?,

    @Schema(description = "备份窗口结束时间（每天的时间点）")
    val backupWindowEnd: LocalTime?,

    @Schema(description = "最后执行时间")
    val lastExecuted: LocalDateTime?,

    @Schema(description = "下次计划执行时间")
    val nextScheduled: LocalDateTime?,

    @Schema(description = "创建者ID")
    val createdBy: String,

    @Schema(description = "创建时间")
    val createdAt: LocalDateTime,

    @Schema(description = "更新时间")
    val updatedAt: LocalDateTime
) {
    companion object {
        /**
         * 从领域模型创建响应模型
         */
        fun fromDomain(backupPolicy: BackupPolicy): BackupPolicyResponse {
            return BackupPolicyResponse(
                id = backupPolicy.id,
                name = backupPolicy.name,
                description = backupPolicy.description,
                backupType = backupPolicy.backupType,
                serviceType = backupPolicy.serviceType,
                serviceName = backupPolicy.serviceName,
                schedule = backupPolicy.schedule,
                retentionDays = backupPolicy.retentionDays,
                maxBackups = backupPolicy.maxBackups,
                storagePathTemplate = backupPolicy.storagePathTemplate,
                enabled = backupPolicy.enabled,
                compress = backupPolicy.compress,
                encrypt = backupPolicy.encrypt,
                encryptionAlgorithm = backupPolicy.encryptionAlgorithm,
                preBackupCommand = backupPolicy.preBackupCommand,
                postBackupCommand = backupPolicy.postBackupCommand,
                backupWindowStart = backupPolicy.backupWindowStart,
                backupWindowEnd = backupPolicy.backupWindowEnd,
                lastExecuted = backupPolicy.lastExecuted,
                nextScheduled = backupPolicy.nextScheduled,
                createdBy = backupPolicy.createdBy,
                createdAt = backupPolicy.createdAt,
                updatedAt = backupPolicy.updatedAt
            )
        }
    }
} 