package com.kace.system.domain.repository

import com.kace.system.domain.model.BackupPolicy
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

/**
 * 备份策略仓库接口
 * 提供备份策略的存储和查询功能
 */
interface BackupPolicyRepository {

    /**
     * 保存备份策略
     *
     * @param backupPolicy 备份策略对象
     * @return 保存后的备份策略对象
     */
    fun save(backupPolicy: BackupPolicy): BackupPolicy

    /**
     * 根据ID查询备份策略
     *
     * @param id 策略ID
     * @return 备份策略对象，如不存在则返回null
     */
    fun findById(id: String): BackupPolicy?

    /**
     * 分页查询所有备份策略
     *
     * @param pageable 分页参数
     * @return 分页备份策略结果
     */
    fun findAll(pageable: Pageable): Page<BackupPolicy>

    /**
     * 根据服务类型查询备份策略
     *
     * @param serviceType 服务类型
     * @param pageable 分页参数
     * @return 分页备份策略结果
     */
    fun findByServiceType(serviceType: String, pageable: Pageable): Page<BackupPolicy>

    /**
     * 根据服务名称查询备份策略
     *
     * @param serviceName 服务名称
     * @param pageable 分页参数
     * @return 分页备份策略结果
     */
    fun findByServiceName(serviceName: String, pageable: Pageable): Page<BackupPolicy>

    /**
     * 查询已启用的备份策略
     *
     * @param pageable 分页参数
     * @return 分页备份策略结果
     */
    fun findByEnabled(enabled: Boolean, pageable: Pageable): Page<BackupPolicy>

    /**
     * 高级搜索备份策略
     *
     * @param backupType 备份类型，可选
     * @param serviceType 服务类型，可选
     * @param serviceName 服务名称，可选
     * @param enabled 是否启用，可选
     * @param createdBy 创建者ID，可选
     * @param startTime 开始时间，可选
     * @param endTime 结束时间，可选
     * @param pageable 分页参数
     * @return 分页备份策略结果
     */
    fun search(
        backupType: String? = null,
        serviceType: String? = null,
        serviceName: String? = null,
        enabled: Boolean? = null,
        createdBy: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        pageable: Pageable
    ): Page<BackupPolicy>

    /**
     * 删除备份策略
     *
     * @param id 策略ID
     * @return 是否成功删除
     */
    fun deleteById(id: String): Boolean

    /**
     * 启用或禁用备份策略
     *
     * @param id 策略ID
     * @param enabled 是否启用
     * @return 更新后的备份策略
     */
    fun setEnabled(id: String, enabled: Boolean): BackupPolicy?

    /**
     * 更新备份执行信息
     *
     * @param id 策略ID
     * @param lastExecuted 最后执行时间
     * @param nextScheduled 下次计划执行时间
     * @return 更新后的备份策略
     */
    fun updateExecutionInfo(id: String, lastExecuted: LocalDateTime, nextScheduled: LocalDateTime?): BackupPolicy?

    /**
     * 查找到期执行的备份策略
     *
     * @param now 当前时间
     * @return 需要执行的备份策略列表
     */
    fun findPoliciesDueForExecution(now: LocalDateTime): List<BackupPolicy>
} 