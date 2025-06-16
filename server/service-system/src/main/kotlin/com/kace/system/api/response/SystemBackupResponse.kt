package com.kace.system.api.response

import com.kace.system.domain.model.SystemBackup
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 系统备份响应模型
 */
data class SystemBackupResponse(
    @Schema(description = "备份ID")
    val id: String,

    @Schema(description = "备份名称")
    val name: String,

    @Schema(description = "备份描述")
    val description: String?,

    @Schema(description = "备份类型")
    val type: String,

    @Schema(description = "备份服务类型")
    val serviceType: String,

    @Schema(description = "备份的具体服务名称")
    val serviceName: String,

    @Schema(description = "备份文件存储路径")
    val filePath: String,

    @Schema(description = "备份文件大小（字节）")
    val fileSize: Long,

    @Schema(description = "备份文件SHA256校验和")
    val checksum: String?,

    @Schema(description = "备份状态")
    val status: String,

    @Schema(description = "状态消息")
    val statusMessage: String?,

    @Schema(description = "执行备份的用户ID")
    val createdBy: String?,

    @Schema(description = "备份创建时间")
    val createdAt: LocalDateTime,

    @Schema(description = "备份完成时间")
    val completedAt: LocalDateTime?,

    @Schema(description = "备份过期时间")
    val expiresAt: LocalDateTime?,

    @Schema(description = "是否已加密")
    val encrypted: Boolean,

    @Schema(description = "加密算法")
    val encryptionAlgorithm: String?,

    @Schema(description = "备份大小压缩比")
    val compressionRatio: Double?
) {
    companion object {
        /**
         * 从领域模型创建响应模型
         */
        fun fromDomain(systemBackup: SystemBackup): SystemBackupResponse {
            return SystemBackupResponse(
                id = systemBackup.id,
                name = systemBackup.name,
                description = systemBackup.description,
                type = systemBackup.type,
                serviceType = systemBackup.serviceType,
                serviceName = systemBackup.serviceName,
                filePath = systemBackup.filePath,
                fileSize = systemBackup.fileSize,
                checksum = systemBackup.checksum,
                status = systemBackup.status,
                statusMessage = systemBackup.statusMessage,
                createdBy = systemBackup.createdBy,
                createdAt = systemBackup.createdAt,
                completedAt = systemBackup.completedAt,
                expiresAt = systemBackup.expiresAt,
                encrypted = systemBackup.encrypted,
                encryptionAlgorithm = systemBackup.encryptionAlgorithm,
                compressionRatio = systemBackup.compressionRatio
            )
        }
    }
} 