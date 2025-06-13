package com.kace.gateway.config

import com.kace.common.security.jwt.JwtConfig
import com.kace.gateway.service.ServiceClient
import io.ktor.server.application.*
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory

/**
 * 配置依赖注入
 */
fun Application.configureKoin() {
    val logger = LoggerFactory.getLogger("KoinConfig")
    
    install(Koin) {
        slf4jLogger()
        modules(gatewayModule)
    }
    
    logger.info("Koin依赖注入已配置")
}

/**
 * 网关模块
 */
val gatewayModule = module {
    // 配置JWT
    single {
        JwtConfig(
            secret = getProperty("security.jwt.secret"),
            issuer = getProperty("security.jwt.issuer"),
            audience = getProperty("security.jwt.audience"),
            expirationInMinutes = getProperty<String>("security.jwt.expirationInMinutes").toLong()
        )
    }
    
    // 配置服务客户端
    single {
        ServiceClient(
            authServiceUrl = getProperty("services.auth.url"),
            userServiceUrl = getProperty("services.user.url"),
            contentServiceUrl = getProperty("services.content.url"),
            mediaServiceUrl = getProperty("services.media.url"),
            analyticsServiceUrl = getProperty("services.analytics.url"),
            notificationServiceUrl = getProperty("services.notification.url")
        )
    }
    
    // 配置Redis客户端
    single {
        val redisHost = getProperty<String>("redis.host")
        val redisPort = getProperty<String>("redis.port").toInt()
        val redisUri = "redis://$redisHost:$redisPort"
        
        RedisClient.create(redisUri)
    }
    
    // 配置Redis连接
    single {
        val redisClient = get<RedisClient>()
        redisClient.connect<String, String>()
    }
    
    // 配置Redis异步命令
    single {
        val connection = get<StatefulRedisConnection<String, String>>()
        connection.async()
    }
}

/**
 * 获取配置属性
 */
inline fun <reified T> ApplicationEnvironment.getConfigProperty(path: String): T {
    val property = this.config.property(path).getString()
    return when (T::class) {
        String::class -> property as T
        Int::class -> property.toInt() as T
        Long::class -> property.toLong() as T
        Boolean::class -> property.toBoolean() as T
        else -> throw IllegalArgumentException("Unsupported type: ${T::class.java.name}")
    }
} 