package com.kace.analytics.api.controller

import com.kace.analytics.api.model.request.CreateReportRequest
import com.kace.analytics.api.model.request.QueryReportsRequest
import com.kace.analytics.api.model.request.ScheduleReportRequest
import com.kace.analytics.api.model.request.UpdateReportRequest
import com.kace.analytics.api.model.response.ReportExecutionResponse
import com.kace.analytics.api.model.response.ReportListResponse
import com.kace.analytics.api.model.response.ReportResponse
import com.kace.analytics.domain.model.Report
import com.kace.analytics.domain.service.ReportService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 报表控制器
 */
class ReportController(private val reportService: ReportService) {
    
    private val logger = LoggerFactory.getLogger(ReportController::class.java)
    
    /**
     * 配置路由
     */
    fun configureRoutes(routing: Routing) {
        routing.route("/api/v1/reports") {
            // 创建报表
            post {
                try {
                    val request = call.receive<CreateReportRequest>()
                    val userId = call.request.headers["X-User-ID"] ?: throw IllegalArgumentException("Missing user ID")
                    
                    val report = Report(
                        id = UUID.randomUUID(),
                        name = request.name,
                        description = request.description,
                        type = request.type,
                        query = request.query,
                        parameters = request.parameters?.mapValues { it.value as Any },
                        schedule = request.schedule,
                        lastRunAt = null,
                        createdBy = UUID.fromString(userId),
                        createdAt = Instant.now(),
                        updatedAt = Instant.now()
                    )
                    
                    val createdReport = reportService.createReport(report)
                    call.respond(HttpStatusCode.Created, ReportResponse.fromReport(createdReport))
                } catch (e: Exception) {
                    logger.error("Failed to create report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 获取单个报表
            get("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val report = reportService.getReport(UUID.fromString(id))
                    if (report != null) {
                        call.respond(HttpStatusCode.OK, ReportResponse.fromReport(report))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Report not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to get report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 更新报表
            put("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val request = call.receive<UpdateReportRequest>()
                    
                    val existingReport = reportService.getReport(UUID.fromString(id))
                        ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("error" to "Report not found"))
                    
                    val updatedReport = existingReport.copy(
                        name = request.name ?: existingReport.name,
                        description = request.description ?: existingReport.description,
                        type = request.type ?: existingReport.type,
                        query = request.query ?: existingReport.query,
                        parameters = request.parameters?.mapValues { it.value as Any } ?: existingReport.parameters,
                        schedule = request.schedule ?: existingReport.schedule,
                        updatedAt = Instant.now()
                    )
                    
                    val result = reportService.updateReport(updatedReport)
                    call.respond(HttpStatusCode.OK, ReportResponse.fromReport(result))
                } catch (e: Exception) {
                    logger.error("Failed to update report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 查询报表
            get {
                try {
                    val request = QueryReportsRequest(
                        type = call.request.queryParameters["type"],
                        createdBy = call.request.queryParameters["createdBy"],
                        limit = call.request.queryParameters["limit"]?.toInt() ?: 20,
                        offset = call.request.queryParameters["offset"]?.toInt() ?: 0
                    )
                    
                    val reports = when {
                        request.type != null && request.createdBy != null -> {
                            reportService.getReportsByType(request.type, request.limit, request.offset)
                                .filter { it.createdBy == UUID.fromString(request.createdBy) }
                        }
                        request.type != null -> {
                            reportService.getReportsByType(request.type, request.limit, request.offset)
                        }
                        request.createdBy != null -> {
                            reportService.getReportsByUser(UUID.fromString(request.createdBy), request.limit, request.offset)
                        }
                        else -> {
                            reportService.getAllReports(request.limit, request.offset)
                        }
                    }
                    
                    val total = when {
                        request.type != null -> {
                            reportService.countReportsByType(request.type)
                        }
                        else -> {
                            reports.size.toLong()
                        }
                    }
                    
                    call.respond(HttpStatusCode.OK, ReportListResponse(
                        reports = reports.map { ReportResponse.fromReport(it) },
                        total = total,
                        limit = request.limit,
                        offset = request.offset
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to query reports: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 获取计划报表
            get("/scheduled") {
                try {
                    val reports = reportService.getScheduledReports()
                    call.respond(HttpStatusCode.OK, reports.map { ReportResponse.fromReport(it) })
                } catch (e: Exception) {
                    logger.error("Failed to get scheduled reports: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 执行报表
            post("/{id}/execute") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val result = reportService.executeReport(UUID.fromString(id))
                    
                    val report = reportService.getReport(UUID.fromString(id))
                        ?: throw IllegalArgumentException("Report not found")
                    
                    call.respond(HttpStatusCode.OK, ReportExecutionResponse(
                        reportId = id,
                        reportName = report.name,
                        executionTime = Instant.now().toString(),
                        data = result
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to execute report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 设置报表计划
            post("/{id}/schedule") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val request = call.receive<ScheduleReportRequest>()
                    
                    val updatedReport = reportService.scheduleReport(UUID.fromString(id), request.schedule)
                    call.respond(HttpStatusCode.OK, ReportResponse.fromReport(updatedReport))
                } catch (e: Exception) {
                    logger.error("Failed to schedule report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 删除报表
            delete("/{id}") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val success = reportService.deleteReport(UUID.fromString(id))
                    if (success) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Report not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to delete report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }
} 