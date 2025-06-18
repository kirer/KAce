package com.github.kirer.kace.plugin.route

import com.github.kirer.kace.api.route.RouteGroup
import com.github.kirer.kace.api.route.RouteRegistrar
import com.github.kirer.kace.api.route.RouteRegistry
import com.github.kirer.kace.log.LoggerFactory
import com.github.kirer.kace.plugin.Plugin
import com.github.kirer.kace.plugin.PluginManager
import com.github.kirer.kace.plugin.security.ApiAccessLevel
import com.github.kirer.kace.plugin.security.PluginApiAccessControl

/**
 * 路由接口
 * 简化版本，用于插件路由注册
 */
interface PluginRoute {
    /**
     * 添加GET路由
     */
    fun get(path: String, handler: () -> Unit)
    
    /**
     * 添加POST路由
     */
    fun post(path: String, handler: () -> Unit)
    
    /**
     * 添加PUT路由
     */
    fun put(path: String, handler: () -> Unit)
    
    /**
     * 添加DELETE路由
     */
    fun delete(path: String, handler: () -> Unit)
}

/**
 * 插件路由注册器接口
 * 插件可以实现此接口来注册自己的路由
 */
interface PluginRouteRegistrar {
    /**
     * 获取路由路径前缀
     * @return 路由路径前缀
     */
    fun getRoutePathPrefix(): String
    
    /**
     * 注册插件路由
     * @param route 路由对象
     */
    fun registerPluginRoutes(route: PluginRoute)
}

/**
 * 创建RouteRegistrar适配器
 * 将PluginRouteRegistrar包装为RouteRegistrar
 */
@Suppress("UNCHECKED_CAST")
class PluginRouteRegistrarAdapter(
    private val plugin: PluginRouteRegistrar,
    private val pluginId: String,
    private val apiAccessControl: PluginApiAccessControl
) {
    // 获取路径前缀
    fun getPathPrefix(): String = plugin.getRoutePathPrefix()
    
    // 注册路由
    fun registerRoutes(route: Any) {
        // 创建PluginRoute适配器
        val routeAdapter = object : PluginRoute {
            override fun get(path: String, handler: () -> Unit) {
                val apiPath = "${getPathPrefix()}$path"
                apiAccessControl.validateAccess(pluginId, apiPath, ApiAccessLevel.READ_ONLY)
                handler()
            }
            
            override fun post(path: String, handler: () -> Unit) {
                val apiPath = "${getPathPrefix()}$path"
                apiAccessControl.validateAccess(pluginId, apiPath, ApiAccessLevel.READ_WRITE)
                handler()
            }
            
            override fun put(path: String, handler: () -> Unit) {
                val apiPath = "${getPathPrefix()}$path"
                apiAccessControl.validateAccess(pluginId, apiPath, ApiAccessLevel.READ_WRITE)
                handler()
            }
            
            override fun delete(path: String, handler: () -> Unit) {
                val apiPath = "${getPathPrefix()}$path"
                apiAccessControl.validateAccess(pluginId, apiPath, ApiAccessLevel.READ_WRITE)
                handler()
            }
        }
        
        // 注册插件路由
        plugin.registerPluginRoutes(routeAdapter)
    }
}

/**
 * 插件路由管理器
 * 负责管理和注册插件的路由
 */
class PluginRouteManager(
    private val routeRegistry: RouteRegistry,
    private val pluginManager: PluginManager,
    private val apiAccessControl: PluginApiAccessControl
) {
    private val logger = LoggerFactory.getSystemLogger(PluginRouteManager::class.java)
    
    // 插件ID -> 路由注册器ID
    private val pluginRouteRegistrars = mutableMapOf<String, String>()
    
    // 插件ID -> 路由组名称
    private val pluginRouteGroups = mutableMapOf<String, String>()
    
    /**
     * 初始化
     */
    fun initialize() {
        // 创建插件API根路由组
        val pluginsGroup = RouteGroup(
            name = "plugins",
            pathPrefix = "/api/plugins",
            description = "插件API",
            tags = setOf("plugins")
        )
        routeRegistry.createRouteGroup(pluginsGroup)
        
        logger.info("初始化插件路由管理器")
    }
    
    /**
     * 注册插件路由
     * @param plugin 插件实例
     */
    fun registerPluginRoutes(plugin: Plugin) {
        val pluginId = plugin.metadata.id
        
        // 如果插件实现了PluginRouteRegistrar接口，注册其路由
        if (plugin is PluginRouteRegistrar) {
            val pathPrefix = plugin.getRoutePathPrefix()
            
            // 创建插件路由组
            val routeGroupName = "plugin-$pluginId"
            val routeGroup = RouteGroup(
                name = routeGroupName,
                pathPrefix = "/$pluginId",
                description = plugin.metadata.name,
                tags = setOf(pluginId),
                parent = "plugins"
            )
            routeRegistry.createRouteGroup(routeGroup)
            pluginRouteGroups[pluginId] = routeGroupName
            
            // 创建路由注册器适配器
            val registrarId = "plugin-route-$pluginId"
            val adapter = PluginRouteRegistrarAdapter(plugin, pluginId, apiAccessControl)
            
            // 使用JDK动态代理创建RouteRegistrar实例
            // 通过反射创建与RouteRegistrar接口兼容的对象
            @Suppress("UNCHECKED_CAST")
            val registrar = java.lang.reflect.Proxy.newProxyInstance(
                RouteRegistrar::class.java.classLoader,
                arrayOf(RouteRegistrar::class.java)
            ) { _, method, args ->
                when (method.name) {
                    "getPathPrefix" -> adapter.getPathPrefix()
                    "registerRoutes" -> {
                        if (args != null && args.isNotEmpty()) {
                            adapter.registerRoutes(args[0])
                        }
                        null
                    }
                    else -> throw UnsupportedOperationException("不支持的方法: ${method.name}")
                }
            } as RouteRegistrar
            
            routeRegistry.registerRegistrar(registrarId, registrar)
            pluginRouteRegistrars[pluginId] = registrarId
            
            logger.info("注册插件路由: $pluginId, 路径前缀: $pathPrefix")
        }
    }
    
    /**
     * 注销插件路由
     * @param pluginId 插件ID
     */
    fun unregisterPluginRoutes(pluginId: String) {
        // 注销路由注册器
        val registrarId = pluginRouteRegistrars[pluginId]
        if (registrarId != null) {
            routeRegistry.unregisterRegistrar(registrarId)
            pluginRouteRegistrars.remove(pluginId)
        }
        
        // 删除路由组
        val groupName = pluginRouteGroups[pluginId]
        if (groupName != null) {
            routeRegistry.removeRouteGroup(groupName)
            pluginRouteGroups.remove(pluginId)
        }
        
        logger.info("注销插件路由: $pluginId")
    }
    
    /**
     * 注册所有已启用的插件路由
     */
    fun registerAllPluginRoutes() {
        val plugins = pluginManager.getPlugins().values.toList()
        for (plugin in plugins) {
            registerPluginRoutes(plugin)
        }
        logger.info("注册所有插件路由, 共 ${plugins.size} 个")
    }
    
    /**
     * 注销所有插件路由
     */
    fun unregisterAllPluginRoutes() {
        val pluginIds = pluginRouteRegistrars.keys.toList()
        for (pluginId in pluginIds) {
            unregisterPluginRoutes(pluginId)
        }
        logger.info("注销所有插件路由")
    }
} 