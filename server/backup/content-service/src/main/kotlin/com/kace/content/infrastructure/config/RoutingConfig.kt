package com.kace.content.infrastructure.config

import com.kace.content.api.controller.ContentTypeController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

/**
 * 路由配置
 */
fun Application.configureRouting() {
    val logger = LoggerFactory.getLogger("RoutingConfig")
    
    // 获取控制器
    val contentTypeController by inject<ContentTypeController>()
    
    // 配置路由
    routing {
        route("/api/v1") {
            // 内容类型路由
            contentTypeController.registerRoutes(this)
            
            // 内容路由
            // TODO: 添加内容路由
            
            // 分类路由
            // TODO: 添加分类路由
            
            // 标签路由
            // TODO: 添加标签路由
            
            // 搜索路由
            // TODO: 添加搜索路由
        }
    }
    
    logger.info("路由配置完成")
} 