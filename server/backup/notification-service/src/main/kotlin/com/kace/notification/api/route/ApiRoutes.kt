package com.kace.notification.api.route

import com.kace.notification.api.controller.channelRoutes
import com.kace.notification.api.controller.notificationRoutes
import com.kace.notification.api.controller.preferenceRoutes
import com.kace.notification.api.controller.templateRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

/**
 * API路由配置
 */
fun Application.configureRouting() {
    routing {
        notificationRoutes()
        templateRoutes()
        preferenceRoutes()
        channelRoutes()
    }
} 