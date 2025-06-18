package com.github.kirer.kace.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KProperty

/**
 * 应用程序配置类
 */
class AppConfig private constructor(private var config: Config) {
    private val logger = LoggerFactory.getLogger(AppConfig::class.java)
    private val configWatcher: ConfigWatcher
    private val listeners = ConcurrentHashMap<String, MutableList<() -> Unit>>()
    
    // 配置加密密钥，从环境变量或配置文件获取
    val encryptionKey: String? = System.getenv("KACE_CONFIG_ENCRYPTION_KEY") ?: 
        try { File("config-encryption.key").takeIf { it.exists() }?.readText()?.trim() } catch (e: Exception) { null }
    
    // 使用委托属性，确保每次访问都获取最新配置
    val server by ConfigDelegate { ServerConfig(config.getConfig("server")) }
    val database by ConfigDelegate { DatabaseConfig(config.getConfig("database"), encryptionKey) }
    val security by ConfigDelegate { SecurityConfig(config.getConfig("security"), encryptionKey) }
    
    init {
        if (encryptionKey != null) {
            logger.info("配置加密已启用")
        } else {
            logger.warn("配置加密未启用，敏感信息将以明文存储")
        }
        
        // 设置配置文件监听
        val configFiles = listOf(
            "application.conf",
            System.getenv("KACE_ENV")?.let { "application.$it.conf" }
        ).filterNotNull()
        
        configWatcher = ConfigWatcher(configFiles) { changedFile ->
            reloadConfig(changedFile)
        }
        configWatcher.start()
    }
    
    /**
     * 重新加载配置
     */
    @Synchronized
    private fun reloadConfig(changedFile: String) {
        try {
            logger.info("重新加载配置文件: $changedFile")
            
            val newConfig = if (changedFile.contains("application.conf")) {
                // 重新加载默认配置
                load().config
            } else {
                // 重新加载特定配置文件
                val fileConfig = ConfigFactory.parseFile(File(changedFile))
                fileConfig.withFallback(config).resolve()
            }
            
            // 更新配置
            config = newConfig
            
            // 通知所有监听器
            notifyListeners()
            
            logger.info("配置重新加载成功")
        } catch (e: Exception) {
            logger.error("重新加载配置失败", e)
        }
    }
    
    /**
     * 添加配置变更监听器
     */
    fun addChangeListener(key: String, listener: () -> Unit) {
        listeners.computeIfAbsent(key) { mutableListOf() }.add(listener)
    }
    
    /**
     * 移除配置变更监听器
     */
    fun removeChangeListener(key: String) {
        listeners.remove(key)
    }
    
    /**
     * 通知所有监听器配置已变更
     */
    private fun notifyListeners() {
        listeners.values.forEach { listenerList ->
            listenerList.forEach { listener ->
                try {
                    listener()
                } catch (e: Exception) {
                    logger.error("执行配置变更监听器失败", e)
                }
            }
        }
    }
    
    /**
     * 关闭配置监听
     */
    fun shutdown() {
        configWatcher.stop()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: AppConfig? = null
        
        /**
         * 加载配置
         */
        fun load(): AppConfig {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: createConfig().also { INSTANCE = it }
            }
        }
        
        /**
         * 创建配置实例
         */
        private fun createConfig(): AppConfig {
            // 默认配置
            val defaultConfig = ConfigFactory.load("application.conf")
            
            // 环境特定配置
            val envConfig = System.getenv("KACE_ENV")?.let { env ->
                val envFile = File("application.$env.conf")
                if (envFile.exists()) {
                    ConfigFactory.parseFile(envFile)
                } else {
                    ConfigFactory.empty()
                }
            } ?: ConfigFactory.empty()
            
            // 合并配置，环境配置优先级高于默认配置
            val config = envConfig.withFallback(defaultConfig).resolve()
            
            return AppConfig(config)
        }
    }
    
    /**
     * 配置委托类，用于确保每次访问配置属性时都获取最新值
     */
    inner class ConfigDelegate<T>(private val initializer: () -> T) {
        private var value: T? = null
        
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return initializer()
        }
    }
}

/**
 * 服务器配置
 */
class ServerConfig(private val config: Config) {
    val host: String = config.getString("host")
    val port: Int = config.getInt("port")
}

/**
 * 数据库配置
 */
class DatabaseConfig(private val config: Config, private val encryptionKey: String?) {
    val driver: String = config.getString("driver")
    val url: String = config.getString("url")
    val user: String = config.getString("user")
    val password: String = if (encryptionKey != null) {
        EncryptedConfig.getString(config, "password", encryptionKey)
    } else {
        config.getString("password")
    }
}

/**
 * 安全配置
 */
class SecurityConfig(private val config: Config, private val encryptionKey: String?) {
    val jwtSecret: String = if (encryptionKey != null) {
        EncryptedConfig.getString(config, "jwtSecret", encryptionKey)
    } else {
        config.getString("jwtSecret")
    }
    val jwtIssuer: String = config.getString("jwtIssuer")
    val jwtAudience: String = config.getString("jwtAudience")
    val jwtRealm: String = config.getString("jwtRealm")
    val jwtExpirationInDays: Long = config.getLong("jwtExpirationInDays")
} 