package com.kace.system.api.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 日志统计响应模型
 */
data class LogStatisticsResponse(
    @Schema(description = "统计起始时间")
    val startTime: LocalDateTime,

    @Schema(description = "统计结束时间")
    val endTime: LocalDateTime,

    @Schema(description = "按类型统计的日志数量")
    val countByType: Map<String, Long>,

    @Schema(description = "按模块统计的日志数量")
    val countByModule: Map<String, Long>,

    @Schema(description = "总日志数量")
    val totalCount: Long,

    @Schema(description = "错误日志数量")
    val errorCount: Long,

    @Schema(description = "警告日志数量")
    val warningCount: Long
) 