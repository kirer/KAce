package com.kace.system.infrastructure.persistence.repository

import com.kace.system.infrastructure.persistence.entity.SystemLogs
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * 系统日志的Spring Data JPA仓库接口
 */
@Repository
interface JpaSystemLogRepository : JpaRepository<SystemLogs, String> {

    /**
     * 根据日志类型分页查询
     */
    fun findByType(type: String, pageable: Pageable): Page<SystemLogs>

    /**
     * 根据模块名称分页查询
     */
    fun findByModule(module: String, pageable: Pageable): Page<SystemLogs>

    /**
     * 根据用户ID分页查询
     */
    fun findByUserId(userId: String, pageable: Pageable): Page<SystemLogs>

    /**
     * 根据时间范围分页查询
     */
    fun findByCreatedAtBetween(startTime: LocalDateTime, endTime: LocalDateTime, pageable: Pageable): Page<SystemLogs>

    /**
     * 删除指定时间之前的日志
     */
    @Modifying
    @Query("DELETE FROM SystemLogs s WHERE s.createdAt < :before")
    fun deleteByCreatedAtBefore(@Param("before") before: LocalDateTime): Int

    /**
     * 高级搜索系统日志
     */
    @Query("""
        SELECT s FROM SystemLogs s 
        WHERE (:type IS NULL OR s.type = :type)
        AND (:module IS NULL OR s.module = :module)
        AND (:operation IS NULL OR s.operation LIKE %:operation%)
        AND (:content IS NULL OR s.content LIKE %:content%)
        AND (:userId IS NULL OR s.userId = :userId)
        AND (:startTime IS NULL OR s.createdAt >= :startTime)
        AND (:endTime IS NULL OR s.createdAt <= :endTime)
    """)
    fun search(
        @Param("type") type: String?,
        @Param("module") module: String?,
        @Param("operation") operation: String?,
        @Param("content") content: String?,
        @Param("userId") userId: String?,
        @Param("startTime") startTime: LocalDateTime?,
        @Param("endTime") endTime: LocalDateTime?,
        pageable: Pageable
    ): Page<SystemLogs>

    /**
     * 按日志类型统计日志数量
     */
    @Query("""
        SELECT s.type, COUNT(s.id) FROM SystemLogs s 
        WHERE s.createdAt BETWEEN :startTime AND :endTime 
        GROUP BY s.type
    """)
    fun countByTypeWithinTimeRange(
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<Array<Any>>

    /**
     * 按模块统计日志数量
     */
    @Query("""
        SELECT s.module, COUNT(s.id) FROM SystemLogs s 
        WHERE s.createdAt BETWEEN :startTime AND :endTime 
        GROUP BY s.module
    """)
    fun countByModuleWithinTimeRange(
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<Array<Any>>
} 