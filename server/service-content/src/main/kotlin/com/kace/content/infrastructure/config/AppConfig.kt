package com.kace.content.infrastructure.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import java.io.File

/**
 * 应用配置
 */
class AppConfig(config: Config) {
    /**
     * 数据库配置
     */
    val database = DatabaseConfig(config.getConfig("database"))
    
    /**
     * Elasticsearch配置
     */
    val elasticsearch = ElasticsearchConfig(config.getConfig("elasticsearch"))
    
    /**
     * 服务配置
     */
    val service = ServiceConfig(config.getConfig("service"))
    
    /**
     * 数据库配置
     */
    class DatabaseConfig(config: Config) {
        val url = config.getString("url")
        val driver = config.getString("driver")
        val username = config.getString("username")
        val password = config.getString("password")
        val poolSize = config.getInt("poolSize")
    }
    
    /**
     * Elasticsearch配置
     */
    class ElasticsearchConfig(config: Config) {
        val hosts = config.getStringList("hosts")
        val contentIndex = config.getString("contentIndex")
    }
    
    /**
     * 服务配置
     */
    class ServiceConfig(config: Config) {
        val port = config.getInt("port")
        val host = config.getString("host")
    }
    
    companion object {
        /**
         * 从文件加载配置
         */
        fun fromFile(filePath: String): AppConfig {
            val file = File(filePath)
            val config = if (file.exists()) {
                ConfigFactory.parseFile(file).resolve()
            } else {
                ConfigFactory.load()
            }
            return AppConfig(config)
        }
        
        /**
         * 从Ktor应用配置加载
         */
        fun fromApplicationConfig(config: ApplicationConfig): AppConfig {
            val databaseConfig = config.config("database")
            val elasticsearchConfig = config.config("elasticsearch")
            val serviceConfig = config.config("service")
            
            val configMap = mapOf(
                "database" to mapOf(
                    "url" to databaseConfig.property("url").getString(),
                    "driver" to databaseConfig.property("driver").getString(),
                    "username" to databaseConfig.property("username").getString(),
                    "password" to databaseConfig.property("password").getString(),
                    "poolSize" to databaseConfig.property("poolSize").getString()
                ),
                "elasticsearch" to mapOf(
                    "hosts" to elasticsearchConfig.property("hosts").getList(),
                    "contentIndex" to elasticsearchConfig.property("contentIndex").getString()
                ),
                "service" to mapOf(
                    "port" to serviceConfig.property("port").getString(),
                    "host" to serviceConfig.property("host").getString()
                )
            )
            
            val config = ConfigFactory.parseMap(configMap).resolve()
            return AppConfig(config)
        }
    }
} 