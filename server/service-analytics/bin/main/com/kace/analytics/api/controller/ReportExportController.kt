package com.kace.analytics.api.controller

import com.kace.analytics.domain.model.ReportFormat
import com.kace.analytics.domain.service.ReportService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * 报表导出控制器
 */
class ReportExportController(private val reportService: ReportService) {
    
    private val logger = LoggerFactory.getLogger(ReportExportController::class.java)
    
    /**
     * 配置路由
     */
    fun configureRoutes(routing: Routing) {
        routing.route("/api/v1/reports") {
            // 导出报表
            get("/{id}/export") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val format = call.request.queryParameters["format"]?.let {
                        try {
                            ReportFormat.valueOf(it.uppercase())
                        } catch (e: IllegalArgumentException) {
                            ReportFormat.PDF
                        }
                    } ?: ReportFormat.PDF
                    
                    val report = reportService.getReport(UUID.fromString(id))
                        ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Report not found"))
                    
                    val reportBytes = reportService.generateReportFile(UUID.fromString(id), format)
                    
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                    val timestamp = LocalDateTime.now().format(dateFormatter)
                    val sanitizedName = report.name.replace("[\\s\\\\/:*?\"<>|]".toRegex(), "_")
                    
                    val fileName = "${sanitizedName}_${timestamp}.${getFileExtension(format)}"
                    val contentType = getContentType(format)
                    
                    call.response.header(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, fileName)
                            .toString()
                    )
                    
                    call.respondBytes(
                        bytes = reportBytes,
                        contentType = contentType,
                        status = HttpStatusCode.OK
                    )
                } catch (e: Exception) {
                    logger.error("Failed to export report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 发送报表
            post("/{id}/send") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val request = call.receive<SendReportRequest>()
                    
                    val report = reportService.getReport(UUID.fromString(id)) 
                        ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Report not found"))
                    
                    val format = try {
                        ReportFormat.valueOf(request.format.uppercase())
                    } catch (e: IllegalArgumentException) {
                        ReportFormat.PDF
                    }
                    
                    val success = reportService.sendReport(UUID.fromString(id), request.recipients, format)
                    
                    if (success) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Report sent successfully"))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to send report"))
                    }
                } catch (e: Exception) {
                    logger.error("Failed to send report: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
            
            // 获取报表执行历史
            get("/{id}/history") {
                try {
                    val id = call.parameters["id"] ?: throw IllegalArgumentException("Missing report ID")
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
                    
                    val report = reportService.getReport(UUID.fromString(id))
                        ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Report not found"))
                    
                    val history = reportService.getReportExecutionHistory(UUID.fromString(id), page, size)
                    
                    call.respond(HttpStatusCode.OK, mapOf(
                        "reportId" to id,
                        "reportName" to report.name,
                        "history" to history
                    ))
                } catch (e: Exception) {
                    logger.error("Failed to get report execution history: ${e.message}", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }
    
    /**
     * 根据报表格式获取文件扩展名
     */
    private fun getFileExtension(format: ReportFormat): String {
        return when (format) {
            ReportFormat.PDF -> "pdf"
            ReportFormat.EXCEL -> "xlsx"
            ReportFormat.CSV -> "csv"
            ReportFormat.HTML -> "html"
        }
    }
    
    /**
     * 根据报表格式获取内容类型
     */
    private fun getContentType(format: ReportFormat): ContentType {
        return when (format) {
            ReportFormat.PDF -> ContentType.Application.Pdf
            ReportFormat.EXCEL -> ContentType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            ReportFormat.CSV -> ContentType.parse("text/csv")
            ReportFormat.HTML -> ContentType.Text.Html
        }
    }
}

/**
 * 发送报表请求
 */
@Serializable
data class SendReportRequest(
    val recipients: List<String>,
    val format: String = "PDF"
) 