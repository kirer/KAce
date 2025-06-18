package com.github.kirer.kace.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 日志类型枚举
 */
enum class LogType {
    /**
     * 系统日志 - 记录系统级别的操作和状态
     */
    SYSTEM,
    
    /**
     * 业务日志 - 记录业务操作和流程
     */
    BUSINESS,
    
    /**
     * 安全日志 - 记录安全相关的事件
     */
    SECURITY
}

/**
 * 日志工厂类
 * 用于创建和获取不同类型的日志记录器
 */
object LoggerFactory {
    private val loggers = ConcurrentHashMap<String, KAceLogger>()
    
    /**
     * 获取系统日志记录器
     * @param clazz 类对象
     * @return 系统日志记录器
     */
    fun getSystemLogger(clazz: Class<*>): KAceLogger {
        return getLogger(clazz, LogType.SYSTEM)
    }
    
    /**
     * 获取业务日志记录器
     * @param clazz 类对象
     * @return 业务日志记录器
     */
    fun getBusinessLogger(clazz: Class<*>): KAceLogger {
        return getLogger(clazz, LogType.BUSINESS)
    }
    
    /**
     * 获取安全日志记录器
     * @param clazz 类对象
     * @return 安全日志记录器
     */
    fun getSecurityLogger(clazz: Class<*>): KAceLogger {
        return getLogger(clazz, LogType.SECURITY)
    }
    
    /**
     * 获取指定类型的日志记录器
     * @param clazz 类对象
     * @param type 日志类型
     * @return 日志记录器
     */
    fun getLogger(clazz: Class<*>, type: LogType): KAceLogger {
        val key = "${clazz.name}:${type.name}"
        return loggers.computeIfAbsent(key) {
            val logger = org.slf4j.LoggerFactory.getLogger("${type.name.lowercase()}.${clazz.name}")
            KAceLogger(logger, type)
        }
    }
    
    /**
     * 获取指定类型的日志记录器
     * @param name 日志记录器名称
     * @param type 日志类型
     * @return 日志记录器
     */
    fun getLogger(name: String, type: LogType): KAceLogger {
        val key = "$name:${type.name}"
        return loggers.computeIfAbsent(key) {
            val logger = org.slf4j.LoggerFactory.getLogger("${type.name.lowercase()}.$name")
            KAceLogger(logger, type)
        }
    }
    
    /**
     * 获取默认的系统日志记录器
     * @param name 日志记录器名称
     * @return 系统日志记录器
     */
    fun getLogger(name: String): KAceLogger {
        return getLogger(name, LogType.SYSTEM)
    }
    
    /**
     * 获取默认的系统日志记录器
     * @param clazz 类对象
     * @return 系统日志记录器
     */
    fun getLogger(clazz: Class<*>): KAceLogger {
        return getLogger(clazz, LogType.SYSTEM)
    }
} 