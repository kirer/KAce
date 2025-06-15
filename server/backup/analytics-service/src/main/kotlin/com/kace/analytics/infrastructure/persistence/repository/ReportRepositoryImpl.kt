package com.kace.analytics.infrastructure.persistence.repository

import com.kace.analytics.domain.model.Report
import com.kace.analytics.domain.repository.ReportRepository
import com.kace.analytics.infrastructure.persistence.entity.ReportEntity
import com.kace.analytics.infrastructure.persistence.entity.Reports
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

/**
 * 报表仓库实现类
 */
class ReportRepositoryImpl : ReportRepository {

    override fun create(report: Report): Report = transaction {
        val entity = ReportEntity.new {
            name = report.name
            description = report.description
            type = report.type
            query = report.query
            parameters = report.parameters
            schedule = report.schedule
            lastRunAt = report.lastRunAt
            createdBy = report.createdBy.toString()
            createdAt = Instant.now()
            updatedAt = Instant.now()
        }
        entity.toReport()
    }

    override fun update(report: Report): Report = transaction {
        val entity = ReportEntity.findById(report.id) ?: throw IllegalArgumentException("Report not found with id: ${report.id}")
        entity.name = report.name
        entity.description = report.description
        entity.type = report.type
        entity.query = report.query
        entity.parameters = report.parameters
        entity.schedule = report.schedule
        entity.lastRunAt = report.lastRunAt
        entity.updatedAt = Instant.now()
        entity.toReport()
    }

    override fun findById(id: UUID): Report? = transaction {
        ReportEntity.findById(id)?.toReport()
    }

    override fun findByName(name: String): List<Report> = transaction {
        ReportEntity.find { Reports.name eq name }
            .orderBy(Reports.createdAt to SortOrder.DESC)
            .map { it.toReport() }
    }

    override fun findByType(type: String, limit: Int, offset: Int): List<Report> = transaction {
        ReportEntity.find { Reports.type eq type }
            .orderBy(Reports.createdAt to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toReport() }
    }

    override fun findByCreatedBy(userId: UUID, limit: Int, offset: Int): List<Report> = transaction {
        ReportEntity.find { Reports.createdBy eq userId.toString() }
            .orderBy(Reports.createdAt to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toReport() }
    }

    override fun findScheduledReports(): List<Report> = transaction {
        ReportEntity.find { Reports.schedule.isNotNull() }
            .orderBy(Reports.createdAt to SortOrder.DESC)
            .map { it.toReport() }
    }

    override fun findAll(limit: Int, offset: Int): List<Report> = transaction {
        ReportEntity.all()
            .orderBy(Reports.createdAt to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toReport() }
    }

    override fun updateLastRunTime(id: UUID, runTime: Instant): Boolean = transaction {
        ReportEntity.findById(id)?.let {
            it.lastRunAt = runTime
            it.updatedAt = Instant.now()
            true
        } ?: false
    }

    override fun deleteById(id: UUID): Boolean = transaction {
        ReportEntity.findById(id)?.let {
            it.delete()
            true
        } ?: false
    }

    override fun countByType(type: String): Long = transaction {
        ReportEntity.find { Reports.type eq type }.count()
    }
} 