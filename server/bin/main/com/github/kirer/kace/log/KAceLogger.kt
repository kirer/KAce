package com.github.kirer.kace.log

import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.event.Level
import java.time.LocalDateTime

/**
 * KAce框架自定义日志记录器
 * 封装SLF4J Logger，添加额外的日志类型和上下文信息
 */
class KAceLogger(
    private val logger: Logger,
    private val type: LogType
) : Logger {
    // 实现Logger接口的方法
    override fun getName(): String = logger.name
    
    override fun isTraceEnabled(): Boolean = logger.isTraceEnabled
    override fun isTraceEnabled(marker: Marker): Boolean = logger.isTraceEnabled(marker)
    override fun trace(msg: String) = logger.trace(msg)
    override fun trace(format: String, arg: Any) = logger.trace(format, arg)
    override fun trace(format: String, arg1: Any, arg2: Any) = logger.trace(format, arg1, arg2)
    override fun trace(format: String, vararg arguments: Any) = logger.trace(format, *arguments)
    override fun trace(msg: String, t: Throwable) = logger.trace(msg, t)
    override fun trace(marker: Marker, msg: String) = logger.trace(marker, msg)
    override fun trace(marker: Marker, format: String, arg: Any) = logger.trace(marker, format, arg)
    override fun trace(marker: Marker, format: String, arg1: Any, arg2: Any) = logger.trace(marker, format, arg1, arg2)
    override fun trace(marker: Marker, format: String, vararg argArray: Any) = logger.trace(marker, format, *argArray)
    override fun trace(marker: Marker, msg: String, t: Throwable) = logger.trace(marker, msg, t)
    
    override fun isDebugEnabled(): Boolean = logger.isDebugEnabled
    override fun isDebugEnabled(marker: Marker): Boolean = logger.isDebugEnabled(marker)
    override fun debug(msg: String) = logger.debug(msg)
    override fun debug(format: String, arg: Any) = logger.debug(format, arg)
    override fun debug(format: String, arg1: Any, arg2: Any) = logger.debug(format, arg1, arg2)
    override fun debug(format: String, vararg arguments: Any) = logger.debug(format, *arguments)
    override fun debug(msg: String, t: Throwable) = logger.debug(msg, t)
    override fun debug(marker: Marker, msg: String) = logger.debug(marker, msg)
    override fun debug(marker: Marker, format: String, arg: Any) = logger.debug(marker, format, arg)
    override fun debug(marker: Marker, format: String, arg1: Any, arg2: Any) = logger.debug(marker, format, arg1, arg2)
    override fun debug(marker: Marker, format: String, vararg argArray: Any) = logger.debug(marker, format, *argArray)
    override fun debug(marker: Marker, msg: String, t: Throwable) = logger.debug(marker, msg, t)
    
    override fun isInfoEnabled(): Boolean = logger.isInfoEnabled
    override fun isInfoEnabled(marker: Marker): Boolean = logger.isInfoEnabled(marker)
    override fun info(msg: String) = logger.info(msg)
    override fun info(format: String, arg: Any) = logger.info(format, arg)
    override fun info(format: String, arg1: Any, arg2: Any) = logger.info(format, arg1, arg2)
    override fun info(format: String, vararg arguments: Any) = logger.info(format, *arguments)
    override fun info(msg: String, t: Throwable) = logger.info(msg, t)
    override fun info(marker: Marker, msg: String) = logger.info(marker, msg)
    override fun info(marker: Marker, format: String, arg: Any) = logger.info(marker, format, arg)
    override fun info(marker: Marker, format: String, arg1: Any, arg2: Any) = logger.info(marker, format, arg1, arg2)
    override fun info(marker: Marker, format: String, vararg argArray: Any) = logger.info(marker, format, *argArray)
    override fun info(marker: Marker, msg: String, t: Throwable) = logger.info(marker, msg, t)
    
    override fun isWarnEnabled(): Boolean = logger.isWarnEnabled
    override fun isWarnEnabled(marker: Marker): Boolean = logger.isWarnEnabled(marker)
    override fun warn(msg: String) = logger.warn(msg)
    override fun warn(format: String, arg: Any) = logger.warn(format, arg)
    override fun warn(format: String, arg1: Any, arg2: Any) = logger.warn(format, arg1, arg2)
    override fun warn(format: String, vararg arguments: Any) = logger.warn(format, *arguments)
    override fun warn(msg: String, t: Throwable) = logger.warn(msg, t)
    override fun warn(marker: Marker, msg: String) = logger.warn(marker, msg)
    override fun warn(marker: Marker, format: String, arg: Any) = logger.warn(marker, format, arg)
    override fun warn(marker: Marker, format: String, arg1: Any, arg2: Any) = logger.warn(marker, format, arg1, arg2)
    override fun warn(marker: Marker, format: String, vararg argArray: Any) = logger.warn(marker, format, *argArray)
    override fun warn(marker: Marker, msg: String, t: Throwable) = logger.warn(marker, msg, t)
    
    override fun isErrorEnabled(): Boolean = logger.isErrorEnabled
    override fun isErrorEnabled(marker: Marker): Boolean = logger.isErrorEnabled(marker)
    override fun error(msg: String) = logger.error(msg)
    override fun error(format: String, arg: Any) = logger.error(format, arg)
    override fun error(format: String, arg1: Any, arg2: Any) = logger.error(format, arg1, arg2)
    override fun error(format: String, vararg arguments: Any) = logger.error(format, *arguments)
    override fun error(msg: String, t: Throwable) = logger.error(msg, t)
    override fun error(marker: Marker, msg: String) = logger.error(marker, msg)
    override fun error(marker: Marker, format: String, arg: Any) = logger.error(marker, format, arg)
    override fun error(marker: Marker, format: String, arg1: Any, arg2: Any) = logger.error(marker, format, arg1, arg2)
    override fun error(marker: Marker, format: String, vararg argArray: Any) = logger.error(marker, format, *argArray)
    override fun error(marker: Marker, msg: String, t: Throwable) = logger.error(marker, msg, t)

    /**
     * 记录带有上下文信息的日志
     * @param level 日志级别
     * @param message 日志消息
     * @param context 上下文信息
     * @param throwable 异常信息（可选）
     */
    fun log(level: Level, message: String, context: Map<String, Any> = emptyMap(), throwable: Throwable? = null) {
        val contextInfo = if (context.isNotEmpty()) {
            context.entries.joinToString(", ", " [", "]") { "${it.key}=${it.value}" }
        } else {
            ""
        }
        
        val logMessage = "$message$contextInfo"
        
        when (level) {
            Level.ERROR -> if (throwable != null) error(logMessage, throwable) else error(logMessage)
            Level.WARN -> if (throwable != null) warn(logMessage, throwable) else warn(logMessage)
            Level.INFO -> if (throwable != null) info(logMessage, throwable) else info(logMessage)
            Level.DEBUG -> if (throwable != null) debug(logMessage, throwable) else debug(logMessage)
            Level.TRACE -> if (throwable != null) trace(logMessage, throwable) else trace(logMessage)
        }
    }
    
    /**
     * 记录系统操作日志
     * @param action 操作名称
     * @param result 操作结果
     * @param details 详细信息
     */
    fun logSystemAction(action: String, result: Boolean, details: String = "") {
        val context = mutableMapOf(
            "type" to "SYSTEM_ACTION",
            "action" to action,
            "result" to result,
            "timestamp" to LocalDateTime.now()
        )
        if (details.isNotEmpty()) {
            context["details"] = details
        }
        
        val level = if (result) Level.INFO else Level.WARN
        log(level, "系统操作: $action", context)
    }
    
    /**
     * 记录业务操作日志
     * @param module 业务模块
     * @param operation 操作类型
     * @param userId 用户ID
     * @param details 详细信息
     */
    fun logBusinessOperation(module: String, operation: String, userId: String? = null, details: String = "") {
        val context = mutableMapOf(
            "type" to "BUSINESS_OPERATION",
            "module" to module,
            "operation" to operation,
            "timestamp" to LocalDateTime.now()
        )
        
        if (userId != null) {
            context["userId"] = userId
        }
        
        if (details.isNotEmpty()) {
            context["details"] = details
        }
        
        log(Level.INFO, "业务操作: $module.$operation", context)
    }
    
    /**
     * 记录安全事件日志
     * @param event 安全事件类型
     * @param level 事件级别
     * @param subject 主体（用户、IP等）
     * @param details 详细信息
     */
    fun logSecurityEvent(event: String, level: Level, subject: String, details: String = "") {
        val context = mutableMapOf(
            "type" to "SECURITY_EVENT",
            "event" to event,
            "subject" to subject,
            "timestamp" to LocalDateTime.now()
        )
        
        if (details.isNotEmpty()) {
            context["details"] = details
        }
        
        log(level, "安全事件: $event", context)
    }
    
    /**
     * 获取日志类型
     * @return 日志类型
     */
    fun getLogType(): LogType {
        return type
    }
} 