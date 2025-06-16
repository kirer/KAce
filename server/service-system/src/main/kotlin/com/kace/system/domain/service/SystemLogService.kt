package com.kace.system.domain.service

import com.kace.system.domain.model.SystemLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

/**
 * 系统日志服务接口
 * 提供系统日志的查询、创建、分析等功能
 */
interface SystemLogService {

    /**
     * 记录系统日志
     *
     * @param type 日志类型（如：INFO, WARNING, ERROR, DEBUG）
     * @param module 模块名称
     * @param operation 操作描述
     * @param content 日志内容
     * @param userId 操作用户ID，可选
     * @return 创建的系统日志
     */
    fun logEvent(type: String, module: String, operation: String, content: String, userId: String? = null): SystemLog

    /**
     * 根据ID获取日志
     *
     * @param id 日志ID
     * @return 系统日志对象，如不存在则返回null
     */
    fun getLogById(id: String): SystemLog?

    /**
     * 分页查询系统日志
     *
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun getLogs(pageable: Pageable): Page<SystemLog>

    /**
     * 根据类型分页查询系统日志
     *
     * @param type 日志类型
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun getLogsByType(type: String, pageable: Pageable): Page<SystemLog>

    /**
     * 根据模块分页查询系统日志
     *
     * @param module 模块名称
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun getLogsByModule(module: String, pageable: Pageable): Page<SystemLog>

    /**
     * 根据时间范围查询系统日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun getLogsByTimeRange(startTime: LocalDateTime, endTime: LocalDateTime, pageable: Pageable): Page<SystemLog>

    /**
     * 根据用户ID查询系统日志
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    fun getLogsByUserId(userId: String, pageable: Pageable): Page<SystemLog>

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
    fun searchLogs(
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
     * 清理指定时间之前的日志
     *
     * @param before 清理此时间之前的日志
     * @return 清理的日志数量
     */
    fun cleanLogsBefore(before: LocalDateTime): Int
} 