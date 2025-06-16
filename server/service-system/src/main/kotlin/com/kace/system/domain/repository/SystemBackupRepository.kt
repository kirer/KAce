package com.kace.system.domain.repository

import com.kace.system.domain.model.SystemBackup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.io.InputStream
import java.time.LocalDateTime

/**
 * 系统备份仓库接口
 * 提供系统备份的存储和查询功能
 */
interface SystemBackupRepository {

    /**
     * 保存系统备份
     *
     * @param systemBackup 系统备份对象
     * @return 保存后的系统备份对象
     */
    fun save(systemBackup: SystemBackup): SystemBackup

    /**
     * 根据ID查询系统备份
     *
     * @param id 备份ID
     * @return 系统备份对象，如不存在则返回null
     */
    fun findById(id: String): SystemBackup?

    /**
     * 分页查询所有系统备份
     *
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun findAll(pageable: Pageable): Page<SystemBackup>

    /**
     * 根据备份类型查询
     *
     * @param type 备份类型
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun findByType(type: String, pageable: Pageable): Page<SystemBackup>

    /**
     * 根据服务类型查询
     *
     * @param serviceType 服务类型
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun findByServiceType(serviceType: String, pageable: Pageable): Page<SystemBackup>

    /**
     * 根据服务名称查询
     *
     * @param serviceName 服务名称
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun findByServiceName(serviceName: String, pageable: Pageable): Page<SystemBackup>

    /**
     * 根据备份状态查询
     *
     * @param status 备份状态
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun findByStatus(status: String, pageable: Pageable): Page<SystemBackup>

    /**
     * 根据创建者查询
     *
     * @param createdBy 创建者ID
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun findByCreatedBy(createdBy: String, pageable: Pageable): Page<SystemBackup>

    /**
     * 高级搜索系统备份
     *
     * @param type 备份类型，可选
     * @param serviceType 服务类型，可选
     * @param serviceName 服务名称，可选
     * @param status 备份状态，可选
     * @param createdBy 创建者ID，可选
     * @param startTime 开始时间，可选
     * @param endTime 结束时间，可选
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun search(
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
     * 删除系统备份
     *
     * @param id 备份ID
     * @return 是否成功删除
     */
    fun deleteById(id: String): Boolean

    /**
     * 查找过期的备份
     *
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 分页系统备份结果
     */
    fun findExpiredBackups(now: LocalDateTime, pageable: Pageable): Page<SystemBackup>

    /**
     * 批量删除备份
     *
     * @param ids 备份ID列表
     * @return 成功删除的数量
     */
    fun deleteAllById(ids: List<String>): Int

    /**
     * 更新备份状态
     *
     * @param id 备份ID
     * @param status 新状态
     * @param statusMessage 状态消息
     * @param completedAt 完成时间，仅在状态为COMPLETED时有效
     * @return 更新后的备份对象
     */
    fun updateStatus(id: String, status: String, statusMessage: String? = null, completedAt: LocalDateTime? = null): SystemBackup?

    /**
     * 获取备份文件流
     *
     * @param id 备份ID
     * @return 备份文件流，如不存在则返回null
     */
    fun getBackupFileStream(id: String): InputStream?

    /**
     * 存储备份文件
     *
     * @param id 备份ID
     * @param inputStream 文件输入流
     * @param size 文件大小
     * @return 是否成功存储
     */
    fun storeBackupFile(id: String, inputStream: InputStream, size: Long): Boolean

    /**
     * 删除备份文件
     *
     * @param id 备份ID
     * @return 是否成功删除
     */
    fun deleteBackupFile(id: String): Boolean
} 