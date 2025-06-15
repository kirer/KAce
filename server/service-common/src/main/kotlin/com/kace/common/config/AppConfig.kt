package com.kace.common.config

import io.ktor.server.config.*

/**
 * 应用配置类，用于从配置文件中读取配置项
 */
class AppConfig(private val config: ApplicationConfig) {
    
    /**
     * 数据库配置
     */
    val database = DatabaseConfig()
    
    /**
     * 安全配置
     */
    val security = SecurityConfig()
    
    /**
     * Redis配置
     */
    val redis = RedisConfig()
    
    /**
     * RabbitMQ配置
     */
    val rabbitmq = RabbitMQConfig()
    
    /**
     * Elasticsearch配置
     */
    val elasticsearch = ElasticsearchConfig()
    
    /**
     * MinIO配置
     */
    val minio = MinioConfig()
    
    /**
     * 数据库配置类
     */
    inner class DatabaseConfig {
        val driverClassName = config.property("database.driverClassName").getString()
        val jdbcUrl = config.property("database.jdbcUrl").getString()
        val username = config.property("database.username").getString()
        val password = config.property("database.password").getString()
        val maximumPoolSize = config.property("database.maximumPoolSize").getString().toInt()
    }
    
    /**
     * 安全配置类
     */
    inner class SecurityConfig {
        val jwtSecret = config.property("security.jwt.secret").getString()
        val jwtIssuer = config.property("security.jwt.issuer").getString()
        val jwtAudience = config.property("security.jwt.audience").getString()
        val jwtRealm = config.property("security.jwt.realm").getString()
        val jwtExpirationInMinutes = config.property("security.jwt.expirationInMinutes").getString().toLong()
    }
    
    /**
     * Redis配置类
     */
    inner class RedisConfig {
        val host = config.property("redis.host").getString()
        val port = config.property("redis.port").getString().toInt()
    }
    
    /**
     * RabbitMQ配置类
     */
    inner class RabbitMQConfig {
        val host = config.property("rabbitmq.host").getString()
        val port = config.property("rabbitmq.port").getString().toInt()
        val username = config.property("rabbitmq.username").getString()
        val password = config.property("rabbitmq.password").getString()
    }
    
    /**
     * Elasticsearch配置类
     */
    inner class ElasticsearchConfig {
        val host = config.property("elasticsearch.host").getString()
        val port = config.property("elasticsearch.port").getString().toInt()
    }
    
    /**
     * MinIO配置类
     */
    inner class MinioConfig {
        val endpoint = config.property("minio.endpoint").getString()
        val accessKey = config.property("minio.accessKey").getString()
        val secretKey = config.property("minio.secretKey").getString()
        val bucket = config.property("minio.bucket").getString()
    }
} 