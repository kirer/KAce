package com.kace.analytics.infrastructure.collector

import com.kace.analytics.domain.model.Event
import com.kace.analytics.domain.service.EventService
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory

/**
 * 页面浏览收集器
 */
class PageViewCollector(eventService: EventService) : EventCollector(eventService) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 处理页面浏览事件
     */
    override suspend fun processEvent(event: Event): Event {
        // 确保事件类型是页面浏览
        if (event.type != "page_view") {
            logger.warn("Non-page view event sent to PageViewCollector: ${event.type}")
            return event
        }
        
        // 添加额外的页面浏览属性
        val enhancedProperties = event.properties.toMutableMap()
        
        // 计算页面路径的深度
        val path = enhancedProperties["path"] ?: "/"
        val pathDepth = path.split("/").filter { it.isNotEmpty() }.size
        enhancedProperties["path_depth"] = pathDepth.toString()
        
        // 检查是否是入口页
        val referrer = enhancedProperties["referrer"] ?: ""
        val isEntryPage = referrer.isEmpty() || !referrer.contains(enhancedProperties["host"] ?: "")
        enhancedProperties["is_entry_page"] = isEntryPage.toString()
        
        return event.copy(
            properties = enhancedProperties,
            timestamp = event.timestamp ?: Clock.System.now()
        )
    }
    
    /**
     * 跟踪页面浏览
     */
    suspend fun trackPageView(
        userId: String?,
        sessionId: String,
        path: String,
        title: String,
        referrer: String = "",
        host: String,
        deviceInfo: Map<String, String> = emptyMap(),
        additionalProperties: Map<String, String> = emptyMap()
    ): Event {
        val properties = mutableMapOf(
            "path" to path,
            "title" to title,
            "referrer" to referrer,
            "host" to host
        )
        
        properties.putAll(additionalProperties)
        
        val event = Event(
            type = "page_view",
            name = "Page View: $title",
            userId = userId,
            sessionId = sessionId,
            properties = properties,
            timestamp = Clock.System.now(),
            source = "web" // 可以根据实际来源修改
        )
        
        return collect(event)
    }
} 