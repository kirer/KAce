package com.github.kirer.kace.config

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * 配置文件监听器
 * 用于监听配置文件变化并自动重新加载
 */
class ConfigWatcher(
    private val configFiles: List<String>,
    private val onChange: (String) -> Unit
) {
    private val logger = LoggerFactory.getLogger(ConfigWatcher::class.java)
    private val watchService = FileSystems.getDefault().newWatchService()
    private val watchKeys = mutableMapOf<WatchKey, Path>()
    private var watchJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * 启动监听
     */
    fun start() {
        try {
            // 注册所有配置文件所在的目录
            configFiles.forEach { configFile ->
                val file = File(configFile)
                if (file.exists()) {
                    val dir = file.parentFile.toPath()
                    registerDirectory(dir)
                    logger.info("开始监听配置文件: $configFile")
                } else {
                    logger.warn("配置文件不存在，无法监听: $configFile")
                }
            }
            
            // 启动监听协程
            watchJob = scope.launch {
                while (isActive) {
                    val key = withContext(Dispatchers.IO) {
                        watchService.take() // 阻塞等待事件
                    }
                    
                    val path = watchKeys[key]
                    if (path != null) {
                        for (event in key.pollEvents()) {
                            val kind = event.kind()
                            
                            // 忽略OVERFLOW事件
                            if (kind == OVERFLOW) {
                                continue
                            }
                            
                            val fileName = (event.context() as Path).toString()
                            val fullPath = path.resolve(fileName).toString()
                            
                            // 检查是否是我们关注的配置文件
                            if (configFiles.any { it.endsWith(fileName) } && (kind == ENTRY_MODIFY)) {
                                logger.info("检测到配置文件变化: $fullPath")
                                delay(100) // 短暂延迟，确保文件写入完成
                                onChange(fullPath)
                            }
                        }
                        
                        // 重置key，准备接收新的事件
                        val valid = key.reset()
                        if (!valid) {
                            watchKeys.remove(key)
                            if (watchKeys.isEmpty()) {
                                break
                            }
                        }
                    }
                }
            }
            
            logger.info("配置文件监听器已启动")
        } catch (e: Exception) {
            logger.error("启动配置文件监听器失败", e)
        }
    }
    
    /**
     * 停止监听
     */
    fun stop() {
        try {
            watchJob?.cancel()
            watchService.close()
            watchKeys.clear()
            logger.info("配置文件监听器已停止")
        } catch (e: Exception) {
            logger.error("停止配置文件监听器失败", e)
        }
    }
    
    /**
     * 注册目录到监听服务
     */
    private fun registerDirectory(dir: Path) {
        if (!dir.exists()) {
            logger.warn("目录不存在，无法监听: $dir")
            return
        }
        
        val key = dir.register(
            watchService,
            ENTRY_CREATE,
            ENTRY_DELETE,
            ENTRY_MODIFY
        )
        watchKeys[key] = dir
    }
} 