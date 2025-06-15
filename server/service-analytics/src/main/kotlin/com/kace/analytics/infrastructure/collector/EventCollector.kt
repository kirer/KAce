package com.kace.analytics.infrastructure.collector

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.service.EventService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

/**
 * 事件收集器基类
 */
abstract class EventCollector(protected val eventService: EventService) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    /**
     * 异步收集事件
     */
    fun collectAsync(event: Event) {
        scope.launch {
            try {
                collect(event)
            } catch (e: Exception) {
                logger.error("Failed to collect event: ${e.message}", e)
            }
        }
    }
    
    /**
     * 异步批量收集事件
     */
    fun collectBatchAsync(events: List<Event>) {
        scope.launch {
            try {
                collectBatch(events)
            } catch (e: Exception) {
                logger.error("Failed to collect events: ${e.message}", e)
            }
        }
    }
    
    /**
     * 收集单个事件
     */
    suspend fun collect(event: Event): Event {
        val processedEvent = processEvent(event)
        return eventService.trackEvent(processedEvent)
    }
    
    /**
     * 批量收集事件
     */
    suspend fun collectBatch(events: List<Event>): List<Event> {
        val processedEvents = events.map { processEvent(it) }
        return eventService.trackEvents(processedEvents)
    }
    
    /**
     * 处理事件，子类可以重写此方法进行自定义处理
     */
    protected open suspend fun processEvent(event: Event): Event {
        return event
    }
} 