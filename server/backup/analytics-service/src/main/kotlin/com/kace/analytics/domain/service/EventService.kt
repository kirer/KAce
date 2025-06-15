package com.kace.analytics.domain.service

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.model.EventFilter
import java.time.Instant
import java.util.UUID

/**
 * 事件服务接口
 */
interface EventService {
    
    /**
     * 记录事件
     */
    suspend fun recordEvent(event: Event): Event
    
    /**
     * 批量记录事件
     */
    suspend fun recordEvents(events: List<Event>): List<Event>
    
    /**
     * 获取事件
     */
    suspend fun getEvent(id: UUID): Event?
    
    /**
     * 查询事件
     */
    suspend fun queryEvents(filter: EventFilter, page: Int, size: Int): List<Event>
    
    /**
     * 获取用户事件
     */
    suspend fun getUserEvents(userId: UUID, page: Int, size: Int): List<Event>
    
    /**
     * 获取会话事件
     */
    suspend fun getSessionEvents(sessionId: String, page: Int, size: Int): List<Event>
    
    /**
     * 获取事件数量
     */
    suspend fun countEvents(filter: EventFilter? = null): Long
    
    /**
     * 聚合事件数据
     */
    suspend fun aggregateEvents(
        filter: EventFilter,
        groupByFields: List<String>,
        aggregateField: String? = null,
        aggregationType: String = "COUNT"
    ): List<Map<String, Any>>
    
    /**
     * 获取热门事件
     */
    suspend fun getTopEvents(
        eventType: String,
        startTime: Instant,
        endTime: Instant,
        limit: Int = 10
    ): List<Map<String, Any>>
    
    /**
     * 获取事件趋势
     */
    suspend fun getEventTrend(
        eventType: String,
        eventName: String? = null,
        startTime: Instant,
        endTime: Instant,
        interval: String = "day"
    ): List<Map<String, Any>>
    
    /**
     * 清理旧事件数据
     */
    suspend fun cleanupOldEvents(retentionDays: Int): Int
}
 