package com.github.kirer.kace.plugin

import com.github.kirer.kace.exception.PluginException
import com.github.kirer.kace.log.LoggerFactory
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.jar.JarFile

/**
 * 插件元数据读取器
 * 负责从插件JAR文件中读取元数据
 */
class PluginMetadataReader {
    private val logger = LoggerFactory.getSystemLogger(PluginMetadataReader::class.java)
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        coerceInputValues = true
    }
    
    /**
     * 从插件JAR文件中读取元数据
     * @param pluginFile 插件文件
     * @return 插件元数据
     * @throws PluginException 如果读取失败
     */
    fun readMetadata(pluginFile: File): PluginMetadata {
        try {
            JarFile(pluginFile).use { jar ->
                val pluginJsonEntry = jar.getJarEntry("plugin.json")
                    ?: throw PluginException("插件JAR中缺少plugin.json文件")
                
                jar.getInputStream(pluginJsonEntry).use { inputStream ->
                    val pluginJson = inputStream.bufferedReader().readText()
                    
                    try {
                        val metadata = json.decodeFromString(PluginMetadata.serializer(), pluginJson)
                        
                        // 验证必填字段
                        if (metadata.id.isBlank()) {
                            throw PluginException("插件ID不能为空")
                        }
                        
                        if (metadata.name.isBlank()) {
                            throw PluginException("插件名称不能为空")
                        }
                        
                        if (metadata.version.isBlank()) {
                            throw PluginException("插件版本不能为空")
                        }
                        
                        if (metadata.mainClass.isBlank()) {
                            throw PluginException("插件主类不能为空")
                        }
                        
                        // 添加文件哈希
                        val validator = PluginValidator()
                        val fileHash = validator.calculateSHA256(pluginFile)
                        
                        return metadata.copy(fileHash = fileHash)
                    } catch (e: SerializationException) {
                        throw PluginException("解析插件元数据时发生错误", e)
                    }
                }
            }
        } catch (e: IOException) {
            throw PluginException("读取插件文件时发生错误", e)
        } catch (e: Exception) {
            if (e is PluginException) throw e
            throw PluginException("读取插件元数据时发生未知错误", e)
        }
    }
    
    /**
     * 从插件JAR文件中读取主类
     * @param pluginFile 插件文件
     * @param metadata 插件元数据
     * @param classLoader 类加载器
     * @return 插件主类
     * @throws PluginException 如果读取失败
     */
    fun loadPluginClass(pluginFile: File, metadata: PluginMetadata, classLoader: ClassLoader): Class<*> {
        try {
            val pluginClass = classLoader.loadClass(metadata.mainClass)
            
            // 验证是否实现了Plugin接口
            if (!Plugin::class.java.isAssignableFrom(pluginClass)) {
                throw PluginException("插件主类 ${metadata.mainClass} 没有实现 Plugin 接口")
            }
            
            return pluginClass
        } catch (e: ClassNotFoundException) {
            throw PluginException("无法找到插件主类: ${metadata.mainClass}", e)
        } catch (e: Exception) {
            throw PluginException("加载插件主类时发生错误", e)
        }
    }
    
    /**
     * 创建插件实例
     * @param pluginClass 插件类
     * @return 插件实例
     * @throws PluginException 如果创建失败
     */
    fun createPluginInstance(pluginClass: Class<*>): Plugin {
        try {
            val constructor = pluginClass.getDeclaredConstructor()
            constructor.isAccessible = true
            val instance = constructor.newInstance()
            
            if (instance !is Plugin) {
                throw PluginException("插件实例不是Plugin类型")
            }
            
            return instance
        } catch (e: Exception) {
            throw PluginException("创建插件实例时发生错误", e)
        }
    }
} 