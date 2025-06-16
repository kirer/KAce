package com.kace.system.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 创建备份请求模型
 */
data class CreateBackupRequest(
    @Schema(description = "备份名称", example = "MySQL数据库每日备份", required = true)
    @field:NotBlank(message = "备份名称不能为空")
    @field:Size(max = 100, message = "备份名称长度不能超过100个字符")
    val name: String,

    @Schema(description = "备份描述", example = "包含所有业务表的完整备份")
    @field:Size(max = 500, message = "备份描述长度不能超过500个字符")
    val description: String? = null,

    @Schema(description = "备份类型，如FULL（全量备份）、INCREMENTAL（增量备份）、DIFFERENTIAL（差异备份）", example = "FULL", required = true)
    @field:NotBlank(message = "备份类型不能为空")
    @field:Size(max = 20, message = "备份类型长度不能超过20个字符")
    val type: String,

    @Schema(description = "备份服务类型，如DATABASE（数据库备份）、FILES（文件备份）、CONFIGURATION（配置备份）", example = "DATABASE", required = true)
    @field:NotBlank(message = "备份服务类型不能为空")
    @field:Size(max = 20, message = "备份服务类型长度不能超过20个字符")
    val serviceType: String,

    @Schema(description = "备份的具体服务名称，如MySQL、Redis、MongoDB等", example = "MySQL", required = true)
    @field:NotBlank(message = "服务名称不能为空")
    @field:Size(max = 50, message = "服务名称长度不能超过50个字符")
    val serviceName: String,

    @Schema(description = "是否压缩", example = "true", defaultValue = "true")
    val compress: Boolean = true,

    @Schema(description = "是否加密", example = "false", defaultValue = "false")
    val encrypt: Boolean = false,

    @Schema(description = "加密算法", example = "AES-256")
    @field:Size(max = 50, message = "加密算法长度不能超过50个字符")
    val encryptionAlgorithm: String? = null,
    
    @Schema(description = "保留天数", example = "30", defaultValue = "30")
    val retentionDays: Int = 30,

    @Schema(description = "额外参数", example = "{\"database\": \"kace_db\", \"username\": \"backup_user\"}")
    val parameters: Map<String, Any>? = null
) 