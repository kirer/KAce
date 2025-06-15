package com.kace.content.infrastructure.config

import com.kace.content.api.configureRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

/**
 * 配置应用程序的路由
 */
fun Application.configureRouting() {
    val logger = LoggerFactory.getLogger("RoutingConfig")
    
    // 健康检查路由
    routing {
        get("/health") {
            call.respondText("Content Service is healthy!")
        }
    }
    
    // API路由
    configureRoutes()
    
    logger.info("路由配置完成")
} 