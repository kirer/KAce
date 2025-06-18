package com.github.kirer.kace.event

import com.github.kirer.kace.log.LoggerFactory
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

/**
 * 事件监听器接口
 */
interface EventListener {
    /**
     * 处理事件
     * @param event 事件对象
     */
    fun onEvent(event: Event)
    
    /**
     * 获取监听器支持的事件类型
     * @return 事件类型列表
     */
    fun getSupportedEventTypes(): List<String>
    
    /**
     * 获取监听器优先级
     * @return 监听器优先级
     */
    fun getPriority(): EventPriority = EventPriority.NORMAL
}

/**
 * 事件总线
 * 负责事件的发布和订阅
 */
class EventBus(
    private val coroutineContext: CoroutineContext = Dispatchers.Default,
    private val persistEvents: Boolean = false
) {
    private val logger = LoggerFactory.getSystemLogger(EventBus::class.java)
    private val listeners = ConcurrentHashMap<String, CopyOnWriteArrayList<EventListener>>()
    private val eventHistory = CopyOnWriteArrayList<Event>()
    private val maxHistorySize = 1000
    
    /**
     * 注册事件监听器
     * @param listener 事件监听器
     */
    fun registerListener(listener: EventListener) {
        listener.getSupportedEventTypes().forEach { eventType ->
            listeners.computeIfAbsent(eventType) { CopyOnWriteArrayList() }.add(listener)
            // 按优先级排序
            listeners[eventType]?.sortByDescending { it.getPriority() }
        }
        logger.debug("Registered event listener: ${listener.javaClass.name}")
    }
    
    /**
     * 注销事件监听器
     * @param listener 事件监听器
     */
    fun unregisterListener(listener: EventListener) {
        listener.getSupportedEventTypes().forEach { eventType ->
            listeners[eventType]?.remove(listener)
        }
        logger.debug("Unregistered event listener: ${listener.javaClass.name}")
    }
    
    /**
     * 同步发布事件
     * @param event 事件对象
     * @return 事件对象（可能被监听器修改）
     */
    fun publishSync(event: Event): Event {
        logger.debug("Publishing event synchronously: $event")
        
        if (persistEvents) {
            addToHistory(event)
        }
        
        val eventListeners = listeners[event.type] ?: return event
        
        for (listener in eventListeners) {
            try {
                if (!event.cancelled) {
                    listener.onEvent(event)
                }
            } catch (e: Exception) {
                logger.error("Error processing event ${event.type} by listener ${listener.javaClass.name}", e)
            }
        }
        
        return event
    }
    
    /**
     * 异步发布事件
     * @param event 事件对象
     * @return 协程Job对象
     */
    fun publishAsync(event: Event): Job {
        logger.debug("Publishing event asynchronously: $event")
        
        if (persistEvents) {
            addToHistory(event)
        }
        
        return CoroutineScope(coroutineContext).launch {
            val eventListeners = listeners[event.type] ?: return@launch
            
            for (listener in eventListeners) {
                try {
                    if (!event.cancelled) {
                        listener.onEvent(event)
                    }
                } catch (e: Exception) {
                    logger.error("Error processing event ${event.type} by listener ${listener.javaClass.name}", e)
                }
            }
        }
    }
    
    /**
     * 获取事件历史记录
     * @return 事件历史记录
     */
    fun getEventHistory(): List<Event> {
        return eventHistory.toList()
    }
    
    /**
     * 清除事件历史记录
     */
    fun clearEventHistory() {
        eventHistory.clear()
    }
    
    /**
     * 添加事件到历史记录
     * @param event 事件对象
     */
    private fun addToHistory(event: Event) {
        eventHistory.add(event)
        
        // 如果历史记录超过最大大小，删除最旧的事件
        while (eventHistory.size > maxHistorySize) {
            eventHistory.removeAt(0)
        }
    }
} 