package com.kace.system.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalTime

/**
 * 创建备份策略请求模型
 */
data class CreateBackupPolicyRequest(
    @Schema(description = "策略名称", example = "MySQL每日备份策略", required = true)
    @field:NotBlank(message = "策略名称不能为空")
    @field:Size(max = 100, message = "策略名称长度不能超过100个字符")
    val name: String,

    @Schema(description = "策略描述", example = "每天凌晨1点执行MySQL全量备份")
    @field:Size(max = 500, message = "策略描述长度不能超过500个字符")
    val description: String? = null,

    @Schema(description = "备份类型，如FULL（全量备份）、INCREMENTAL（增量备份）、DIFFERENTIAL（差异备份）", example = "FULL", required = true)
    @field:NotBlank(message = "备份类型不能为空")
    @field:Size(max = 20, message = "备份类型长度不能超过20个字符")
    val backupType: String,

    @Schema(description = "备份服务类型，如DATABASE（数据库备份）、FILES（文件备份）、CONFIGURATION（配置备份）", example = "DATABASE", required = true)
    @field:NotBlank(message = "备份服务类型不能为空")
    @field:Size(max = 20, message = "备份服务类型长度不能超过20个字符")
    val serviceType: String,

    @Schema(description = "备份的具体服务名称，如MySQL、Redis、MongoDB等", example = "MySQL", required = true)
    @field:NotBlank(message = "服务名称不能为空")
    @field:Size(max = 50, message = "服务名称长度不能超过50个字符")
    val serviceName: String,

    @Schema(description = "备份计划表达式（Cron表达式）", example = "0 0 1 * * ?", required = true)
    @field:NotBlank(message = "备份计划表达式不能为空")
    @field:Size(max = 50, message = "备份计划表达式长度不能超过50个字符")
    val schedule: String,

    @Schema(description = "备份保留天数", example = "30", required = true)
    @field:Positive(message = "保留天数必须大于0")
    val retentionDays: Int,

    @Schema(description = "最大保留数量，超过此数量时，将删除最旧的备份", example = "10")
    val maxBackups: Int? = null,

    @Schema(description = "备份存储路径模板，支持变量如 {date}, {service}, {type} 等", example = "/backup/{service}/{date}/{type}", required = true)
    @field:NotBlank(message = "存储路径模板不能为空")
    @field:Size(max = 500, message = "存储路径模板长度不能超过500个字符")
    val storagePathTemplate: String,

    @Schema(description = "是否启用备份策略", example = "true", defaultValue = "true")
    val enabled: Boolean = true,

    @Schema(description = "是否在备份时压缩", example = "true", defaultValue = "true")
    val compress: Boolean = true,

    @Schema(description = "是否在备份时加密", example = "false", defaultValue = "false")
    val encrypt: Boolean = false,

    @Schema(description = "加密算法", example = "AES-256")
    @field:Size(max = 50, message = "加密算法长度不能超过50个字符")
    val encryptionAlgorithm: String? = null,

    @Schema(description = "备份前执行的命令或脚本", example = "echo 'Starting backup' >> /var/log/backup.log")
    @field:Size(max = 500, message = "命令或脚本长度不能超过500个字符")
    val preBackupCommand: String? = null,

    @Schema(description = "备份后执行的命令或脚本", example = "echo 'Backup completed' >> /var/log/backup.log")
    @field:Size(max = 500, message = "命令或脚本长度不能超过500个字符")
    val postBackupCommand: String? = null,

    @Schema(description = "备份窗口开始时间（每天的时间点）", example = "01:00")
    val backupWindowStart: LocalTime? = null,

    @Schema(description = "备份窗口结束时间（每天的时间点）", example = "05:00")
    val backupWindowEnd: LocalTime? = null,

    @Schema(description = "额外参数", example = "{\"database\": \"kace_db\", \"username\": \"backup_user\"}")
    val parameters: Map<String, Any>? = null
) 