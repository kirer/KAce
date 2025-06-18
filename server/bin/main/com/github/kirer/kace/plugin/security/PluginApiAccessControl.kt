package com.github.kirer.kace.plugin.security

import com.github.kirer.kace.exception.PluginException
import com.github.kirer.kace.log.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * API访问权限
 */
enum class ApiAccessLevel {
    /**
     * 禁止访问
     */
    DENIED,
    
    /**
     * 只读访问
     */
    READ_ONLY,
    
    /**
     * 读写访问
     */
    READ_WRITE,
    
    /**
     * 完全访问
     */
    FULL_ACCESS
}

/**
 * API访问规则
 */
data class ApiAccessRule(
    val apiPath: String,
    val accessLevel: ApiAccessLevel
)

/**
 * 插件API访问控制
 * 负责管理插件对系统API的访问权限
 */
class PluginApiAccessControl {
    private val logger = LoggerFactory.getSystemLogger(PluginApiAccessControl::class.java)
    
    // 插件ID -> API访问规则列表
    private val pluginApiRules = ConcurrentHashMap<String, MutableList<ApiAccessRule>>()
    
    // 系统API路径 -> 默认访问级别
    private val systemApiDefaults = ConcurrentHashMap<String, ApiAccessLevel>()
    
    init {
        // 设置系统API的默认访问级别
        systemApiDefaults["/api/system/**"] = ApiAccessLevel.DENIED
        systemApiDefaults["/api/plugins/**"] = ApiAccessLevel.READ_ONLY
        systemApiDefaults["/api/config/**"] = ApiAccessLevel.DENIED
        systemApiDefaults["/api/users/**"] = ApiAccessLevel.READ_ONLY
        systemApiDefaults["/api/logs/**"] = ApiAccessLevel.READ_ONLY
        systemApiDefaults["/api/events/**"] = ApiAccessLevel.READ_ONLY
    }
    
    /**
     * 注册插件API访问规则
     * @param pluginId 插件ID
     * @param rules API访问规则列表
     */
    fun registerPluginApiRules(pluginId: String, rules: List<ApiAccessRule>) {
        pluginApiRules[pluginId] = rules.toMutableList()
        logger.debug("注册插件API访问规则: $pluginId, 规则数: ${rules.size}")
    }
    
    /**
     * 更新插件API访问规则
     * @param pluginId 插件ID
     * @param apiPath API路径
     * @param accessLevel 访问级别
     */
    fun updatePluginApiRule(pluginId: String, apiPath: String, accessLevel: ApiAccessLevel) {
        val rules = pluginApiRules.getOrPut(pluginId) { mutableListOf() }
        
        val existingRule = rules.find { it.apiPath == apiPath }
        if (existingRule != null) {
            rules.remove(existingRule)
            rules.add(existingRule.copy(accessLevel = accessLevel))
        } else {
            rules.add(ApiAccessRule(apiPath, accessLevel))
        }

        logger.debug("更新插件API访问规则: {}, {} -> {}", pluginId, apiPath, accessLevel)
    }
    
    /**
     * 移除插件API访问规则
     * @param pluginId 插件ID
     */
    fun removePluginApiRules(pluginId: String) {
        pluginApiRules.remove(pluginId)
        logger.debug("移除插件API访问规则: $pluginId")
    }
    
    /**
     * 检查插件是否有权限访问API
     * @param pluginId 插件ID
     * @param apiPath API路径
     * @param requiredLevel 所需访问级别
     * @return 是否有权限
     */
    fun checkAccess(pluginId: String, apiPath: String, requiredLevel: ApiAccessLevel): Boolean {
        // 获取插件的API访问规则
        val rules = pluginApiRules[pluginId] ?: return false
        
        // 查找最匹配的规则
        val matchedRule = findMatchingRule(rules, apiPath)
        
        // 如果找到匹配的规则，检查访问级别
        if (matchedRule != null) {
            return hasAccessLevel(matchedRule.accessLevel, requiredLevel)
        }
        
        // 如果没有找到匹配的规则，检查系统默认规则
        val defaultLevel = findDefaultAccessLevel(apiPath)
        return hasAccessLevel(defaultLevel, requiredLevel)
    }
    
