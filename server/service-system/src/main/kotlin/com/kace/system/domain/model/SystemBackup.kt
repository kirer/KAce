package com.kace.system.domain.model

import java.time.LocalDateTime

/**
 * 系统备份领域模型
 * 记录系统备份的元数据信息
 */
data class SystemBackup(
    /**
     * 备份ID，唯一标识
     */
    val id: String,
    
    /**
     * 备份名称
     */
    val name: String,
    
    /**
     * 备份描述
     */
    val description: String?,
    
    /**
     * 备份类型：FULL（全量备份）、INCREMENTAL（增量备份）、DIFFERENTIAL（差异备份）
     */
    val type: String,
    
    /**
     * 备份服务类型：DATABASE（数据库备份）、FILES（文件备份）、CONFIGURATION（配置备份）
     */
    val serviceType: String,
    
    /**
     * 备份的具体服务名称（如：MySQL、Redis、MongoDB等）
     */
    val serviceName: String,
    
    /**
     * 备份文件存储路径
     */
    val filePath: String,
    
    /**
     * 备份文件大小（字节）
     */
    val fileSize: Long,
    
    /**
     * 备份文件SHA256校验和
     */
    val checksum: String?,
    
    /**
     * 备份状态：PENDING（待处理）、IN_PROGRESS（处理中）、COMPLETED（已完成）、FAILED（失败）
     */
    val status: String,
    
    /**
     * 状态消息，特别是在失败时记录原因
     */
    val statusMessage: String?,
    
    /**
     * 执行备份的用户ID
     */
    val createdBy: String?,
    
    /**
     * 备份创建时间
     */
    val createdAt: LocalDateTime,
    
    /**
     * 备份完成时间
     */
    val completedAt: LocalDateTime?,
    
    /**
     * 备份过期时间，超过此时间可自动清理
     */
    val expiresAt: LocalDateTime?,
    
    /**
     * 是否已加密
     */
    val encrypted: Boolean,
    
    /**
     * 加密算法（如果已加密）
     */
    val encryptionAlgorithm: String?,
    
    /**
     * 备份大小压缩比（原始大小/压缩后大小）
     */
    val compressionRatio: Double?,
    
    /**
     * 相关参数，存储备份时的特定参数
     */
    val parameters: Map<String, Any>?
) {
    companion object {
        // 备份类型常量
        const val TYPE_FULL = "FULL"
        const val TYPE_INCREMENTAL = "INCREMENTAL"
        const val TYPE_DIFFERENTIAL = "DIFFERENTIAL"
        
        // 服务类型常量
        const val SERVICE_TYPE_DATABASE = "DATABASE"
        const val SERVICE_TYPE_FILES = "FILES"
        const val SERVICE_TYPE_CONFIGURATION = "CONFIGURATION"
        
        // 备份状态常量
        const val STATUS_PENDING = "PENDING"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_FAILED = "FAILED"
    }
    
    /**
     * 检查备份是否已完成
     */
    fun isCompleted(): Boolean {
        return status == STATUS_COMPLETED
    }
    
    /**
     * 检查备份是否失败
     */
    fun isFailed(): Boolean {
        return status == STATUS_FAILED
    }
    
    /**
     * 检查备份是否过期
     */
    fun isExpired(): Boolean {
        return expiresAt?.isBefore(LocalDateTime.now()) ?: false
    }
    
    /**
     * 检查备份是否正在进行中
     */
    fun isInProgress(): Boolean {
        return status == STATUS_PENDING || status == STATUS_IN_PROGRESS
    }
} 