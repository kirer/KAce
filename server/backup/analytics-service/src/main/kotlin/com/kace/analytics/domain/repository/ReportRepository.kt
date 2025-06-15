package com.kace.analytics.domain.repository

import com.kace.analytics.domain.model.Report
import java.time.Instant
import java.util.UUID

/**
 * 报表仓库接口
 */
interface ReportRepository {
    
    /**
     * 保存报表
     */
    suspend fun save(report: Report): Report
    
    /**
     * 根据ID查找报表
     */
    suspend fun findById(id: UUID): Report?
    
    /**
     * 根据名称查找报表
     */
    suspend fun findByName(name: String): Report?
    
    /**
     * 查找所有报表
     */
    suspend fun findAll(page: Int, size: Int): List<Report>
    
    /**
     * 根据创建者查找报表
     */
    suspend fun findByCreatedBy(userId: UUID, page: Int, size: Int): List<Report>
    
    /**
     * 查找计划执行的报表
     */
    suspend fun findScheduledReports(): List<Report>
    
    /**
     * 根据创建时间范围查找报表
     */
    suspend fun findByCreatedAtRange(startTime: Instant, endTime: Instant, page: Int, size: Int): List<Report>
    
    /**
     * 统计报表数量
     */
    suspend fun count(): Long
    
    /**
     * 统计用户报表数量
     */
    suspend fun countByUserId(userId: UUID): Long
    
    /**
     * 删除报表
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 更新报表的最后执行时间
     */
    suspend fun updateLastExecutionTime(id: UUID, executionTime: Instant): Boolean
} 