    /**
     * 验证插件API访问权限
     * @param pluginId 插件ID
     * @param apiPath API路径
     * @param requiredLevel 所需访问级别
     * @throws PluginException 如果没有权限
     */
    fun validateAccess(pluginId: String, apiPath: String, requiredLevel: ApiAccessLevel) {
        if (!checkAccess(pluginId, apiPath, requiredLevel)) {
            logger.warn("插件 $pluginId 尝试未授权访问API: $apiPath, 所需级别: $requiredLevel")
            throw PluginException("插件 $pluginId 没有权限访问API: $apiPath (所需级别: $requiredLevel)")
        }
    }
    
    /**
     * 查找匹配的API访问规则
     * @param rules API访问规则列表
     * @param apiPath API路径
     * @return 匹配的规则，如果没有找到则返回null
     */
    private fun findMatchingRule(rules: List<ApiAccessRule>, apiPath: String): ApiAccessRule? {
        // 首先尝试精确匹配
        val exactMatch = rules.find { it.apiPath == apiPath }
        if (exactMatch != null) {
            return exactMatch
        }
        
        // 然后尝试通配符匹配
        return rules.filter { isWildcardMatch(it.apiPath, apiPath) }
            .maxByOrNull { it.apiPath.length }
    }
    
    /**
     * 查找默认访问级别
     * @param apiPath API路径
     * @return 默认访问级别
     */
    private fun findDefaultAccessLevel(apiPath: String): ApiAccessLevel {
        // 首先尝试精确匹配
        val exactMatch = systemApiDefaults[apiPath]
        if (exactMatch != null) {
            return exactMatch
        }
        
        // 然后尝试通配符匹配
        val wildcardMatch = systemApiDefaults.entries
            .filter { isWildcardMatch(it.key, apiPath) }
            .maxByOrNull { it.key.length }
            
        return wildcardMatch?.value ?: ApiAccessLevel.DENIED
    }
    
    /**
     * 检查是否是通配符匹配
     * @param pattern 模式
     * @param path 路径
     * @return 是否匹配
     */
    private fun isWildcardMatch(pattern: String, path: String): Boolean {
        if (pattern.endsWith("/**")) {
            val prefix = pattern.removeSuffix("/**")
            return path.startsWith(prefix)
        }
        
        if (pattern.endsWith("/*")) {
            val prefix = pattern.removeSuffix("/*")
            val pathWithoutQuery = path.split("?")[0]
            val segments = pathWithoutQuery.split("/")
            val patternSegments = prefix.split("/")
            
            if (segments.size != patternSegments.size + 1) {
                return false
            }
            
            for (i in patternSegments.indices) {
                if (patternSegments[i] != segments[i]) {
                    return false
                }
            }
            
            return true
        }
        
        return false
    }
    
    /**
     * 检查是否有所需的访问级别
     * @param actualLevel 实际访问级别
     * @param requiredLevel 所需访问级别
     * @return 是否有权限
     */
    private fun hasAccessLevel(actualLevel: ApiAccessLevel, requiredLevel: ApiAccessLevel): Boolean {
        // 如果实际级别是DENIED，则没有任何权限
        if (actualLevel == ApiAccessLevel.DENIED) {
            return false
        }
        
        // 如果实际级别是FULL_ACCESS，则有所有权限
        if (actualLevel == ApiAccessLevel.FULL_ACCESS) {
            return true
        }
        
        // 如果所需级别是READ_ONLY，则READ_ONLY、READ_WRITE和FULL_ACCESS都可以满足
        if (requiredLevel == ApiAccessLevel.READ_ONLY) {
            return actualLevel == ApiAccessLevel.READ_ONLY || 
                   actualLevel == ApiAccessLevel.READ_WRITE ||
                   actualLevel == ApiAccessLevel.FULL_ACCESS
        }
        
        // 如果所需级别是READ_WRITE，则READ_WRITE和FULL_ACCESS可以满足
        if (requiredLevel == ApiAccessLevel.READ_WRITE) {
            return actualLevel == ApiAccessLevel.READ_WRITE ||
                   actualLevel == ApiAccessLevel.FULL_ACCESS
        }
        
        // 如果所需级别是FULL_ACCESS，则只有FULL_ACCESS可以满足
        return actualLevel == ApiAccessLevel.FULL_ACCESS
    }
} 