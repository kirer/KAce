package com.github.kirer.kace.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.ThrowableProxy
import ch.qos.logback.core.AppenderBase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

/**
 * KAce日志Appender
 * 捕获Logback日志并存储到LogStorage
 */
class KAceLogAppender : AppenderBase<ILoggingEvent>(), KoinComponent {
    
    private val logStorage: LogStorage by inject()
    private val loggerTypeCache = ConcurrentHashMap<String, LogType>()
    
    init {
        name = "KAceLogAppender"
    }
    
    override fun append(event: ILoggingEvent) {
        try {
            val loggerName = event.loggerName
            val type = determineLogType(loggerName)
            
            val timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(event.timeStamp),
                ZoneId.systemDefault()
            )
            
            val mdcContext = event.mdcPropertyMap?.toMap() ?: emptyMap()
            val context = mdcContext.mapValues { it.value as Any }
            
            val exception = if (event.throwableProxy != null) {
                val throwableProxy = event.throwableProxy as ThrowableProxy
                val throwable = throwableProxy.throwable
                throwable.stackTraceToString()
            } else {
                null
            }
            
            val logEntry = LogEntry(
                timestamp = timestamp,
                type = type,
                level = event.level.levelStr,
                logger = loggerName,
                message = event.formattedMessage,
                context = context,
                exception = exception
            )
            
            logStorage.store(logEntry)
        } catch (e: Exception) {
            System.err.println("Error in KAceLogAppender: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun determineLogType(loggerName: String): LogType {
        return loggerTypeCache.computeIfAbsent(loggerName) {
            when {
                loggerName.startsWith("system.") -> LogType.SYSTEM
                loggerName.startsWith("business.") -> LogType.BUSINESS
                loggerName.startsWith("security.") -> LogType.SECURITY
                else -> LogType.SYSTEM // 默认为系统日志
            }
        }
    }
} 