package com.kace.system.domain.service.impl

import com.kace.system.domain.model.SystemLog
import com.kace.system.domain.repository.SystemLogRepository
import com.kace.system.domain.service.SystemLogService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

/**
 * 系统日志服务实现
 */
@Service
class SystemLogServiceImpl(
    private val systemLogRepository: SystemLogRepository
) : SystemLogService {

    private val logger = LoggerFactory.getLogger(javaClass)

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
    @Transactional
    override fun logEvent(type: String, module: String, operation: String, content: String, userId: String?): SystemLog {
        logger.debug("记录系统日志: 类型=$type, 模块=$module, 操作=$operation, 用户ID=$userId")
        
        val systemLog = SystemLog(
            id = UUID.randomUUID().toString(),
            type = type,
            module = module,
            operation = operation,
            content = content,
            userId = userId,
            clientIp = null, // 此处可以从当前请求上下文获取，此实现中暂不处理
            executionTime = null,
            status = SystemLog.STATUS_SUCCESS,
            createdAt = LocalDateTime.now()
        )
        
        return systemLogRepository.save(systemLog)
    }

    /**
     * 根据ID获取日志
     *
     * @param id 日志ID
     * @return 系统日志对象，如不存在则返回null
     */
    @Transactional(readOnly = true)
    override fun getLogById(id: String): SystemLog? {
        logger.debug("根据ID获取系统日志: $id")
        return systemLogRepository.findById(id)
    }

    /**
     * 分页查询系统日志
     *
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    @Transactional(readOnly = true)
    override fun getLogs(pageable: Pageable): Page<SystemLog> {
        logger.debug("分页查询系统日志: page=${pageable.pageNumber}, size=${pageable.pageSize}")
        return systemLogRepository.findAll(pageable)
    }

    /**
     * 根据类型分页查询系统日志
     *
     * @param type 日志类型
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    @Transactional(readOnly = true)
    override fun getLogsByType(type: String, pageable: Pageable): Page<SystemLog> {
        logger.debug("根据类型查询系统日志: type=$type, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        return systemLogRepository.findByType(type, pageable)
    }

    /**
     * 根据模块分页查询系统日志
     *
     * @param module 模块名称
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    @Transactional(readOnly = true)
    override fun getLogsByModule(module: String, pageable: Pageable): Page<SystemLog> {
        logger.debug("根据模块查询系统日志: module=$module, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        return systemLogRepository.findByModule(module, pageable)
    }

    /**
     * 根据时间范围查询系统日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    @Transactional(readOnly = true)
    override fun getLogsByTimeRange(startTime: LocalDateTime, endTime: LocalDateTime, pageable: Pageable): Page<SystemLog> {
        logger.debug("根据时间范围查询系统日志: startTime=$startTime, endTime=$endTime, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        return systemLogRepository.findByTimeRange(startTime, endTime, pageable)
    }

    /**
     * 根据用户ID查询系统日志
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页系统日志结果
     */
    @Transactional(readOnly = true)
    override fun getLogsByUserId(userId: String, pageable: Pageable): Page<SystemLog> {
        logger.debug("根据用户ID查询系统日志: userId=$userId, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        return systemLogRepository.findByUserId(userId, pageable)
    }

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
    @Transactional(readOnly = true)
    override fun searchLogs(
        type: String?,
        module: String?,
        operation: String?,
        content: String?,
        userId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        pageable: Pageable
    ): Page<SystemLog> {
        logger.debug("高级搜索系统日志: type=$type, module=$module, operation=$operation, content=$content, userId=$userId, startTime=$startTime, endTime=$endTime")
        return systemLogRepository.search(type, module, operation, content, userId, startTime, endTime, pageable)
    }

    /**
     * 清理指定时间之前的日志
     *
     * @param before 清理此时间之前的日志
     * @return 清理的日志数量
     */
    @Transactional
    override fun cleanLogsBefore(before: LocalDateTime): Int {
        logger.info("清理系统日志: before=$before")
        val count = systemLogRepository.deleteLogsBefore(before)
        logger.info("已清理系统日志数量: $count")
        return count
    }
} 