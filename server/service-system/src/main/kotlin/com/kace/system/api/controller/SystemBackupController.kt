package com.kace.system.api.controller

import com.kace.system.api.request.BackupSearchRequest
import com.kace.system.api.request.CreateBackupPolicyRequest
import com.kace.system.api.request.CreateBackupRequest
import com.kace.system.api.response.BackupPolicyResponse
import com.kace.system.api.response.SystemBackupResponse
import com.kace.system.domain.model.BackupPolicy
import com.kace.system.domain.service.SystemBackupService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.core.io.InputStreamResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.UUID

@RestController
@RequestMapping("/api/system/backups")
@Tag(name = "系统备份管理", description = "系统备份和备份策略的管理功能")
class SystemBackupController(private val systemBackupService: SystemBackupService) {

    @PostMapping
    @Operation(summary = "创建系统备份", description = "创建一个新的系统备份任务")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun createBackup(
        @Valid @RequestBody request: CreateBackupRequest,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<SystemBackupResponse> {
        val userId = userDetails?.username
        
        val backup = systemBackupService.createBackup(
            name = request.name,
            description = request.description,
            type = request.type,
            serviceType = request.serviceType,
            serviceName = request.serviceName,
            userId = userId,
            compress = request.compress,
            encrypt = request.encrypt,
            encryptionAlgorithm = request.encryptionAlgorithm,
            retentionDays = request.retentionDays,
            parameters = request.parameters
        )
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SystemBackupResponse.fromDomain(backup))
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取备份详情", description = "根据ID获取备份详情")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getBackupById(
        @Parameter(description = "备份ID", required = true)
        @PathVariable id: String
    ): ResponseEntity<SystemBackupResponse> {
        val backup = systemBackupService.getBackupById(id)
            ?: return ResponseEntity.notFound().build()
            
        return ResponseEntity.ok(SystemBackupResponse.fromDomain(backup))
    }

    @GetMapping
    @Operation(summary = "搜索备份", description = "根据条件搜索系统备份")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun searchBackups(@Valid backupSearchRequest: BackupSearchRequest): ResponseEntity<Page<SystemBackupResponse>> {
        val pageable = PageRequest.of(backupSearchRequest.page, backupSearchRequest.size)
        
        val backups = systemBackupService.searchBackups(
            type = backupSearchRequest.type,
            serviceType = backupSearchRequest.serviceType,
            serviceName = backupSearchRequest.serviceName,
            status = backupSearchRequest.status,
            createdBy = backupSearchRequest.createdBy,
            startTime = backupSearchRequest.startTime,
            endTime = backupSearchRequest.endTime,
            pageable = pageable
        ).map { SystemBackupResponse.fromDomain(it) }
        
        return ResponseEntity.ok(backups)
    }

