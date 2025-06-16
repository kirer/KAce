package com.kace.system.api.controller

import com.kace.system.api.request.LogEventRequest
import com.kace.system.api.request.LogSearchRequest
import com.kace.system.api.response.LogStatisticsResponse
import com.kace.system.api.response.SystemLogResponse
import com.kace.system.domain.model.SystemLog
import com.kace.system.domain.service.SystemLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/api/system/logs")
@Tag(name = "系统日志管理", description = "系统日志的查询、创建和分析功能")
class SystemLogController(private val systemLogService: SystemLogService) {

    @PostMapping
    @Operation(summary = "记录系统日志", description = "记录一条系统日志")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_LOGGER')")
    fun logEvent(
        @Valid @RequestBody logEventRequest: LogEventRequest,
        request: HttpServletRequest
    ): ResponseEntity<SystemLogResponse> {
        // 可以从请求中获取客户端IP
        val clientIp = request.getHeader("X-Forwarded-For") ?: request.remoteAddr
        
        val systemLog = systemLogService.logEvent(
            type = logEventRequest.type,
            module = logEventRequest.module,
            operation = logEventRequest.operation,
            content = logEventRequest.content,
            userId = logEventRequest.userId
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SystemLogResponse.fromDomain(systemLog))
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取日志详情", description = "根据日志ID获取日志详情")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getLogById(
        @Parameter(description = "日志ID", required = true)
        @PathVariable id: String
    ): ResponseEntity<SystemLogResponse> {
        val systemLog = systemLogService.getLogById(id)
            ?: return ResponseEntity.notFound().build()
            
        return ResponseEntity.ok(SystemLogResponse.fromDomain(systemLog))
    }

    @GetMapping
    @Operation(summary = "查询系统日志", description = "分页查询系统日志，可根据类型、模块等条件筛选")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun searchLogs(@Valid logSearchRequest: LogSearchRequest): ResponseEntity<Page<SystemLogResponse>> {
        val pageable = PageRequest.of(logSearchRequest.page, logSearchRequest.size)
        
        val logs = systemLogService.searchLogs(
            type = logSearchRequest.type,
            module = logSearchRequest.module,
            operation = logSearchRequest.operation,
            content = logSearchRequest.content,
            userId = logSearchRequest.userId,
            startTime = logSearchRequest.startTime,
            endTime = logSearchRequest.endTime,
            pageable = pageable
        ).map { SystemLogResponse.fromDomain(it) }
        
        return ResponseEntity.ok(logs)
    }

    @GetMapping("/types/{type}")
    @Operation(summary = "根据类型查询日志", description = "根据日志类型分页查询系统日志")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getLogsByType(
        @Parameter(description = "日志类型", required = true)
        @PathVariable type: String,
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<SystemLogResponse>> {
        val pageable = PageRequest.of(page, size)
        val logs = systemLogService.getLogsByType(type, pageable)
            .map { SystemLogResponse.fromDomain(it) }
            
        return ResponseEntity.ok(logs)
    }

    @GetMapping("/modules/{module}")
    @Operation(summary = "根据模块查询日志", description = "根据模块名称分页查询系统日志")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getLogsByModule(
        @Parameter(description = "模块名称", required = true)
        @PathVariable module: String,
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<SystemLogResponse>> {
        val pageable = PageRequest.of(page, size)
        val logs = systemLogService.getLogsByModule(module, pageable)
            .map { SystemLogResponse.fromDomain(it) }
            
        return ResponseEntity.ok(logs)
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "根据用户查询日志", description = "根据用户ID分页查询系统日志")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getLogsByUserId(
        @Parameter(description = "用户ID", required = true)
        @PathVariable userId: String,
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<SystemLogResponse>> {
        val pageable = PageRequest.of(page, size)
        val logs = systemLogService.getLogsByUserId(userId, pageable)
            .map { SystemLogResponse.fromDomain(it) }
            
        return ResponseEntity.ok(logs)
    }

    @GetMapping("/timerange")
    @Operation(summary = "根据时间范围查询日志", description = "根据时间范围分页查询系统日志")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getLogsByTimeRange(
        @Parameter(description = "开始时间（格式：yyyy-MM-ddTHH:mm:ss）", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime,
        @Parameter(description = "结束时间（格式：yyyy-MM-ddTHH:mm:ss）", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime,
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<SystemLogResponse>> {
        val pageable = PageRequest.of(page, size)
        val logs = systemLogService.getLogsByTimeRange(startTime, endTime, pageable)
            .map { SystemLogResponse.fromDomain(it) }
            
        return ResponseEntity.ok(logs)
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取日志统计信息", description = "获取指定时间范围内的日志统计信息")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getLogStatistics(
        @Parameter(description = "开始时间（格式：yyyy-MM-ddTHH:mm:ss），默认为7天前")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @Parameter(description = "结束时间（格式：yyyy-MM-ddTHH:mm:ss），默认为当前时间")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?
    ): ResponseEntity<LogStatisticsResponse> {
        val now = LocalDateTime.now()
        val actualStartTime = startTime ?: now.minus(7, ChronoUnit.DAYS)
        val actualEndTime = endTime ?: now
        
        // 获取按类型统计数据
        val countByType = systemLogService.searchLogs(
            type = null, 
            module = null,
            operation = null,
            content = null,
            userId = null,
            startTime = actualStartTime,
            endTime = actualEndTime,
            pageable = PageRequest.of(0, 1) // 仅获取总数信息
        ).totalElements
        
        // 这里创建一个示例统计响应
        // 在实际实现中，应该从仓库中获取真实的统计数据
        val errorCount = systemLogService.getLogsByType(SystemLog.TYPE_ERROR, PageRequest.of(0, 1)).totalElements
        val warningCount = systemLogService.getLogsByType(SystemLog.TYPE_WARNING, PageRequest.of(0, 1)).totalElements
        
        val typeMap = mutableMapOf<String, Long>()
        val moduleMap = mutableMapOf<String, Long>()
        
        // 实际中应当查询并填充这些统计数据
        
        val response = LogStatisticsResponse(
            startTime = actualStartTime,
            endTime = actualEndTime,
            countByType = typeMap,
            countByModule = moduleMap,
            totalCount = countByType,
            errorCount = errorCount,
            warningCount = warningCount
        )
        
        return ResponseEntity.ok(response)
    }
    
    @DeleteMapping("/before/{date}")
    @Operation(summary = "清理历史日志", description = "删除指定日期之前的所有日志")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun cleanLogsBefore(
        @Parameter(description = "截止日期（格式：yyyy-MM-ddTHH:mm:ss）", required = true)
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) date: LocalDateTime
    ): ResponseEntity<Map<String, Any>> {
        val count = systemLogService.cleanLogsBefore(date)
        
        val response = mapOf(
            "message" to "成功清理历史日志",
            "count" to count,
            "beforeDate" to date.toString()
        )
        
        return ResponseEntity.ok(response)
    }
} 