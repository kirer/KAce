package com.github.kirer.kace.plugin.security

import com.github.kirer.kace.exception.PluginException
import com.github.kirer.kace.log.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * 资源限制配置
 */
data class ResourceLimits(
    val maxMemoryMb: Int = 256,
    val maxCpuPercent: Int = 20,
    val maxThreads: Int = 10,
    val maxFileHandles: Int = 100,
    val maxNetworkBandwidthKbps: Int = 1024,
    val maxDatabaseConnections: Int = 5,
    val maxDatabaseQueriesPerMinute: Int = 1000,
    val maxApiCallsPerMinute: Int = 100
)

/**
 * 资源使用统计
 */
data class ResourceUsage(
    val memoryUsageMb: Int = 0,
    val cpuPercent: Int = 0,
    val threadCount: Int = 0,
    val openFileHandles: Int = 0,
    val networkBandwidthKbps: Int = 0,
    val databaseConnections: Int = 0,
    val databaseQueriesPerMinute: Int = 0,
    val apiCallsPerMinute: Int = 0
)

/**
 * 插件资源限制器
 * 负责监控和限制插件的资源使用
 */
class PluginResourceLimiter {
    private val logger = LoggerFactory.getSystemLogger(PluginResourceLimiter::class.java)
    
    // 插件ID -> 资源限制
    private val pluginLimits = ConcurrentHashMap<String, ResourceLimits>()
    
    // 插件ID -> 资源使用统计
    private val pluginUsage = ConcurrentHashMap<String, ResourceUsage>()
    
    // 插件ID -> API调用计数器
    private val apiCallCounters = ConcurrentHashMap<String, AtomicLong>()
    
    // 插件ID -> 数据库查询计数器
    private val dbQueryCounters = ConcurrentHashMap<String, AtomicLong>()
    
    // 定时任务执行器
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    
    init {
        // 定期重置计数器
        scheduler.scheduleAtFixedRate(this::resetCounters, 1, 1, TimeUnit.MINUTES)
        
        // 定期检查资源使用情况
        scheduler.scheduleAtFixedRate(this::checkResourceUsage, 5, 5, TimeUnit.SECONDS)
    }
    
    /**
     * 注册插件资源限制
     * @param pluginId 插件ID
     * @param limits 资源限制
     */
    fun registerPlugin(pluginId: String, limits: ResourceLimits) {
        pluginLimits[pluginId] = limits
        pluginUsage[pluginId] = ResourceUsage()
        apiCallCounters[pluginId] = AtomicLong(0)
        dbQueryCounters[pluginId] = AtomicLong(0)
        logger.debug("注册插件资源限制: $pluginId")
    }
    
    /**
     * 注销插件
     * @param pluginId 插件ID
     */
    fun unregisterPlugin(pluginId: String) {
        pluginLimits.remove(pluginId)
        pluginUsage.remove(pluginId)
        apiCallCounters.remove(pluginId)
        dbQueryCounters.remove(pluginId)
        logger.debug("注销插件资源限制: $pluginId")
    }
    
    /**
     * 记录API调用
     * @param pluginId 插件ID
     * @throws PluginException 如果超出限制
     */
    fun recordApiCall(pluginId: String) {
        val counter = apiCallCounters[pluginId] ?: return
        val limits = pluginLimits[pluginId] ?: return
        
        val count = counter.incrementAndGet()
        if (count > limits.maxApiCallsPerMinute) {
            logger.warn("插件 $pluginId 超出API调用限制: $count/${limits.maxApiCallsPerMinute}")
            throw PluginException("插件 $pluginId 超出API调用限制")
        }
    }
    
