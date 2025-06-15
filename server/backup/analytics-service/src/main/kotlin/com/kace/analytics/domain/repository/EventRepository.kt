package com.kace.analytics.domain.repository

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.model.EventFilter
import java.time.Instant
import java.util.UUID

/**
 * 事件仓库接口
 */
interface EventRepository {
    
    /**
     * 保存事件
     */
    suspend fun save(event: Event): Event
    
    /**
     * 批量保存事件
     */
    suspend fun saveAll(events: List<Event>): List<Event>
    
    /**
     * 根据ID查找事件
     */
    suspend fun findById(id: UUID): Event?
    
    /**
     * 根据过滤条件查找事件
     */
    suspend fun findByFilter(filter: EventFilter, page: Int, size: Int): List<Event>
    
    /**
     * 根据用户ID查找事件
     */
    suspend fun findByUserId(userId: UUID, page: Int, size: Int): List<Event>
    
    /**
     * 根据会话ID查找事件
     */
    suspend fun findBySessionId(sessionId: String, page: Int, size: Int): List<Event>
    
    /**
     * 根据时间范围查找事件
     */
    suspend fun findByTimeRange(startTime: Instant, endTime: Instant, page: Int, size: Int): List<Event>
    
    /**
     * 根据事件类型查找事件
     */
    suspend fun findByType(type: String, page: Int, size: Int): List<Event>
    
    /**
     * 根据事件名称查找事件
     */
    suspend fun findByName(name: String, page: Int, size: Int): List<Event>
    
    /**
     * 统计事件数量
     */
    suspend fun count(filter: EventFilter? = null): Long
    
    /**
     * 删除指定时间之前的事件
     */
    suspend fun deleteBeforeTime(time: Instant): Int
    
    /**
     * 删除事件
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 聚合事件数据
     */
    suspend fun aggregate(
        filter: EventFilter,
        groupByFields: List<String>,
        aggregateField: String? = null,
        aggregationType: String = "COUNT"
    ): List<Map<String, Any>>
} 