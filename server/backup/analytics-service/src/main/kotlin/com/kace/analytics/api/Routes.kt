package com.kace.analytics.api

import com.kace.analytics.api.controller.EventController
import com.kace.analytics.api.controller.MetricController
import com.kace.analytics.api.controller.ReportController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * 配置所有API路由
 */
fun Application.configureRoutes() {
    routing {
        // 注入控制器
        val eventController by inject<EventController>()
        val metricController by inject<MetricController>()
        val reportController by inject<ReportController>()
        
        // 配置各个控制器的路由
        eventController.configureRoutes(this)
        metricController.configureRoutes(this)
        reportController.configureRoutes(this)
    }
} 