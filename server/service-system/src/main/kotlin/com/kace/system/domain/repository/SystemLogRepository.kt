package com.kace.system.domain.repository

import com.kace.system.domain.model.SystemLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

/**
 * 系统日志仓库接口
 * 提供系统日志的存储和查询功能
 */
interface SystemLogRepository {

    /**
     * 保存系统日志
     *
     * @param systemLog 系统日志对象
     * @return 保存后的系统日志对象（可能包含自动生成的ID）
     */
    fun save(systemLog: SystemLog): SystemLog

    /**
     * 根据ID查询系统日志
     *
     * @param id 日志ID
     * @return 系统日志对象，如未找到则返回null
     */
    fun findById(id: String): SystemLog?

    /**
     * 分页查询所有系统日志
     *
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun findAll(pageable: Pageable): Page<SystemLog>

    /**
     * 根据日志类型分页查询
     *
     * @param type 日志类型
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun findByType(type: String, pageable: Pageable): Page<SystemLog>

    /**
     * 根据模块名称分页查询
     *
     * @param module 模块名称
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun findByModule(module: String, pageable: Pageable): Page<SystemLog>

    /**
     * 根据操作用户ID分页查询
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun findByUserId(userId: String, pageable: Pageable): Page<SystemLog>

    /**
     * 根据时间范围分页查询
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun findByTimeRange(startTime: LocalDateTime, endTime: LocalDateTime, pageable: Pageable): Page<SystemLog>

    /**
     * 高级搜索系统日志
     *
     * @param type 日志类型，可选
     * @param module 模块名称，可选
     * @param operation 操作描述，可选
     * @param content 内容关键词，可选
     * @param userId 用户ID，可选
     * @param startTime 开始时间，可选
     * @param endTime 结束时间，可选
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun search(
        type: String? = null,
        module: String? = null,
        operation: String? = null,
        content: String? = null,
        userId: String? = null,
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        pageable: Pageable
    ): Page<SystemLog>

    /**
     * 删除指定时间之前的日志
     *
     * @param before 删除此时间之前的日志
     * @return 删除的日志数量
     */
    fun deleteLogsBefore(before: LocalDateTime): Int

    /**
     * 批量保存系统日志
     *
     * @param logs 系统日志列表
     * @return 保存后的系统日志列表
     */
    fun saveAll(logs: List<SystemLog>): List<SystemLog>

    /**
     * 按日志类型统计日志数量
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 类型到数量的映射
     */
    fun countByType(startTime: LocalDateTime, endTime: LocalDateTime): Map<String, Long>

    /**
     * 按模块统计日志数量
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 模块到数量的映射
     */
    fun countByModule(startTime: LocalDateTime, endTime: LocalDateTime): Map<String, Long>
} 