package com.github.kirer.kace.api.route

import com.github.kirer.kace.log.LoggerFactory
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 路由注册接口
 * 所有需要注册路由的组件都应该实现此接口
 */
interface RouteRegistrar {
    /**
     * 获取路由路径前缀
     * @return 路由路径前缀
     */
    fun getPathPrefix(): String
    
    /**
     * 注册路由
     * @param route 路由对象
     */
    fun registerRoutes(route: Route)
}

/**
 * 路由组
 * 用于组织和管理相关的路由
 */
data class RouteGroup(
    val name: String,
    val pathPrefix: String,
    val description: String = "",
    val tags: Set<String> = emptySet(),
    val parent: String? = null
)

/**
 * 路由注册表
 * 负责管理和注册所有路由
 */
class RouteRegistry {
    private val logger = LoggerFactory.getSystemLogger(RouteRegistry::class.java)
    
    // 路由注册器映射表（ID -> 注册器）
    private val registrars = ConcurrentHashMap<String, RouteRegistrar>()
    
    // 路由组映射表（组名 -> 路由组）
    private val routeGroups = ConcurrentHashMap<String, RouteGroup>()
    
    /**
     * 注册路由注册器
     * @param id 注册器ID
     * @param registrar 路由注册器
     */
    fun registerRegistrar(id: String, registrar: RouteRegistrar) {
        registrars[id] = registrar
        logger.debug("注册路由注册器: $id, 路径前缀: ${registrar.getPathPrefix()}")
    }
    
    /**
     * 注销路由注册器
     * @param id 注册器ID
     */
    fun unregisterRegistrar(id: String) {
        registrars.remove(id)
        logger.debug("注销路由注册器: $id")
    }
    
    /**
     * 创建路由组
     * @param group 路由组
     */
    fun createRouteGroup(group: RouteGroup) {
        // 检查父组是否存在
        if (group.parent != null && !routeGroups.containsKey(group.parent)) {
            throw IllegalArgumentException("父路由组不存在: ${group.parent}")
        }
        
        routeGroups[group.name] = group
        logger.debug("创建路由组: ${group.name}, 路径前缀: ${group.pathPrefix}")
    }
    
    /**
     * 删除路由组
     * @param name 路由组名称
     */
    fun removeRouteGroup(name: String) {
        routeGroups.remove(name)
        logger.debug("删除路由组: $name")
    }
    
    /**
     * 获取路由组
     * @param name 路由组名称
     * @return 路由组，如果不存在则返回null
     */
    fun getRouteGroup(name: String): RouteGroup? {
        return routeGroups[name]
    }
    
    /**
     * 获取所有路由组
     * @return 所有路由组
     */
    fun getAllRouteGroups(): List<RouteGroup> {
        return routeGroups.values.toList()
    }
    
    /**
     * 获取路由组的完整路径
     * @param groupName 路由组名称
     * @return 完整路径
     */
    fun getFullPath(groupName: String): String {
        val group = routeGroups[groupName] ?: return ""
        
        val path = StringBuilder(group.pathPrefix)
        var currentParent = group.parent
        
        while (currentParent != null) {
            val parentGroup = routeGroups[currentParent] ?: break
            path.insert(0, parentGroup.pathPrefix)
            currentParent = parentGroup.parent
        }
        
        return path.toString()
    }
    
    /**
     * 应用所有路由注册器
     * @param application Ktor应用程序
     */
    fun applyAll(application: Application) {
        application.routing {
            // 按照路由组的层次结构注册路由
            val rootGroups = routeGroups.values.filter { it.parent == null }
            for (rootGroup in rootGroups) {
                registerGroupRoutes(this, rootGroup)
            }
            
            // 注册没有关联到路由组的注册器
            val ungroupedRegistrars = registrars.values.filter { registrar ->
                val prefix = registrar.getPathPrefix()
                routeGroups.values.none { it.pathPrefix == prefix }
            }
            
            for (registrar in ungroupedRegistrars) {
                route(registrar.getPathPrefix()) {
                    registrar.registerRoutes(this)
                }
            }
        }
    }
    
    /**
     * 递归注册路由组的路由
     * @param parentRoute 父路由
     * @param group 路由组
     */
    private fun registerGroupRoutes(parentRoute: Route, group: RouteGroup) {
        parentRoute.route(group.pathPrefix) {
            // 注册与此路由组关联的注册器
            val groupRegistrars = registrars.values.filter { it.getPathPrefix() == group.pathPrefix }
            for (registrar in groupRegistrars) {
                registrar.registerRoutes(this)
            }
            
            // 递归注册子组
            val childGroups = routeGroups.values.filter { it.parent == group.name }
            for (childGroup in childGroups) {
                registerGroupRoutes(this, childGroup)
            }
        }
    }
} 