package com.kace.analytics.infrastructure.service

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.model.EventFilter
import com.kace.analytics.domain.repository.EventRepository
import com.kace.analytics.domain.service.EventService
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 事件服务实现类
 */
class EventServiceImpl(private val eventRepository: EventRepository) : EventService {
    
    private val logger = LoggerFactory.getLogger(EventServiceImpl::class.java)
    
    override suspend fun recordEvent(event: Event): Event {
        logger.info("Recording event: ${event.type} - ${event.name}")
        return eventRepository.save(event)
    }
    
    override suspend fun recordEvents(events: List<Event>): List<Event> {
        logger.info("Recording ${events.size} events")
        return eventRepository.saveAll(events)
    }
    
    override suspend fun getEvent(id: UUID): Event? {
        logger.debug("Getting event with id: $id")
        return eventRepository.findById(id)
    }
    
    override suspend fun queryEvents(filter: EventFilter, page: Int, size: Int): List<Event> {
        logger.debug("Querying events with filter: $filter, page: $page, size: $size")
        return eventRepository.findByFilter(filter, page, size)
    }
    
    override suspend fun getUserEvents(userId: UUID, page: Int, size: Int): List<Event> {
        logger.debug("Getting events for user id: $userId, page: $page, size: $size")
        val filter = EventFilter(userId = userId)
        return eventRepository.findByFilter(filter, page, size)
    }
    
    override suspend fun getSessionEvents(sessionId: String, page: Int, size: Int): List<Event> {
        logger.debug("Getting events for session id: $sessionId, page: $page, size: $size")
        val filter = EventFilter(sessionId = sessionId)
        return eventRepository.findByFilter(filter, page, size)
    }
    
    override suspend fun countEvents(filter: EventFilter?): Long {
        logger.debug("Counting events with filter: $filter")
        return eventRepository.count(filter)
    }
    
    override suspend fun aggregateEvents(
        filter: EventFilter,
        groupByFields: List<String>,
        aggregateField: String?,
        aggregationType: String
    ): List<Map<String, Any>> {
        logger.debug("Aggregating events with filter: $filter, groupByFields: $groupByFields, aggregateField: $aggregateField, aggregationType: $aggregationType")
        return eventRepository.aggregate(filter, groupByFields, aggregateField, aggregationType)
    }
    
    override suspend fun getTopEvents(
        eventType: String,
        startTime: Instant,
        endTime: Instant,
        limit: Int
    ): List<Map<String, Any>> {
        logger.debug("Getting top events for type: $eventType, timeRange: $startTime - $endTime, limit: $limit")
        val filter = EventFilter(
            types = listOf(eventType),
            startTime = startTime,
            endTime = endTime
        )
        return eventRepository.getTopEvents(filter, limit)
    }
    
    override suspend fun getEventTrend(
        eventType: String,
        eventName: String?,
        startTime: Instant,
        endTime: Instant,
        interval: String
    ): List<Map<String, Any>> {
        logger.debug("Getting event trend for type: $eventType, name: $eventName, timeRange: $startTime - $endTime, interval: $interval")
        val filter = EventFilter(
            types = listOf(eventType),
            names = eventName?.let { listOf(it) },
            startTime = startTime,
            endTime = endTime
        )
        return eventRepository.getTrend(filter, interval)
    }
    
    override suspend fun cleanupOldEvents(retentionDays: Int): Int {
        val cutoffDate = Instant.now().minusSeconds(retentionDays * 24L * 60L * 60L)
        logger.info("Cleaning up events older than: $cutoffDate (retention: $retentionDays days)")
        return eventRepository.deleteOlderThan(cutoffDate)
    }
} 