package com.github.kirer.kace.plugin

import com.github.kirer.kace.exception.PluginException
import com.github.kirer.kace.log.LoggerFactory
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

/**
 * 插件类加载器
 * 负责加载插件JAR文件中的类
 */
class PluginClassLoader(
    private val pluginFile: File,
    parent: ClassLoader = PluginClassLoader::class.java.classLoader
) : URLClassLoader(arrayOf(pluginFile.toURI().toURL()), parent) {
    
    private val logger = LoggerFactory.getSystemLogger(PluginClassLoader::class.java)
    private val classCache = ConcurrentHashMap<String, Class<*>>()
    
    companion object {
        // 系统类前缀，这些类将由父类加载器加载
        private val SYSTEM_PACKAGES = arrayOf(
            "java.",
            "javax.",
            "kotlin.",
            "kotlinx.",
            "org.slf4j.",
            "org.jetbrains.",
            "io.ktor.",
            "com.github.kirer.kace.plugin.api."  // 插件API包，由父类加载器加载
        )
    }
    
    /**
     * 加载类
     * @param name 类名
     * @param resolve 是否解析
     * @return 加载的类
     */
    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(getClassLoadingLock(name)) {
            // 检查类是否已加载
            var clazz = findLoadedClass(name)
            
            if (clazz == null) {
                // 检查是否是系统类
                if (isSystemClass(name)) {
                    try {
                        clazz = parent.loadClass(name)
                        logger.debug("Loaded system class: $name from parent")
                    } catch (e: ClassNotFoundException) {
                        // 忽略，尝试从插件加载
                    }
                }
                
                // 如果仍未找到，尝试从插件加载
                if (clazz == null) {
                    try {
                        clazz = findClass(name)
                        logger.debug("Loaded plugin class: $name from plugin jar")
                    } catch (e: ClassNotFoundException) {
                        // 如果插件中没有找到，尝试从父类加载器加载
                        clazz = parent.loadClass(name)
                        logger.debug("Loaded class: $name from parent as fallback")
                    }
                }
            }
            
            if (resolve) {
                resolveClass(clazz)
            }
            
            return clazz
        }
    }
    
    /**
     * 查找类
     * @param name 类名
     * @return 找到的类
     */
    @Throws(ClassNotFoundException::class)
    override fun findClass(name: String): Class<*> {
        return classCache.computeIfAbsent(name) {
            try {
                super.findClass(name)
            } catch (e: ClassNotFoundException) {
                throw ClassNotFoundException("无法在插件 ${pluginFile.name} 中找到类 $name", e)
            } catch (e: Exception) {
                throw PluginException("加载插件类 $name 时发生错误", e)
            }
        }
    }
    
    /**
     * 判断是否是系统类
     * @param name 类名
     * @return 是否是系统类
     */
    private fun isSystemClass(name: String): Boolean {
        return SYSTEM_PACKAGES.any { name.startsWith(it) }
    }
    
    /**
     * 获取插件文件
     * @return 插件文件
     */
    fun getPluginFile(): File {
        return pluginFile
    }
    
    /**
     * 获取插件URL
     * @return 插件URL
     */
    fun getPluginURL(): URL {
        return pluginFile.toURI().toURL()
    }
} 