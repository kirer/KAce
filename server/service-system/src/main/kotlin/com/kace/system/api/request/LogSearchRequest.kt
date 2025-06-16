package com.kace.system.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * 系统日志搜索请求
 */
data class LogSearchRequest(
    @Schema(description = "日志类型，如INFO、WARNING、ERROR、DEBUG等", example = "ERROR")
    @field:Size(max = 20, message = "日志类型长度不能超过20个字符")
    val type: String? = null,

    @Schema(description = "所属模块，如USER、AUTH、CONTENT等", example = "AUTH")
    @field:Size(max = 50, message = "模块名称长度不能超过50个字符")
    val module: String? = null,

    @Schema(description = "操作描述关键词", example = "登录")
    @field:Size(max = 100, message = "操作描述长度不能超过100个字符")
    val operation: String? = null,

    @Schema(description = "日志内容关键词", example = "失败")
    val content: String? = null,

    @Schema(description = "用户ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val userId: String? = null,

    @Schema(description = "开始时间", example = "2023-01-01T00:00:00")
    val startTime: LocalDateTime? = null,

    @Schema(description = "结束时间", example = "2023-01-31T23:59:59")
    val endTime: LocalDateTime? = null,

    @Schema(description = "页码（从0开始）", example = "0", defaultValue = "0")
    val page: Int = 0,

    @Schema(description = "每页条数", example = "20", defaultValue = "20")
    val size: Int = 20
) 