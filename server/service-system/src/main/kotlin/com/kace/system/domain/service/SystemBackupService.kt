package com.kace.system.domain.service

import com.kace.system.domain.model.BackupPolicy
import com.kace.system.domain.model.SystemBackup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.InputStream
import java.time.LocalDateTime

/**
 * 系统备份服务接口
 * 提供系统备份相关的功能
 */
interface SystemBackupService {

    /**
     * 创建备份
     *
     * @param name 备份名称
     * @param description 备份描述
     * @param type 备份类型
     * @param serviceType 备份服务类型
     * @param serviceName 备份的具体服务名称
     * @param userId 创建备份的用户ID
     * @param compress 是否压缩
     * @param encrypt 是否加密
     * @param encryptionAlgorithm 加密算法（如果需要加密）
     * @param retentionDays 保留天数
     * @param parameters 额外参数
     * @return 创建的备份对象
     */
    fun createBackup(
        name: String,
        description: String? = null,
        type: String,
        serviceType: String,
        serviceName: String,
        userId: String? = null,
        compress: Boolean = true,
        encrypt: Boolean = false,
        encryptionAlgorithm: String? = null,
        retentionDays: Int = 30,
        parameters: Map<String, Any>? = null
    ): SystemBackup

    /**
     * 根据ID获取备份
     *
     * @param id 备份ID
     * @return 备份对象，如不存在则返回null
     */
    fun getBackupById(id: String): SystemBackup?

    /**
     * 分页查询所有备份
     *
     * @param pageable 分页参数
     * @return 分页备份结果
     */
    fun getAllBackups(pageable: Pageable): Page<SystemBackup>

    /**
     * 根据服务类型分页查询备份
     *
     * @param serviceType 备份服务类型
     * @param pageable 分页参数
     * @return 分页备份结果
     */
    fun getBackupsByServiceType(serviceType: String, pageable: Pageable): Page<SystemBackup>

    /**
     * 根据状态分页查询备份
     *
     * @param status 备份状态
     * @param pageable 分页参数
     * @return 分页备份结果
     */
    fun getBackupsByStatus(status: String, pageable: Pageable): Page<SystemBackup>

    /**
     * 高级搜索备份
     *
     * @param type 备份类型，可选
     * @param serviceType 备份服务类型，可选
     * @param serviceName 备份的具体服务名称，可选
     * @param status 备份状态，可选
     * @param createdBy 创建者ID，可选
     * @param startTime 开始时间，可选
     * @param endTime 结束时间，可选
     * @param pageable 分页参数
     * @return 分页备份结果
     */
    fun searchBackups(
        type: String? = null,
        serviceType: String? = null,
        serviceName: String? = null,
        status: String? = null,
        createdBy: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        pageable: Pageable
    ): Page<SystemBackup>

    /**
     * 删除备份
     *
     * @param id 备份ID
     * @param deleteFiles 是否同时删除备份文件
     * @return 是否成功删除
     */
    fun deleteBackup(id: String, deleteFiles: Boolean = true): Boolean

    /**
     * 清理过期备份
     *
     * @return 清理的备份数量
     */
    fun cleanExpiredBackups(): Int

    /**
     * 获取备份文件流
     *
     * @param id 备份ID
     * @return 备份文件流，如不存在则返回null
     */
    fun getBackupFileStream(id: String): InputStream?

    /**
     * 恢复备份
     *
     * @param id 备份ID
     * @param userId 执行恢复的用户ID
     * @return 是否成功开始恢复过程
     */
    fun restoreBackup(id: String, userId: String?): Boolean

    /**
     * 获取备份详情，包括备份过程的详细日志
     *
     * @param id 备份ID
     * @return 备份详情，如不存在则返回null
     */
    fun getBackupDetails(id: String): Map<String, Any>?

    /**
     * 获取系统所有支持的备份服务
     *
     * @return 支持的备份服务列表
     */
    fun getSupportedBackupServices(): List<Map<String, Any>>

    /**
     * 创建备份策略
     *
     * @param backupPolicy 备份策略对象
     * @return 创建的备份策略
     */
    fun createBackupPolicy(backupPolicy: BackupPolicy): BackupPolicy

    /**
     * 更新备份策略
     *
     * @param backupPolicy 备份策略对象
     * @return 更新后的备份策略
     */
    fun updateBackupPolicy(backupPolicy: BackupPolicy): BackupPolicy

    /**
     * 根据ID获取备份策略
     *
     * @param id 策略ID
     * @return 备份策略对象，如不存在则返回null
     */
    fun getBackupPolicyById(id: String): BackupPolicy?

    /**
     * 删除备份策略
     *
     * @param id 策略ID
     * @return 是否成功删除
     */
    fun deleteBackupPolicy(id: String): Boolean

    /**
     * 获取所有备份策略
     *
     * @param pageable 分页参数
     * @return 备份策略分页结果
     */
    fun getAllBackupPolicies(pageable: Pageable): Page<BackupPolicy>

    /**
     * 启用或禁用备份策略
     *
     * @param id 策略ID
     * @param enabled 是否启用
     * @return 更新后的备份策略
     */
    fun enableBackupPolicy(id: String, enabled: Boolean): BackupPolicy

    /**
     * 手动触发备份策略执行
     *
     * @param id 策略ID
     * @param userId 触发备份的用户ID
     * @return 创建的备份对象
     */
    fun triggerBackupPolicy(id: String, userId: String?): SystemBackup
} 