    /**
     * 记录数据库查询
     * @param pluginId 插件ID
     * @throws PluginException 如果超出限制
     */
    fun recordDatabaseQuery(pluginId: String) {
        val counter = dbQueryCounters[pluginId] ?: return
        val limits = pluginLimits[pluginId] ?: return
        
        val count = counter.incrementAndGet()
        if (count > limits.maxDatabaseQueriesPerMinute) {
            logger.warn("插件 $pluginId 超出数据库查询限制: $count/${limits.maxDatabaseQueriesPerMinute}")
            throw PluginException("插件 $pluginId 超出数据库查询限制")
        }
    }
    
    /**
     * 检查数据库连接数
     * @param pluginId 插件ID
     * @param connections 连接数
     * @throws PluginException 如果超出限制
     */
    fun checkDatabaseConnections(pluginId: String, connections: Int) {
        val limits = pluginLimits[pluginId] ?: return
        
        if (connections > limits.maxDatabaseConnections) {
            logger.warn("插件 $pluginId 超出数据库连接限制: $connections/${limits.maxDatabaseConnections}")
            throw PluginException("插件 $pluginId 超出数据库连接限制")
        }
        
        // 更新使用统计
        val usage = pluginUsage[pluginId] ?: return
        pluginUsage[pluginId] = usage.copy(databaseConnections = connections)
    }
    
    /**
     * 检查线程数
     * @param pluginId 插件ID
     * @param threads 线程数
     * @throws PluginException 如果超出限制
     */
    fun checkThreadCount(pluginId: String, threads: Int) {
        val limits = pluginLimits[pluginId] ?: return
        
        if (threads > limits.maxThreads) {
            logger.warn("插件 $pluginId 超出线程数限制: $threads/${limits.maxThreads}")
            throw PluginException("插件 $pluginId 超出线程数限制")
        }
        
        // 更新使用统计
        val usage = pluginUsage[pluginId] ?: return
        pluginUsage[pluginId] = usage.copy(threadCount = threads)
    }
    
    /**
     * 获取插件资源使用情况
     * @param pluginId 插件ID
     * @return 资源使用统计
     */
    fun getResourceUsage(pluginId: String): ResourceUsage? {
        return pluginUsage[pluginId]
    }
    
    /**
     * 获取插件资源限制
     * @param pluginId 插件ID
     * @return 资源限制
     */
    fun getResourceLimits(pluginId: String): ResourceLimits? {
        return pluginLimits[pluginId]
    }
    
    /**
     * 更新资源使用统计
     * @param pluginId 插件ID
     * @param usage 资源使用统计
     */
    fun updateResourceUsage(pluginId: String, usage: ResourceUsage) {
        pluginUsage[pluginId] = usage
        
        // 检查是否超出限制
        val limits = pluginLimits[pluginId] ?: return
        
        if (usage.memoryUsageMb > limits.maxMemoryMb) {
            logger.warn("插件 $pluginId 超出内存限制: ${usage.memoryUsageMb}/${limits.maxMemoryMb} MB")
        }
        
        if (usage.cpuPercent > limits.maxCpuPercent) {
            logger.warn("插件 $pluginId 超出CPU限制: ${usage.cpuPercent}/${limits.maxCpuPercent}%")
        }
        
        if (usage.networkBandwidthKbps > limits.maxNetworkBandwidthKbps) {
            logger.warn("插件 $pluginId 超出网络带宽限制: ${usage.networkBandwidthKbps}/${limits.maxNetworkBandwidthKbps} Kbps")
        }
    }
    
    /**
     * 重置计数器
     */
    private fun resetCounters() {
        apiCallCounters.forEach { (pluginId, counter) ->
            counter.set(0)
        }
        
        dbQueryCounters.forEach { (pluginId, counter) ->
            counter.set(0)
        }
    }
    
    /**
     * 检查资源使用情况
     */
    private fun checkResourceUsage() {
        // 这里可以实现更复杂的资源监控逻辑
        // 例如使用JMX或其他系统API获取插件的内存和CPU使用情况
    }
    
    /**
     * 关闭资源限制器
     */
    fun shutdown() {
        scheduler.shutdown()
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow()
            }
        } catch (e: InterruptedException) {
            scheduler.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
} 