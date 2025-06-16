package com.kace.system.api.request

import com.kace.system.domain.model.SystemLog
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 记录系统日志请求
 */
data class LogEventRequest(
    @Schema(description = "日志类型，如INFO、WARNING、ERROR、DEBUG等", example = "INFO", required = true)
    @field:NotBlank(message = "日志类型不能为空")
    @field:Size(max = 20, message = "日志类型长度不能超过20个字符")
    val type: String,

    @Schema(description = "所属模块，如USER、AUTH、CONTENT等", example = "USER", required = true)
    @field:NotBlank(message = "模块名称不能为空")
    @field:Size(max = 50, message = "模块名称长度不能超过50个字符")
    val module: String,

    @Schema(description = "操作描述，简短说明事件", example = "用户登录", required = true)
    @field:NotBlank(message = "操作描述不能为空")
    @field:Size(max = 100, message = "操作描述长度不能超过100个字符")
    val operation: String,

    @Schema(description = "详细日志内容，可包含JSON、错误堆栈等详细信息", example = "用户admin尝试登录系统", required = true)
    @field:NotBlank(message = "日志内容不能为空")
    val content: String,

    @Schema(description = "执行操作的用户ID，如果是系统操作可为空", example = "550e8400-e29b-41d4-a716-446655440000")
    val userId: String? = null,

    @Schema(description = "客户端IP地址，记录请求来源", example = "192.168.1.1")
    val clientIp: String? = null,

    @Schema(description = "执行时间，记录操作耗时（毫秒）", example = "150")
    val executionTime: Long? = null,

    @Schema(description = "事件状态，如SUCCESS、FAILED等", example = "SUCCESS")
    val status: String? = SystemLog.STATUS_SUCCESS,

    @Schema(description = "额外参数，存储不适合放入标准字段的扩展信息")
    val extraParams: Map<String, Any>? = null
) 