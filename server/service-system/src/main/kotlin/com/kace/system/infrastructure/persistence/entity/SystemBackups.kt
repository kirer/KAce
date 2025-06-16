package com.kace.system.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 系统备份实体类
 */
@Entity
@Table(
    name = "system_backups",
    indexes = [
        Index(name = "idx_system_backups_type", columnList = "type"),
        Index(name = "idx_system_backups_service_type", columnList = "service_type"),
        Index(name = "idx_system_backups_status", columnList = "status"),
        Index(name = "idx_system_backups_created_at", columnList = "created_at"),
        Index(name = "idx_system_backups_expires_at", columnList = "expires_at")
    ]
)
class SystemBackups(
    /**
     * 备份ID，唯一标识
     */
    @Id
    @Column(name = "id", length = 36, nullable = false)
    val id: String,

    /**
     * 备份名称
     */
    @Column(name = "name", length = 100, nullable = false)
    val name: String,

    /**
     * 备份描述
     */
    @Column(name = "description", length = 500)
    val description: String?,

    /**
     * 备份类型
     */
    @Column(name = "type", length = 20, nullable = false)
    val type: String,

    /**
     * 备份服务类型
     */
    @Column(name = "service_type", length = 20, nullable = false)
    val serviceType: String,

    /**
     * 备份的具体服务名称
     */
    @Column(name = "service_name", length = 50, nullable = false)
    val serviceName: String,

    /**
     * 备份文件存储路径
     */
    @Column(name = "file_path", length = 500, nullable = false)
    val filePath: String,

    /**
     * 备份文件大小（字节）
     */
    @Column(name = "file_size", nullable = false)
    val fileSize: Long,

    /**
     * 备份文件SHA256校验和
     */
    @Column(name = "checksum", length = 64)
    val checksum: String?,

    /**
     * 备份状态
     */
    @Column(name = "status", length = 20, nullable = false)
    val status: String,

    /**
     * 状态消息
     */
    @Column(name = "status_message", length = 500)
    val statusMessage: String?,

    /**
     * 执行备份的用户ID
     */
    @Column(name = "created_by", length = 36)
    val createdBy: String?,

    /**
     * 备份创建时间
     */
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    /**
     * 备份完成时间
     */
    @Column(name = "completed_at")
    val completedAt: LocalDateTime?,

    /**
     * 备份过期时间
     */
    @Column(name = "expires_at")
    val expiresAt: LocalDateTime?,

    /**
     * 是否已加密
     */
    @Column(name = "encrypted", nullable = false)
    val encrypted: Boolean,

    /**
     * 加密算法
     */
    @Column(name = "encryption_algorithm", length = 50)
    val encryptionAlgorithm: String?,

    /**
     * 备份大小压缩比
     */
    @Column(name = "compression_ratio")
    val compressionRatio: Double?,

    /**
     * 相关参数，存储为JSON
     */
    @Column(name = "parameters", columnDefinition = "TEXT")
    @Lob
    val parameters: String?
) 