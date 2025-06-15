package com.kace.analytics.infrastructure.service

import com.kace.analytics.domain.model.Event
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
    
    override fun trackEvent(event: Event): Event {
        logger.info("Tracking event: ${event.type} - ${event.name}")
        return eventRepository.create(event)
    }
    
    override fun trackEvents(events: List<Event>): List<Event> {
        logger.info("Tracking ${events.size} events")
        return events.map { eventRepository.create(it) }
    }
    
    override fun getEvent(id: UUID): Event? {
        logger.debug("Getting event with id: $id")
        return eventRepository.findById(id)
    }
    
    override fun getEventsByType(type: String, limit: Int, offset: Int): List<Event> {
        logger.debug("Getting events by type: $type, limit: $limit, offset: $offset")
        return eventRepository.findByType(type, limit, offset)
    }
    
    override fun getEventsByTimeRange(startTime: Instant, endTime: Instant, limit: Int, offset: Int): List<Event> {
        logger.debug("Getting events by time range: $startTime - $endTime, limit: $limit, offset: $offset")
        return eventRepository.findByTimeRange(startTime, endTime, limit, offset)
    }
    
    override fun getEventsByUserId(userId: UUID, limit: Int, offset: Int): List<Event> {
        logger.debug("Getting events by user id: $userId, limit: $limit, offset: $offset")
        return eventRepository.findByUserId(userId, limit, offset)
    }
    
    override fun getEventsBySessionId(sessionId: String, limit: Int, offset: Int): List<Event> {
        logger.debug("Getting events by session id: $sessionId, limit: $limit, offset: $offset")
        return eventRepository.findBySessionId(sessionId, limit, offset)
    }
    
    override fun countEventsByType(type: String): Long {
        logger.debug("Counting events by type: $type")
        return eventRepository.countByType(type)
    }
    
    override fun countEventsByTimeRange(startTime: Instant, endTime: Instant): Long {
        logger.debug("Counting events by time range: $startTime - $endTime")
        return eventRepository.countByTimeRange(startTime, endTime)
    }
    
    override fun deleteEvent(id: UUID): Boolean {
        logger.info("Deleting event with id: $id")
        return eventRepository.deleteById(id)
    }
    
    override fun deleteEventsByTimeRange(startTime: Instant, endTime: Instant): Int {
        logger.info("Deleting events by time range: $startTime - $endTime")
        return eventRepository.deleteByTimeRange(startTime, endTime)
    }
} 