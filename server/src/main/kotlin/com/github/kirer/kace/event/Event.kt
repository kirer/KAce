package com.github.kirer.kace.event

import java.time.LocalDateTime
import java.util.*

/**
 * 事件接口
 * 所有事件必须实现此接口
 */
interface Event {
    /**
     * 事件ID
     */
    val id: String
    
    /**
     * 事件类型
     */
    val type: String
    
    /**
     * 事件发生时间
     */
    val timestamp: LocalDateTime
    
    /**
     * 事件源
     */
    val source: String
    
    /**
     * 事件数据
     */
    val data: Map<String, Any>
    
    /**
     * 是否可取消
     */
    val cancelable: Boolean
    
    /**
     * 是否已取消
     */
    var cancelled: Boolean
}

/**
 * 事件优先级
 */
enum class EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}

/**
 * 基础事件实现
 */
open class BaseEvent(
    override val type: String,
    override val source: String,
    override val data: Map<String, Any> = emptyMap(),
    override val cancelable: Boolean = false
) : Event {
    override val id: String = UUID.randomUUID().toString()
    override val timestamp: LocalDateTime = LocalDateTime.now()
    override var cancelled: Boolean = false
    
    /**
     * 取消事件
     * @throws IllegalStateException 如果事件不可取消
     */
    fun cancel() {
        if (!cancelable) {
            throw IllegalStateException("Event of type $type is not cancelable")
        }
        cancelled = true
    }
    
    override fun toString(): String {
        return "Event(id=$id, type=$type, source=$source, timestamp=$timestamp, cancelled=$cancelled)"
    }
}

/**
 * 系统事件
 * 用于系统级别的事件通知
 */
class SystemEvent(
    type: String,
    source: String,
    data: Map<String, Any> = emptyMap(),
    cancelable: Boolean = false
) : BaseEvent(type, source, data, cancelable)

/**
 * 业务事件
 * 用于业务逻辑的事件通知
 */
class BusinessEvent(
    type: String,
    source: String,
    data: Map<String, Any> = emptyMap(),
    cancelable: Boolean = true
) : BaseEvent(type, source, data, cancelable)

/**
 * 插件事件
 * 用于插件相关的事件通知
 */
class PluginEvent(
    type: String,
    source: String,
    data: Map<String, Any> = emptyMap(),
    cancelable: Boolean = false
) : BaseEvent(type, source, data, cancelable) 