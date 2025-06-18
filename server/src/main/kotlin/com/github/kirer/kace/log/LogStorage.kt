package com.github.kirer.kace.log

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max

/**
 * 日志条目
 */
data class LogEntry(
    val timestamp: LocalDateTime,
    val type: LogType,
    val level: String,
    val logger: String,
    val message: String,
    val context: Map<String, Any> = emptyMap(),
    val exception: String? = null
)

/**
 * 日志查询条件
 */
data class LogQueryCriteria(
    val types: Set<LogType>? = null,
    val levels: Set<String>? = null,
    val loggers: Set<String>? = null,
    val fromTime: LocalDateTime? = null,
    val toTime: LocalDateTime? = null,
    val messageContains: String? = null,
    val limit: Int = 100,
    val offset: Int = 0
)

/**
 * 日志存储接口
 */
interface LogStorage {
    /**
     * 存储日志条目
     */
    fun store(entry: LogEntry)
    
    /**
     * 查询日志条目
     */
    fun query(criteria: LogQueryCriteria): List<LogEntry>
    
    /**
     * 清理过期日志
     */
    fun cleanup(before: LocalDateTime): Int
}

/**
 * 内存日志存储实现
 */
class InMemoryLogStorage(private val maxEntries: Int = 10000) : LogStorage {
    private val logs = ConcurrentLinkedQueue<LogEntry>()
    
    override fun store(entry: LogEntry) {
        logs.add(entry)
        
        // 如果超过最大条目数，移除最旧的条目
        while (logs.size > maxEntries) {
            logs.poll()
        }
    }
    
    override fun query(criteria: LogQueryCriteria): List<LogEntry> {
        return logs.asSequence()
            .filter { entry ->
                (criteria.types == null || criteria.types.contains(entry.type)) &&
                (criteria.levels == null || criteria.levels.contains(entry.level)) &&
                (criteria.loggers == null || criteria.loggers.contains(entry.logger)) &&
                (criteria.fromTime == null || !entry.timestamp.isBefore(criteria.fromTime)) &&
                (criteria.toTime == null || !entry.timestamp.isAfter(criteria.toTime)) &&
                (criteria.messageContains == null || entry.message.contains(criteria.messageContains, ignoreCase = true))
            }
            .drop(max(0, criteria.offset))
            .take(criteria.limit)
            .toList()
    }
    
    override fun cleanup(before: LocalDateTime): Int {
        val initialSize = logs.size
        logs.removeIf { it.timestamp.isBefore(before) }
        return initialSize - logs.size
    }
} 