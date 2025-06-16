package com.kace.system.api.response

import com.kace.system.domain.model.SystemLog
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 系统日志响应模型
 */
data class SystemLogResponse(
    @Schema(description = "日志ID")
    val id: String,

    @Schema(description = "日志类型，如INFO、WARNING、ERROR、DEBUG等")
    val type: String,

    @Schema(description = "所属模块，如USER、AUTH、CONTENT等")
    val module: String,

    @Schema(description = "操作描述，简短说明事件")
    val operation: String,

    @Schema(description = "详细日志内容")
    val content: String,

    @Schema(description = "执行操作的用户ID")
    val userId: String?,

    @Schema(description = "客户端IP地址")
    val clientIp: String?,

    @Schema(description = "执行时间（毫秒）")
    val executionTime: Long?,

    @Schema(description = "事件状态，如SUCCESS、FAILED等")
    val status: String?,

    @Schema(description = "日志创建时间")
    val createdAt: LocalDateTime,

    @Schema(description = "额外参数")
    val extraParams: Map<String, Any>?
) {
    companion object {
        /**
         * 从领域模型创建响应模型
         */
        fun fromDomain(systemLog: SystemLog): SystemLogResponse {
            return SystemLogResponse(
                id = systemLog.id,
                type = systemLog.type,
                module = systemLog.module,
                operation = systemLog.operation,
                content = systemLog.content,
                userId = systemLog.userId,
                clientIp = systemLog.clientIp,
                executionTime = systemLog.executionTime,
                status = systemLog.status,
                createdAt = systemLog.createdAt,
                extraParams = systemLog.extraParams
            )
        }
    }
} 