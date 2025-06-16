package com.kace.system.domain.model

import java.time.LocalDateTime

/**
 * 系统日志领域模型
 * 记录系统操作、事件和错误信息
 */
data class SystemLog(
    /**
     * 日志唯一标识符
     */
    val id: String,
    
    /**
     * 日志类型，如INFO、WARNING、ERROR、DEBUG等
     */
    val type: String,
    
    /**
     * 所属模块，如USER、AUTH、CONTENT等
     */
    val module: String,
    
    /**
     * 操作描述，简短说明事件
     */
    val operation: String,
    
    /**
     * 详细日志内容，可包含JSON、错误堆栈等详细信息
     */
    val content: String,
    
    /**
     * 执行操作的用户ID，如果是系统操作可为空
     */
    val userId: String?,
    
    /**
     * 客户端IP地址，记录请求来源
     */
    val clientIp: String?,
    
    /**
     * 执行时间，记录操作耗时（毫秒）
     */
    val executionTime: Long?,
    
    /**
     * 事件状态，如SUCCESS、FAILED等
     */
    val status: String?,
    
    /**
     * 日志创建时间
     */
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    /**
     * 额外参数，存储不适合放入标准字段的扩展信息
     */
    val extraParams: Map<String, Any>? = null
) {
    companion object {
        // 日志类型常量
        const val TYPE_INFO = "INFO"
        const val TYPE_WARNING = "WARNING"
        const val TYPE_ERROR = "ERROR"
        const val TYPE_DEBUG = "DEBUG"
        const val TYPE_SECURITY = "SECURITY"
        const val TYPE_AUDIT = "AUDIT"
        
        // 日志状态常量
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_FAILED = "FAILED"
        const val STATUS_PENDING = "PENDING"
    }
    
    /**
     * 判断是否为错误日志
     */
    fun isError(): Boolean {
        return type == TYPE_ERROR
    }
    
    /**
     * 判断是否为安全相关日志
     */
    fun isSecurity(): Boolean {
        return type == TYPE_SECURITY
    }
    
    /**
     * 判断是否为审计日志
     */
    fun isAudit(): Boolean {
        return type == TYPE_AUDIT
    }
    
    /**
     * 判断操作是否成功
     */
    fun isSuccess(): Boolean {
        return status == STATUS_SUCCESS
    }
} 