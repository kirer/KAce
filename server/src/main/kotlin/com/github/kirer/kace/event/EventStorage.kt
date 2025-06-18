package com.github.kirer.kace.event

import com.github.kirer.kace.log.LoggerFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 事件存储接口
 */
interface EventStorage {
    /**
     * 保存事件
     * @param event 事件对象
     */
    fun saveEvent(event: Event)
    
    /**
     * 查询事件
     * @param criteria 查询条件
     * @return 事件列表
     */
    fun queryEvents(criteria: EventQueryCriteria): List<Event>
    
    /**
     * 清理过期事件
     * @param before 截止时间
     * @return 清理的事件数量
     */
    fun cleanupEvents(before: LocalDateTime): Int
}

/**
 * 事件查询条件
 */
data class EventQueryCriteria(
    val types: Set<String>? = null,
    val sources: Set<String>? = null,
    val fromTime: LocalDateTime? = null,
    val toTime: LocalDateTime? = null,
    val limit: Int = 100,
    val offset: Int = 0
)

/**
 * 序列化的事件数据
 */
@Serializable
data class SerializedEvent(
    val id: String,
    val type: String,
    val timestamp: String,
    val source: String,
    val data: Map<String, String>,
    val cancelable: Boolean,
    val cancelled: Boolean
)

/**
 * 内存事件存储实现
 */
class InMemoryEventStorage(private val maxEvents: Int = 10000) : EventStorage {
    private val logger = LoggerFactory.getSystemLogger(InMemoryEventStorage::class.java)
    private val events = CopyOnWriteArrayList<Event>()
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    
    override fun saveEvent(event: Event) {
        events.add(event)
        
        // 如果超过最大事件数，删除最旧的事件
        while (events.size > maxEvents) {
            events.removeAt(0)
        }
    }
    
    override fun queryEvents(criteria: EventQueryCriteria): List<Event> {
        return events.asSequence()
            .filter { event ->
                (criteria.types == null || criteria.types.contains(event.type)) &&
                (criteria.sources == null || criteria.sources.contains(event.source)) &&
                (criteria.fromTime == null || !event.timestamp.isBefore(criteria.fromTime)) &&
                (criteria.toTime == null || !event.timestamp.isAfter(criteria.toTime))
            }
            .drop(criteria.offset)
            .take(criteria.limit)
            .toList()
    }
    
    override fun cleanupEvents(before: LocalDateTime): Int {
        val initialSize = events.size
        events.removeIf { it.timestamp.isBefore(before) }
        return initialSize - events.size
    }
    
    /**
     * 将事件序列化为JSON
     * @param event 事件对象
     * @return 序列化后的JSON字符串
     */
    private fun serializeEvent(event: Event): String {
        val serializedEvent = SerializedEvent(
            id = event.id,
            type = event.type,
            timestamp = event.timestamp.format(dateTimeFormatter),
            source = event.source,
            data = event.data.mapValues { it.value.toString() },
            cancelable = event.cancelable,
            cancelled = event.cancelled
        )
        
        return Json.encodeToString(SerializedEvent.serializer(), serializedEvent)
    }
} 