package com.github.kirer.kace.plugin

import com.github.kirer.kace.exception.PluginException
import com.github.kirer.kace.log.LoggerFactory
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 插件验证结果
 */
data class PluginValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

/**
 * 插件验证器
 * 负责验证插件JAR文件的完整性和安全性
 */
class PluginValidator {
    private val logger = LoggerFactory.getSystemLogger(PluginValidator::class.java)
    
    // 必须存在的文件
    private val requiredFiles = listOf(
        "plugin.json",
        "META-INF/MANIFEST.MF"
    )
    
    // 禁止的包名
    private val forbiddenPackages = listOf(
        "com.github.kirer.kace.core",
        "com.github.kirer.kace.config",
        "com.github.kirer.kace.security"
    )
    
    // 禁止的类名
    private val forbiddenClasses = listOf(
        "java.lang.System",
        "java.lang.Runtime",
        "java.lang.ProcessBuilder"
    )
    
    /**
     * 验证插件JAR文件
     * @param pluginFile 插件文件
     * @return 验证结果
     */
    fun validate(pluginFile: File): PluginValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        if (!pluginFile.exists()) {
            errors.add("插件文件不存在: ${pluginFile.absolutePath}")
            return PluginValidationResult(false, errors)
        }
        
        if (!pluginFile.isFile) {
            errors.add("插件路径不是一个文件: ${pluginFile.absolutePath}")
            return PluginValidationResult(false, errors)
        }
        
        if (!pluginFile.name.endsWith(".jar")) {
            errors.add("插件文件不是JAR格式: ${pluginFile.name}")
            return PluginValidationResult(false, errors)
        }
        
        try {
            JarFile(pluginFile).use { jar ->
                // 检查必须存在的文件
                for (requiredFile in requiredFiles) {
                    if (jar.getJarEntry(requiredFile) == null) {
                        errors.add("缺少必需的文件: $requiredFile")
                    }
                }
                
                // 检查插件元数据
                val pluginJsonEntry = jar.getJarEntry("plugin.json")
                if (pluginJsonEntry != null) {
                    try {
                        jar.getInputStream(pluginJsonEntry).use { inputStream ->
                            val pluginJson = inputStream.bufferedReader().readText()
                            // 这里可以进一步解析和验证plugin.json内容
                        }
                    } catch (e: Exception) {
                        errors.add("无法读取插件元数据: ${e.message}")
                    }
                }
                
                // 检查所有类文件
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if (isClassFile(entry)) {
                        val className = entryToClassName(entry)
                        
                        // 检查是否使用了禁止的包名
                        for (forbiddenPackage in forbiddenPackages) {
                            if (className.startsWith(forbiddenPackage)) {
                                errors.add("类 $className 使用了禁止的包名: $forbiddenPackage")
                            }
                        }
                        
                        // 检查是否引用了禁止的类
                        // 注意：这里只是简单检查类名，实际上需要更复杂的字节码分析
                        for (forbiddenClass in forbiddenClasses) {
                            if (className == forbiddenClass) {
                                errors.add("使用了禁止的类: $forbiddenClass")
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            errors.add("无法打开JAR文件: ${e.message}")
            return PluginValidationResult(false, errors)
        } catch (e: Exception) {
            errors.add("验证插件时发生错误: ${e.message}")
            return PluginValidationResult(false, errors)
        }
        
        return PluginValidationResult(errors.isEmpty(), errors, warnings)
    }
    
    /**
     * 计算文件的SHA-256哈希值
     * @param file 文件
     * @return 哈希值的十六进制字符串
     */
    fun calculateSHA256(file: File): String {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream().use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            val hashBytes = digest.digest()
            return hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            throw PluginException("计算文件哈希值时发生错误", e)
        }
    }
    
    /**
     * 判断JAR条目是否是类文件
     * @param entry JAR条目
     * @return 是否是类文件
     */
    private fun isClassFile(entry: JarEntry): Boolean {
        return !entry.isDirectory && entry.name.endsWith(".class")
    }
    
    /**
     * 将JAR条目转换为类名
     * @param entry JAR条目
     * @return 类名
     */
    private fun entryToClassName(entry: JarEntry): String {
        return entry.name
            .removeSuffix(".class")
            .replace('/', '.')
    }
} 