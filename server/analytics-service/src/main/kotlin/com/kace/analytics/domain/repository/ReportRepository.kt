package com.kace.analytics.domain.repository

import com.kace.analytics.domain.model.Report
import kotlinx.datetime.Instant

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
    suspend fun findById(id: String): Report?
    
    /**
     * 根据条件查询报表
     */
    suspend fun findAll(
        createdBy: String? = null,
        startDate: Instant? = null,
        endDate: Instant? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<Report>
    
    /**
     * 查找需要执行的计划报表
     */
    suspend fun findScheduledReports(executionTime: Instant): List<Report>
    
    /**
     * 更新报表
     */
    suspend fun update(report: Report): Report?
    
    /**
     * 删除报表
     */
    suspend fun delete(id: String): Boolean
} 