    @GetMapping("/types/{type}")
    @Operation(summary = "根据类型查询备份", description = "根据备份类型查询备份")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getBackupsByType(
        @Parameter(description = "备份类型", required = true)
        @PathVariable type: String,
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<SystemBackupResponse>> {
        val pageable = PageRequest.of(page, size)
        val backups = systemBackupService.searchBackups(
            type = type,
            pageable = pageable
        ).map { SystemBackupResponse.fromDomain(it) }
        
        return ResponseEntity.ok(backups)
    }

    @GetMapping("/services/{serviceType}")
    @Operation(summary = "根据服务类型查询备份", description = "根据服务类型查询备份")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getBackupsByServiceType(
        @Parameter(description = "服务类型", required = true)
        @PathVariable serviceType: String,
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<SystemBackupResponse>> {
        val pageable = PageRequest.of(page, size)
        val backups = systemBackupService.getBackupsByServiceType(serviceType, pageable)
            .map { SystemBackupResponse.fromDomain(it) }
        
        return ResponseEntity.ok(backups)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除备份", description = "根据ID删除备份")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun deleteBackup(
        @Parameter(description = "备份ID", required = true)
        @PathVariable id: String,
        @Parameter(description = "是否同时删除备份文件", example = "true")
        @RequestParam(required = false, defaultValue = "true") deleteFiles: Boolean
    ): ResponseEntity<Map<String, Any>> {
        val success = systemBackupService.deleteBackup(id, deleteFiles)
        
        return if (success) {
            ResponseEntity.ok(mapOf(
                "message" to "备份已删除",
                "id" to id,
                "deletedFiles" to deleteFiles
            ))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "恢复备份", description = "从指定备份恢复数据")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun restoreBackup(
        @Parameter(description = "备份ID", required = true)
        @PathVariable id: String,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<Map<String, Any>> {
        val userId = userDetails?.username
        
        val success = systemBackupService.restoreBackup(id, userId)
        
        return if (success) {
            ResponseEntity.ok(mapOf(
                "message" to "备份恢复过程已启动",
                "id" to id,
                "startedBy" to (userId ?: "system")
            ))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf(
                "message" to "备份恢复失败，可能备份不存在或状态不允许恢复",
                "id" to id
            ))
        }
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "下载备份文件", description = "下载指定备份的文件")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun downloadBackup(
        @Parameter(description = "备份ID", required = true)
        @PathVariable id: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val backup = systemBackupService.getBackupById(id)
            ?: return ResponseEntity.notFound().build<Any>()
            
        val fileStream = systemBackupService.getBackupFileStream(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "备份文件不存在"))
                
        val resource = InputStreamResource(fileStream)
        
        val fileName = if (backup.name.contains(".")) {
            backup.name
        } else {
            val extension = when {
                backup.encrypted -> ".enc"
                backup.filePath.endsWith(".tar.gz") -> ".tar.gz"
                backup.filePath.endsWith(".zip") -> ".zip"
                backup.serviceType == "DATABASE" -> ".sql"
                else -> ".bin"
            }
            "${backup.name}${extension}"
        }
        
        return ResponseEntity.ok()
            .contentLength(backup.fileSize)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")
            .body(resource)
    }

    @PostMapping("/clean")
    @Operation(summary = "清理过期备份", description = "清理系统中的过期备份")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun cleanExpiredBackups(): ResponseEntity<Map<String, Any>> {
        val count = systemBackupService.cleanExpiredBackups()
        
        return ResponseEntity.ok(mapOf(
            "message" to "过期备份清理完成",
            "count" to count
        ))
    }

    @GetMapping("/supported-services")
    @Operation(summary = "获取支持的备份服务", description = "获取系统支持的所有备份服务")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getSupportedBackupServices(): ResponseEntity<List<Map<String, Any>>> {
        val services = systemBackupService.getSupportedBackupServices()
        
        return ResponseEntity.ok(services)
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "获取备份详情", description = "获取包括备份过程日志在内的详细信息")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getBackupDetails(
        @Parameter(description = "备份ID", required = true)
        @PathVariable id: String
    ): ResponseEntity<Map<String, Any>> {
        val details = systemBackupService.getBackupDetails(id)
            ?: return ResponseEntity.notFound().build()
            
        return ResponseEntity.ok(details)
    }

    // 备份策略相关API
    
    @PostMapping("/policies")
    @Operation(summary = "创建备份策略", description = "创建一个新的备份策略")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun createBackupPolicy(
        @Valid @RequestBody request: CreateBackupPolicyRequest,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<BackupPolicyResponse> {
        val userId = userDetails?.username ?: "system"
        
        val backupPolicy = BackupPolicy(
            id = UUID.randomUUID().toString(),
            name = request.name,
            description = request.description,
            backupType = request.backupType,
            serviceType = request.serviceType,
            serviceName = request.serviceName,
            schedule = request.schedule,
            retentionDays = request.retentionDays,
            maxBackups = request.maxBackups,
            storagePathTemplate = request.storagePathTemplate,
            enabled = request.enabled,
            compress = request.compress,
            encrypt = request.encrypt,
            encryptionAlgorithm = request.encryptionAlgorithm,
            preBackupCommand = request.preBackupCommand,
            postBackupCommand = request.postBackupCommand,
            backupWindowStart = request.backupWindowStart,
            backupWindowEnd = request.backupWindowEnd,
            lastExecuted = null,
            nextScheduled = null,
            createdBy = userId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            parameters = request.parameters
        )
        
        val savedPolicy = systemBackupService.createBackupPolicy(backupPolicy)
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(BackupPolicyResponse.fromDomain(savedPolicy))
    }

    @GetMapping("/policies")
    @Operation(summary = "获取所有备份策略", description = "分页获取系统中的所有备份策略")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getAllBackupPolicies(
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<BackupPolicyResponse>> {
        val pageable = PageRequest.of(page, size)
        val policies = systemBackupService.getAllBackupPolicies(pageable)
            .map { BackupPolicyResponse.fromDomain(it) }
        
        return ResponseEntity.ok(policies)
    }

    @GetMapping("/policies/{id}")
    @Operation(summary = "获取备份策略详情", description = "根据ID获取备份策略详情")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getBackupPolicyById(
        @Parameter(description = "策略ID", required = true)
        @PathVariable id: String
    ): ResponseEntity<BackupPolicyResponse> {
        val policy = systemBackupService.getBackupPolicyById(id)
            ?: return ResponseEntity.notFound().build()
            
        return ResponseEntity.ok(BackupPolicyResponse.fromDomain(policy))
    }

    @PutMapping("/policies/{id}")
    @Operation(summary = "更新备份策略", description = "更新现有的备份策略")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun updateBackupPolicy(
        @Parameter(description = "策略ID", required = true)
        @PathVariable id: String,
        @Valid @RequestBody request: CreateBackupPolicyRequest
    ): ResponseEntity<BackupPolicyResponse> {
        // 先获取现有策略
        val existingPolicy = systemBackupService.getBackupPolicyById(id)
            ?: return ResponseEntity.notFound().build()
            
        // 创建更新后的策略对象
        val updatedPolicy = existingPolicy.copy(
            name = request.name,
            description = request.description,
            backupType = request.backupType,
            serviceType = request.serviceType,
            serviceName = request.serviceName,
            schedule = request.schedule,
            retentionDays = request.retentionDays,
            maxBackups = request.maxBackups,
            storagePathTemplate = request.storagePathTemplate,
            enabled = request.enabled,
            compress = request.compress,
            encrypt = request.encrypt,
            encryptionAlgorithm = request.encryptionAlgorithm,
            preBackupCommand = request.preBackupCommand,
            postBackupCommand = request.postBackupCommand,
            backupWindowStart = request.backupWindowStart,
            backupWindowEnd = request.backupWindowEnd,
            updatedAt = LocalDateTime.now(),
            parameters = request.parameters
        )
        
        val savedPolicy = systemBackupService.updateBackupPolicy(updatedPolicy)
        
        return ResponseEntity.ok(BackupPolicyResponse.fromDomain(savedPolicy))
    }

    @DeleteMapping("/policies/{id}")
    @Operation(summary = "删除备份策略", description = "根据ID删除备份策略")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun deleteBackupPolicy(
        @Parameter(description = "策略ID", required = true)
        @PathVariable id: String
    ): ResponseEntity<Map<String, Any>> {
        val success = systemBackupService.deleteBackupPolicy(id)
        
        return if (success) {
            ResponseEntity.ok(mapOf(
                "message" to "备份策略已删除",
                "id" to id
            ))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/policies/{id}/toggle")
    @Operation(summary = "启用或禁用备份策略", description = "启用或禁用指定的备份策略")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun toggleBackupPolicy(
        @Parameter(description = "策略ID", required = true)
        @PathVariable id: String,
        @Parameter(description = "是否启用", example = "true")
        @RequestParam enabled: Boolean
    ): ResponseEntity<BackupPolicyResponse> {
        val policy = systemBackupService.enableBackupPolicy(id, enabled)
            ?: return ResponseEntity.notFound().build()
            
        return ResponseEntity.ok(BackupPolicyResponse.fromDomain(policy))
    }

    @PostMapping("/policies/{id}/trigger")
    @Operation(summary = "手动触发备份策略", description = "手动触发执行指定的备份策略")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    fun triggerBackupPolicy(
        @Parameter(description = "策略ID", required = true)
        @PathVariable id: String,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<SystemBackupResponse> {
        val userId = userDetails?.username
        
        val backup = systemBackupService.triggerBackupPolicy(id, userId)
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SystemBackupResponse.fromDomain(backup))
    }

    @GetMapping("/timerange")
    @Operation(summary = "根据时间范围查询备份", description = "根据指定的时间范围查询备份")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'SYSTEM_VIEWER')")
    fun getBackupsByTimeRange(
        @Parameter(description = "开始时间（格式：yyyy-MM-ddTHH:mm:ss）", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime,
        @Parameter(description = "结束时间（格式：yyyy-MM-ddTHH:mm:ss）", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime,
        @Parameter(description = "页码（从0开始）", example = "0")
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @Parameter(description = "每页条数", example = "20")
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ResponseEntity<Page<SystemBackupResponse>> {
        val pageable = PageRequest.of(page, size)
        val backups = systemBackupService.searchBackups(
            startTime = startTime,
            endTime = endTime,
            pageable = pageable
        ).map { SystemBackupResponse.fromDomain(it) }
        
        return ResponseEntity.ok(backups)
    }
} 