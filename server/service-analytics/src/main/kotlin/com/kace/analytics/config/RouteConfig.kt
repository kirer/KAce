package com.kace.analytics.config

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import com.kace.analytics.api.controller.*
import org.koin.ktor.ext.inject

/**
 * 配置API路由
 */
fun Application.configureRouting() {
    val eventController: EventController by inject()
    val metricController: MetricController by inject()
    val reportController: ReportController by inject()
    
    routing {
        route("/api/v1") {
            // 公开API
            route("/events") {
                post { eventController.trackEvent(call) }
                post("/batch") { eventController.trackEvents(call) }
            }
            
            // 需要认证的API
            authenticate {
                // 事件API
                route("/events") {
                    get { eventController.getEvents(call) }
                    get("/{id}") { eventController.getEvent(call) }
                }
                
                // 指标API
                route("/metrics") {
                    get { metricController.getMetrics(call) }
                    get("/{name}") { metricController.getMetric(call) }
                    get("/dashboard") { metricController.getDashboardMetrics(call) }
                }
                
                // 报表API
                route("/reports") {
                    get { reportController.getReports(call) }
                    get("/{id}") { reportController.getReport(call) }
                    post { reportController.createReport(call) }
                    put("/{id}") { reportController.updateReport(call) }
                    delete("/{id}") { reportController.deleteReport(call) }
                    get("/{id}/export") { reportController.exportReport(call) }
                }
            }
        }
    }
} 