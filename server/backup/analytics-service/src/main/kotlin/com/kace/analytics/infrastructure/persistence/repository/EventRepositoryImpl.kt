package com.kace.analytics.infrastructure.persistence.repository

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.repository.EventRepository
import com.kace.analytics.infrastructure.persistence.entity.EventEntity
import com.kace.analytics.infrastructure.persistence.entity.Events
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

/**
 * 事件仓库实现类
 */
class EventRepositoryImpl : EventRepository {

    override fun create(event: Event): Event = transaction {
        val entity = EventEntity.new {
            type = event.type
            name = event.name
            userId = event.userId?.toString()
            sessionId = event.sessionId
            properties = event.properties
            timestamp = event.timestamp
            appVersion = event.appVersion
            deviceInfo = event.deviceInfo
            source = event.source
        }
        entity.toEvent()
    }

    override fun findById(id: UUID): Event? = transaction {
        EventEntity.findById(id)?.toEvent()
    }

    override fun findByType(type: String, limit: Int, offset: Int): List<Event> = transaction {
        EventEntity.find { Events.type eq type }
            .orderBy(Events.timestamp to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toEvent() }
    }

    override fun findByTimeRange(startTime: Instant, endTime: Instant, limit: Int, offset: Int): List<Event> = transaction {
        EventEntity.find { 
            (Events.timestamp greaterEq startTime) and (Events.timestamp lessEq endTime) 
        }
        .orderBy(Events.timestamp to SortOrder.DESC)
        .limit(limit, offset.toLong())
        .map { it.toEvent() }
    }

    override fun findByUserId(userId: UUID, limit: Int, offset: Int): List<Event> = transaction {
        EventEntity.find { Events.userId eq userId.toString() }
            .orderBy(Events.timestamp to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toEvent() }
    }

    override fun findBySessionId(sessionId: String, limit: Int, offset: Int): List<Event> = transaction {
        EventEntity.find { Events.sessionId eq sessionId }
            .orderBy(Events.timestamp to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toEvent() }
    }

    override fun countByType(type: String): Long = transaction {
        EventEntity.find { Events.type eq type }.count()
    }

    override fun countByTimeRange(startTime: Instant, endTime: Instant): Long = transaction {
        EventEntity.find { 
            (Events.timestamp greaterEq startTime) and (Events.timestamp lessEq endTime) 
        }.count()
    }

    override fun deleteById(id: UUID): Boolean = transaction {
        EventEntity.findById(id)?.let {
            it.delete()
            true
        } ?: false
    }

    override fun deleteByTimeRange(startTime: Instant, endTime: Instant): Int = transaction {
        val entities = EventEntity.find { 
            (Events.timestamp greaterEq startTime) and (Events.timestamp lessEq endTime) 
        }
        val count = entities.count().toInt()
        entities.forEach { it.delete() }
        count
    }
} 