package com.github.kirer.kace.plugin

import com.github.kirer.kace.exception.PluginException
import com.github.kirer.kace.log.LoggerFactory
import java.util.*

/**
 * 依赖解析结果
 */
data class DependencyResolutionResult(
    val loadOrder: List<String>,
    val missingDependencies: Map<String, List<String>> = emptyMap(),
    val circularDependencies: List<List<String>> = emptyList()
)

/**
 * 插件依赖管理器
 * 负责解析插件之间的依赖关系，检测循环依赖，并确定加载顺序
 */
class PluginDependencyManager {
    private val logger = LoggerFactory.getSystemLogger(PluginDependencyManager::class.java)
    
    /**
     * 解析插件依赖关系
     * @param plugins 插件元数据映射表（插件ID -> 元数据）
     * @return 依赖解析结果
     */
    fun resolveDependencies(plugins: Map<String, PluginMetadata>): DependencyResolutionResult {
        val loadOrder = mutableListOf<String>()
        val missingDependencies = mutableMapOf<String, List<String>>()
        val circularDependencies = mutableListOf<List<String>>()
        
        // 构建依赖图
        val dependencyGraph = buildDependencyGraph(plugins)
        
        // 检查缺失的依赖
        checkMissingDependencies(plugins, dependencyGraph, missingDependencies)
        
        // 检测循环依赖
        detectCircularDependencies(dependencyGraph, circularDependencies)
        
        // 如果有循环依赖，无法确定加载顺序
        if (circularDependencies.isNotEmpty()) {
            logger.warn("检测到循环依赖，无法确定加载顺序")
            return DependencyResolutionResult(emptyList(), missingDependencies, circularDependencies)
        }
        
        // 使用拓扑排序确定加载顺序
        val visited = mutableSetOf<String>()
        val tempMarked = mutableSetOf<String>()
        
        fun visit(pluginId: String) {
            if (tempMarked.contains(pluginId)) {
                // 循环依赖，但这应该已经在之前的检查中被捕获
                return
            }
            
            if (!visited.contains(pluginId)) {
                tempMarked.add(pluginId)
                
                dependencyGraph[pluginId]?.forEach { dependency ->
                    if (plugins.containsKey(dependency)) {
                        visit(dependency)
                    }
                }
                
                tempMarked.remove(pluginId)
                visited.add(pluginId)
                loadOrder.add(pluginId)
            }
        }
        
        // 对所有插件执行拓扑排序
        plugins.keys.forEach { pluginId ->
            if (!visited.contains(pluginId)) {
                visit(pluginId)
            }
        }
        
        return DependencyResolutionResult(loadOrder, missingDependencies, circularDependencies)
    }
    
    /**
     * 构建依赖图
     * @param plugins 插件元数据映射表
     * @return 依赖图（插件ID -> 依赖插件ID列表）
     */
    private fun buildDependencyGraph(plugins: Map<String, PluginMetadata>): Map<String, List<String>> {
        val graph = mutableMapOf<String, List<String>>()
        
        plugins.forEach { (pluginId, metadata) ->
            // 从PluginDependency对象中提取依赖插件ID
            val dependencies = metadata.dependencies.map { it.id }
            graph[pluginId] = dependencies
        }
        
        return graph
    }
    
    /**
     * 检查缺失的依赖
     * @param plugins 插件元数据映射表
     * @param dependencyGraph 依赖图
     * @param missingDependencies 缺失依赖结果
     */
    private fun checkMissingDependencies(
        plugins: Map<String, PluginMetadata>,
        dependencyGraph: Map<String, List<String>>,
        missingDependencies: MutableMap<String, List<String>>
    ) {
        dependencyGraph.forEach { (pluginId, dependencies) ->
            val missing = dependencies.filter { !plugins.containsKey(it) }
            if (missing.isNotEmpty()) {
                missingDependencies[pluginId] = missing
                logger.warn("插件 $pluginId 缺少依赖: ${missing.joinToString(", ")}")
            }
        }
    }
    
    /**
     * 检测循环依赖
     * @param dependencyGraph 依赖图
     * @param circularDependencies 循环依赖结果
     */
    private fun detectCircularDependencies(
        dependencyGraph: Map<String, List<String>>,
        circularDependencies: MutableList<List<String>>
    ) {
        val visited = mutableSetOf<String>()
        val recursionStack = mutableSetOf<String>()
        val pathStack = Stack<String>()
        
        fun dfs(pluginId: String) {
            if (recursionStack.contains(pluginId)) {
                // 找到循环依赖
                val cycle = mutableListOf<String>()
                var i = pathStack.size - 1
                while (i >= 0) {
                    val node = pathStack[i]
                    cycle.add(node)
                    if (node == pluginId) {
                        break
                    }
                    i--
                }
                cycle.reverse()
                circularDependencies.add(cycle)
                return
            }
            
            if (visited.contains(pluginId)) {
                return
            }
            
            visited.add(pluginId)
            recursionStack.add(pluginId)
            pathStack.push(pluginId)
            
            dependencyGraph[pluginId]?.forEach { dependency ->
                dfs(dependency)
            }
            
            recursionStack.remove(pluginId)
            pathStack.pop()
        }
        
        dependencyGraph.keys.forEach { pluginId ->
            if (!visited.contains(pluginId)) {
                dfs(pluginId)
            }
        }
    }
    
    /**
     * 检查版本兼容性
     * @param plugins 插件元数据映射表
     * @throws PluginException 如果存在版本不兼容的情况
     */
    fun checkVersionCompatibility(plugins: Map<String, PluginMetadata>) {
        plugins.forEach { (pluginId, metadata) ->
            metadata.dependencies.forEach { dependency ->
                val dependencyPlugin = plugins[dependency.id]
                
                if (dependencyPlugin != null) {
                    val dependencyVersion = dependencyPlugin.version
                    val requiredVersion = dependency.version
                    
                    if (!isVersionCompatible(dependencyVersion, requiredVersion)) {
                        throw PluginException("插件 $pluginId 需要依赖 ${dependency.id} 版本 $requiredVersion，但实际版本是 $dependencyVersion")
                    }
                }
            }
        }
    }
    
    /**
     * 检查版本是否兼容
     * @param actualVersion 实际版本
     * @param requiredVersion 需求版本
     * @return 是否兼容
     */
    private fun isVersionCompatible(actualVersion: String, requiredVersion: String): Boolean {
        // 简单实现，只检查版本是否相等
        // 实际应用中，应该实现更复杂的版本比较逻辑，如语义化版本比较
        return actualVersion == requiredVersion || requiredVersion == "*"
    }
} 