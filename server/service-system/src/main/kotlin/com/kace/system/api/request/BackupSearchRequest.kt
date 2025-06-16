package com.kace.system.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 备份搜索请求模型
 */
data class BackupSearchRequest(
    @Schema(description = "备份类型，如FULL（全量备份）、INCREMENTAL（增量备份）、DIFFERENTIAL（差异备份）", example = "FULL")
    @field:Size(max = 20, message = "备份类型长度不能超过20个字符")
    val type: String? = null,

    @Schema(description = "备份服务类型，如DATABASE（数据库备份）、FILES（文件备份）、CONFIGURATION（配置备份）", example = "DATABASE")
    @field:Size(max = 20, message = "备份服务类型长度不能超过20个字符")
    val serviceType: String? = null,

    @Schema(description = "备份的具体服务名称，如MySQL、Redis、MongoDB等", example = "MySQL")
    @field:Size(max = 50, message = "服务名称长度不能超过50个字符")
    val serviceName: String? = null,

    @Schema(description = "备份状态，如PENDING（待处理）、IN_PROGRESS（处理中）、COMPLETED（已完成）、FAILED（失败）", example = "COMPLETED")
    @field:Size(max = 20, message = "状态长度不能超过20个字符")
    val status: String? = null,

    @Schema(description = "执行备份的用户ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @field:Size(max = 36, message = "用户ID长度不能超过36个字符")
    val createdBy: String? = null,

    @Schema(description = "开始时间", example = "2023-01-01T00:00:00")
    val startTime: LocalDateTime? = null,

    @Schema(description = "结束时间", example = "2023-12-31T23:59:59")
    val endTime: LocalDateTime? = null,

    @Schema(description = "页码（从0开始）", example = "0", defaultValue = "0")
    val page: Int = 0,

    @Schema(description = "每页条数", example = "20", defaultValue = "20")
    val size: Int = 20
) 