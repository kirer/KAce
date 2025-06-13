package com.kace.analytics.domain.service

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.repository.EventRepository
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

/**
 * 事件服务
 */
class EventService(private val eventRepository: EventRepository) {
    
    /**
     * 跟踪单个事件
     */
    suspend fun trackEvent(event: Event): Event {
        return eventRepository.save(event)
    }
    
    /**
     * 批量跟踪事件
     */
    suspend fun trackEvents(events: List<Event>): List<Event> {
        return eventRepository.saveAll(events)
    }
    
    /**
     * 获取事件
     */
    suspend fun getEvent(id: String): Event? {
        return eventRepository.findById(id)
    }
    
    /**
     * 查询事件
     */
    suspend fun getEvents(
        type: String? = null,
        name: String? = null,
        userId: String? = null,
        sessionId: String? = null,
        startTime: Instant? = null,
        endTime: Instant? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<Event> {
        return eventRepository.findAll(
            type = type,
            name = name,
            userId = userId,
            sessionId = sessionId,
            startTime = startTime,
            endTime = endTime,
            limit = limit,
            offset = offset
        )
    }
    
    /**
     * 统计事件
     */
    suspend fun countEvents(
        type: String? = null,
        name: String? = null,
        userId: String? = null,
        sessionId: String? = null,
        startTime: Instant? = null,
        endTime: Instant? = null
    ): Long {
        return eventRepository.count(
            type = type,
            name = name,
            userId = userId,
            sessionId = sessionId,
            startTime = startTime,
            endTime = endTime
        )
    }
    
    /**
     * 删除事件
     */
    suspend fun deleteEvent(id: String): Boolean {
        return eventRepository.delete(id)
    }
    
    /**
     * 清理旧数据
     */
    suspend fun cleanupOldEvents(retentionDays: Int): Int {
        val cutoffTime = Clock.System.now() - retentionDays.days
        return eventRepository.deleteOlderThan(cutoffTime)
    }
}
 