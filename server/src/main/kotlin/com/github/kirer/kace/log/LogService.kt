package com.github.kirer.kace.log

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * 日志服务
 * 提供日志查询和管理功能
 */
class LogService : KoinComponent {
    
    private val logStorage: LogStorage by inject()
    
    /**
     * 查询日志
     * @param criteria 查询条件
     * @return 日志条目列表
     */
    fun queryLogs(criteria: LogQueryCriteria): List<LogEntry> {
        return logStorage.query(criteria)
    }
    
    /**
     * 获取系统日志
     * @param fromTime 开始时间
     * @param toTime 结束时间
     * @param limit 最大条目数
     * @param offset 偏移量
     * @return 系统日志条目列表
     */
    fun getSystemLogs(
        fromTime: LocalDateTime? = null,
        toTime: LocalDateTime? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<LogEntry> {
        return queryLogs(
            LogQueryCriteria(
                types = setOf(LogType.SYSTEM),
                fromTime = fromTime,
                toTime = toTime,
                limit = limit,
                offset = offset
            )
        )
    }
    
    /**
     * 获取业务日志
     * @param fromTime 开始时间
     * @param toTime 结束时间
     * @param limit 最大条目数
     * @param offset 偏移量
     * @return 业务日志条目列表
     */
    fun getBusinessLogs(
        fromTime: LocalDateTime? = null,
        toTime: LocalDateTime? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<LogEntry> {
        return queryLogs(
            LogQueryCriteria(
                types = setOf(LogType.BUSINESS),
                fromTime = fromTime,
                toTime = toTime,
                limit = limit,
                offset = offset
            )
        )
    }
    
    /**
     * 获取安全日志
     * @param fromTime 开始时间
     * @param toTime 结束时间
     * @param limit 最大条目数
     * @param offset 偏移量
     * @return 安全日志条目列表
     */
    fun getSecurityLogs(
        fromTime: LocalDateTime? = null,
        toTime: LocalDateTime? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<LogEntry> {
        return queryLogs(
            LogQueryCriteria(
                types = setOf(LogType.SECURITY),
                fromTime = fromTime,
                toTime = toTime,
                limit = limit,
                offset = offset
            )
        )
    }
    
    /**
     * 清理过期日志
     * @param days 保留天数
     * @return 清理的日志条目数
     */
    fun cleanupLogs(days: Int): Int {
        val cutoffDate = LocalDateTime.now().minus(days.toLong(), ChronoUnit.DAYS)
        return logStorage.cleanup(cutoffDate)
    }
} 