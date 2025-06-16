package com.kace.analytics

import com.kace.analytics.api.configureRoutes
import com.kace.analytics.infrastructure.config.analyticsModule
import com.kace.analytics.infrastructure.persistence.entity.Events
import com.kace.analytics.infrastructure.persistence.entity.Metrics
import com.kace.analytics.infrastructure.persistence.entity.Reports
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

/**
 * 分析服务应用入口
 */
fun main() {
    embeddedServer(Netty, port = 8083, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * 应用模块配置
 */
fun Application.module() {
    // 配置数据库
    configureDatabase()
    
    // 配置依赖注入
    install(Koin) {
        slf4jLogger()
        modules(analyticsModule)
    }
    
    // 配置CORS
    install(CORS) {
        anyHost()
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
        allowHeader("X-User-ID")
    }
    
    // 配置内容协商
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    // 配置日志
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
    }
    
    // 配置路由
    configureRoutes()
}

/**
 * 配置数据库连接和表结构
 */
private fun configureDatabase() {
    val driverClassName = System.getenv("JDBC_DRIVER") ?: "org.postgresql.Driver"
    val jdbcUrl = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/kace_analytics"
    val username = System.getenv("JDBC_USER") ?: "kace"
    val password = System.getenv("JDBC_PASSWORD") ?: "kace"
    
    Database.connect(jdbcUrl, driver = driverClassName, user = username, password = password)
    
    transaction {
        // 创建表
        SchemaUtils.create(Events, Metrics, Reports)
    }
} 