package com.kace.system

import com.kace.system.api.configureRouting
import com.kace.system.infrastructure.config.DatabaseConfig
import com.kace.system.infrastructure.config.appModule
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

/**
 * 应用程序主入口
 */
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("com.kace.system.SystemServiceApplicationKt")
    logger.info("启动系统服务...")
    
    try {
        embeddedServer(Netty, environment = applicationEngineEnvironment {
            log = logger
            
            module {
                main()
            }
            
            connector {
                host = "0.0.0.0"
                port = System.getenv("PORT")?.toIntOrNull() ?: 8082
            }
        }).start(wait = true)
    } catch (e: Exception) {
        logger.error("系统服务启动失败", e)
    }
}

/**
 * 应用程序配置
 */
fun Application.main() {
    // 初始化数据库
    DatabaseConfig.init()
    
    // 配置依赖注入
    install(Koin) {
        modules(appModule)
    }
    
    // 配置日志
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val uri = call.request.uri
            "$httpMethod $uri - $status - $userAgent"
        }
    }
    
    // 配置JSON序列化
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    // 配置CORS
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowCredentials = true
        anyHost() // 在生产环境中应该指定具体的域名
    }
    
    // 配置路由
    configureRouting()
}