package com.kace.system.infrastructure.persistence.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.kace.system.domain.model.SystemLog
import com.kace.system.domain.repository.SystemLogRepository
import com.kace.system.infrastructure.persistence.entity.SystemLogs
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 系统日志仓库实现
 */
@Repository
class SystemLogRepositoryImpl(
    private val jpaSystemLogRepository: JpaSystemLogRepository,
    private val objectMapper: ObjectMapper
) : SystemLogRepository {

    /**
     * 将领域模型转换为实体
     */
    private fun toEntity(systemLog: SystemLog): SystemLogs {
        val extraParamsJson = if (systemLog.extraParams != null) {
            objectMapper.writeValueAsString(systemLog.extraParams)
        } else {
            null
        }
        
        return SystemLogs(
            id = systemLog.id,
            type = systemLog.type,
            module = systemLog.module,
            operation = systemLog.operation,
            content = systemLog.content,
            userId = systemLog.userId,
            clientIp = systemLog.clientIp,
            executionTime = systemLog.executionTime,
            status = systemLog.status,
            extraParams = extraParamsJson,
            createdAt = systemLog.createdAt
        )
    }

    /**
     * 将实体转换为领域模型
     */
    private fun toDomainModel(systemLogs: SystemLogs): SystemLog {
        val extraParams = if (systemLogs.extraParams != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                objectMapper.readValue(systemLogs.extraParams, Map::class.java) as Map<String, Any>
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
        
        return SystemLog(
            id = systemLogs.id,
            type = systemLogs.type,
            module = systemLogs.module,
            operation = systemLogs.operation,
            content = systemLogs.content,
            userId = systemLogs.userId,
            clientIp = systemLogs.clientIp,
            executionTime = systemLogs.executionTime,
            status = systemLogs.status,
            createdAt = systemLogs.createdAt,
            extraParams = extraParams
        )
    }

    /**
     * 保存系统日志
     */
    override fun save(systemLog: SystemLog): SystemLog {
        val entity = toEntity(systemLog)
        val savedEntity = jpaSystemLogRepository.save(entity)
        return toDomainModel(savedEntity)
    }

    /**
     * 根据ID查询系统日志
     */
    override fun findById(id: String): SystemLog? {
        return jpaSystemLogRepository.findById(id)
            .map { toDomainModel(it) }
            .orElse(null)
    }

    /**
     * 分页查询所有系统日志
     */
    override fun findAll(pageable: Pageable): Page<SystemLog> {
        return jpaSystemLogRepository.findAll(pageable)
            .map { toDomainModel(it) }
    }

    /**
     * 根据日志类型分页查询
     */
    override fun findByType(type: String, pageable: Pageable): Page<SystemLog> {
        return jpaSystemLogRepository.findByType(type, pageable)
            .map { toDomainModel(it) }
    }

    /**
     * 根据模块名称分页查询
     */
    override fun findByModule(module: String, pageable: Pageable): Page<SystemLog> {
        return jpaSystemLogRepository.findByModule(module, pageable)
            .map { toDomainModel(it) }
    }

    /**
     * 根据操作用户ID分页查询
     */
    override fun findByUserId(userId: String, pageable: Pageable): Page<SystemLog> {
        return jpaSystemLogRepository.findByUserId(userId, pageable)
            .map { toDomainModel(it) }
    }

    /**
     * 根据时间范围分页查询
     */
    override fun findByTimeRange(startTime: LocalDateTime, endTime: LocalDateTime, pageable: Pageable): Page<SystemLog> {
        return jpaSystemLogRepository.findByCreatedAtBetween(startTime, endTime, pageable)
            .map { toDomainModel(it) }
    }

    /**
     * 高级搜索系统日志
     */
    override fun search(
        type: String?,
        module: String?,
        operation: String?,
        content: String?,
        userId: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        pageable: Pageable
    ): Page<SystemLog> {
        return jpaSystemLogRepository.search(type, module, operation, content, userId, startTime, endTime, pageable)
            .map { toDomainModel(it) }
    }

    /**
     * 删除指定时间之前的日志
     */
    override fun deleteLogsBefore(before: LocalDateTime): Int {
        return jpaSystemLogRepository.deleteByCreatedAtBefore(before)
    }

    /**
     * 批量保存系统日志
     */
    override fun saveAll(logs: List<SystemLog>): List<SystemLog> {
        val entities = logs.map { toEntity(it) }
        val savedEntities = jpaSystemLogRepository.saveAll(entities)
        return savedEntities.map { toDomainModel(it) }
    }

    /**
     * 按日志类型统计日志数量
     */
    override fun countByType(startTime: LocalDateTime, endTime: LocalDateTime): Map<String, Long> {
        val results = jpaSystemLogRepository.countByTypeWithinTimeRange(startTime, endTime)
        return results.associate { row ->
            val type = row[0] as String
            val count = (row[1] as Number).toLong()
            type to count
        }
    }

    /**
     * 按模块统计日志数量
     */
    override fun countByModule(startTime: LocalDateTime, endTime: LocalDateTime): Map<String, Long> {
        val results = jpaSystemLogRepository.countByModuleWithinTimeRange(startTime, endTime)
        return results.associate { row ->
            val module = row[0] as String
            val count = (row[1] as Number).toLong()
            module to count
        }
    }
} 