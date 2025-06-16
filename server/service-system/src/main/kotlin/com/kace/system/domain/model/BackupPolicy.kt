package com.kace.system.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 备份策略领域模型
 * 定义系统备份的执行策略
 */
data class BackupPolicy(
    /**
     * 策略ID
     */
    val id: String,
    
    /**
     * 策略名称
     */
    val name: String,
    
    /**
     * 策略描述
     */
    val description: String?,
    
    /**
     * 备份类型：FULL（全量备份）、INCREMENTAL（增量备份）、DIFFERENTIAL（差异备份）
     */
    val backupType: String,
    
    /**
     * 备份服务类型：DATABASE（数据库备份）、FILES（文件备份）、CONFIGURATION（配置备份）
     */
    val serviceType: String,
    
    /**
     * 备份的具体服务名称（如：MySQL、Redis、MongoDB等）
     */
    val serviceName: String,
    
    /**
     * 备份计划表达式（Cron表达式）
     */
    val schedule: String,
    
    /**
     * 备份保留天数
     */
    val retentionDays: Int,
    
    /**
     * 最大保留数量，超过此数量时，将删除最旧的备份
     */
    val maxBackups: Int?,
    
    /**
     * 备份存储路径模板，支持变量如 {date}, {service}, {type} 等
     */
    val storagePathTemplate: String,
    
    /**
     * 是否启用备份策略
     */
    val enabled: Boolean,
    
    /**
     * 是否在备份时压缩
     */
    val compress: Boolean,
    
    /**
     * 是否在备份时加密
     */
    val encrypt: Boolean,
    
    /**
     * 加密算法（如果需要加密）
     */
    val encryptionAlgorithm: String?,
    
    /**
     * 备份前执行的命令或脚本
     */
    val preBackupCommand: String?,
    
    /**
     * 备份后执行的命令或脚本
     */
    val postBackupCommand: String?,
    
    /**
     * 备份窗口开始时间（每天的时间点）
     */
    val backupWindowStart: LocalTime?,
    
    /**
     * 备份窗口结束时间（每天的时间点）
     */
    val backupWindowEnd: LocalTime?,
    
    /**
     * 最后执行时间
     */
    val lastExecuted: LocalDateTime?,
    
    /**
     * 下次计划执行时间
     */
    val nextScheduled: LocalDateTime?,
    
    /**
     * 创建者ID
     */
    val createdBy: String,
    
    /**
     * 创建时间
     */
    val createdAt: LocalDateTime,
    
    /**
     * 更新时间
     */
    val updatedAt: LocalDateTime,
    
    /**
     * 额外参数
     */
    val parameters: Map<String, Any>?
) {
    companion object {
        // 备份类型常量
        const val TYPE_FULL = SystemBackup.TYPE_FULL
        const val TYPE_INCREMENTAL = SystemBackup.TYPE_INCREMENTAL
        const val TYPE_DIFFERENTIAL = SystemBackup.TYPE_DIFFERENTIAL
        
        // 服务类型常量
        const val SERVICE_TYPE_DATABASE = SystemBackup.SERVICE_TYPE_DATABASE
        const val SERVICE_TYPE_FILES = SystemBackup.SERVICE_TYPE_FILES
        const val SERVICE_TYPE_CONFIGURATION = SystemBackup.SERVICE_TYPE_CONFIGURATION
    }
    
    /**
     * 检查当前是否在备份窗口内
     */
    fun isInBackupWindow(): Boolean {
        val now = LocalTime.now()
        return if (backupWindowStart != null && backupWindowEnd != null) {
            if (backupWindowStart.isBefore(backupWindowEnd)) {
                // 正常情况：开始时间在结束时间之前
                now.isAfter(backupWindowStart) && now.isBefore(backupWindowEnd)
            } else {
                // 跨日情况：开始时间在结束时间之后（如晚上22:00到次日凌晨4:00）
                now.isAfter(backupWindowStart) || now.isBefore(backupWindowEnd)
            }
        } else {
            // 如果未设置备份窗口，则认为总是在备份窗口内
            true
        }
    }
} 