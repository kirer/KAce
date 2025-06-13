package com.kace.analytics.domain.repository

import com.kace.analytics.domain.model.Event
import kotlinx.datetime.Instant

/**
 * 事件仓库接口
 */
interface EventRepository {
    /**
     * 保存单个事件
     */
    suspend fun save(event: Event): Event
    
    /**
     * 批量保存事件
     */
    suspend fun saveAll(events: List<Event>): List<Event>
    
    /**
     * 根据ID查找事件
     */
    suspend fun findById(id: String): Event?
    
    /**
     * 根据条件查询事件
     */
    suspend fun findAll(
        type: String? = null,
        name: String? = null,
        userId: String? = null,
        sessionId: String? = null,
        startTime: Instant? = null,
        endTime: Instant? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<Event>
    
    /**
     * 统计事件数量
     */
    suspend fun count(
        type: String? = null,
        name: String? = null,
        userId: String? = null,
        sessionId: String? = null,
        startTime: Instant? = null,
        endTime: Instant? = null
    ): Long
    
    /**
     * 删除事件
     */
    suspend fun delete(id: String): Boolean
    
    /**
     * 删除指定时间之前的事件
     */
    suspend fun deleteOlderThan(timestamp: Instant): Int
} 