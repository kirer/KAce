package com.kace.analytics.infrastructure.persistence.repository

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.repository.EventRepository
import com.kace.analytics.infrastructure.persistence.entity.EventEntity
import com.kace.analytics.infrastructure.persistence.entity.EventTable
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory

/**
 * 事件仓库实现
 */
class EventRepositoryImpl : EventRepository {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 保存单个事件
     */
    override suspend fun save(event: Event): Event = newSuspendedTransaction {
        try {
            val entity = EventEntity.new(event.id) {
                type = event.type
                name = event.name
                userId = event.userId
                sessionId = event.sessionId
                properties = event.properties.toString() // 简化处理，实际应使用JSON序列化
                timestamp = event.timestamp
                appVersion = event.appVersion
                deviceInfo = event.deviceInfo.toString() // 简化处理，实际应使用JSON序列化
                source = event.source
            }
            entity.toModel()
        } catch (e: Exception) {
            logger.error("Failed to save event: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * 批量保存事件
     */
    override suspend fun saveAll(events: List<Event>): List<Event> = newSuspendedTransaction {
        try {
            events.map { save(it) }
        } catch (e: Exception) {
            logger.error("Failed to save events: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * 根据ID查找事件
     */
    override suspend fun findById(id: String): Event? = newSuspendedTransaction {
        try {
            EventEntity.findById(id)?.toModel()
        } catch (e: Exception) {
            logger.error("Failed to find event by id: ${e.message}", e)
            null
        }
    }
    
    /**
     * 根据条件查询事件
     */
    override suspend fun findAll(
        type: String?,
        name: String?,
        userId: String?,
        sessionId: String?,
        startTime: Instant?,
        endTime: Instant?,
        limit: Int,
        offset: Int
    ): List<Event> = newSuspendedTransaction {
        try {
            val query = EventTable.selectAll()
            
            type?.let { query.andWhere { EventTable.type eq it } }
            name?.let { query.andWhere { EventTable.name eq it } }
            userId?.let { query.andWhere { EventTable.userId eq it } }
            sessionId?.let { query.andWhere { EventTable.sessionId eq it } }
            startTime?.let { query.andWhere { EventTable.timestamp greaterEq it } }
            endTime?.let { query.andWhere { EventTable.timestamp lessEq it } }
            
            query.orderBy(EventTable.timestamp to SortOrder.DESC)
                .limit(limit, offset.toLong())
                .map { EventEntity.wrapRow(it).toModel() }
        } catch (e: Exception) {
            logger.error("Failed to find events: ${e.message}", e)
            emptyList()
        }
    }
    
    /**
     * 统计事件数量
     */
    override suspend fun count(
        type: String?,
        name: String?,
        userId: String?,
        sessionId: String?,
        startTime: Instant?,
        endTime: Instant?
    ): Long = newSuspendedTransaction {
        try {
            val query = EventTable.selectAll()
            
            type?.let { query.andWhere { EventTable.type eq it } }
            name?.let { query.andWhere { EventTable.name eq it } }
            userId?.let { query.andWhere { EventTable.userId eq it } }
            sessionId?.let { query.andWhere { EventTable.sessionId eq it } }
            startTime?.let { query.andWhere { EventTable.timestamp greaterEq it } }
            endTime?.let { query.andWhere { EventTable.timestamp lessEq it } }
            
            query.count()
        } catch (e: Exception) {
            logger.error("Failed to count events: ${e.message}", e)
            0L
        }
    }
    
    /**
     * 删除事件
     */
    override suspend fun delete(id: String): Boolean = newSuspendedTransaction {
        try {
            val entity = EventEntity.findById(id) ?: return@newSuspendedTransaction false
            entity.delete()
            true
        } catch (e: Exception) {
            logger.error("Failed to delete event: ${e.message}", e)
            false
        }
    }
    
    /**
     * 删除指定时间之前的事件
     */
    override suspend fun deleteOlderThan(timestamp: Instant): Int = newSuspendedTransaction {
        try {
            EventTable.deleteWhere { EventTable.timestamp less timestamp }
        } catch (e: Exception) {
            logger.error("Failed to delete old events: ${e.message}", e)
            0
        }
    }
} 