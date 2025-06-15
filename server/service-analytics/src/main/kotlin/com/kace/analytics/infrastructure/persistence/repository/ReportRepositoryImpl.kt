package com.kace.analytics.infrastructure.persistence.repository

import com.kace.analytics.domain.model.Report
import com.kace.analytics.domain.repository.ReportRepository
import com.kace.analytics.infrastructure.persistence.entity.ReportEntity
import com.kace.analytics.infrastructure.persistence.entity.Reports
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

/**
 * 报表仓库实现类
 */
class ReportRepositoryImpl : ReportRepository {

    override suspend fun save(report: Report): Report = withContext(Dispatchers.IO) {
        transaction {
            val entity = if (report.id != null && ReportEntity.findById(report.id) != null) {
                // Update existing
                val existingEntity = ReportEntity.findById(report.id)!!
                existingEntity.apply {
                    name = report.name
                    description = report.description
                    parameters = report.parameters
                    schedule = report.schedule
                    createdBy = report.createdBy.toString()
                    updatedAt = Instant.now()
                }
            } else {
                // Create new
                ReportEntity.new(report.id ?: UUID.randomUUID()) {
                    name = report.name
                    description = report.description
                    parameters = report.parameters
                    schedule = report.schedule
                    createdBy = report.createdBy.toString()
                    createdAt = report.createdAt ?: Instant.now()
                    updatedAt = report.updatedAt ?: Instant.now()
                }
            }
            entity.toReport()
        }
    }

    override suspend fun update(report: Report): Report = withContext(Dispatchers.IO) {
        transaction {
            val entity = ReportEntity.findById(report.id) ?: throw IllegalArgumentException("Report not found with id: ${report.id}")
            entity.apply {
                name = report.name
                description = report.description
                parameters = report.parameters
                schedule = report.schedule
                updatedAt = Instant.now()
            }
            entity.toReport()
        }
    }

    override suspend fun findById(id: UUID): Report? = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.findById(id)?.toReport()
        }
    }

    override suspend fun findByName(name: String): Report? = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.find { Reports.name eq name }
                .firstOrNull()
                ?.toReport()
        }
    }

    override suspend fun findAll(page: Int, size: Int): List<Report> = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.all()
                .orderBy(Reports.createdAt to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toReport() }
        }
    }

    override suspend fun findByUserId(userId: UUID, page: Int, size: Int): List<Report> = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.find { Reports.createdBy eq userId.toString() }
                .orderBy(Reports.createdAt to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toReport() }
        }
    }

    override suspend fun findScheduled(): List<Report> = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.find { Reports.schedule.isNotNull() }
                .orderBy(Reports.createdAt to SortOrder.DESC)
                .map { it.toReport() }
        }
    }

    override suspend fun findByCreatedAtRange(startTime: Instant, endTime: Instant, page: Int, size: Int): List<Report> = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.find { Reports.createdAt.between(startTime, endTime) }
                .orderBy(Reports.createdAt to SortOrder.DESC)
                .limit(size, ((page - 1) * size).toLong())
                .map { it.toReport() }
        }
    }

    override suspend fun count(): Long = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.count()
        }
    }

    override suspend fun countByUserId(userId: UUID): Long = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.find { Reports.createdBy eq userId.toString() }.count()
        }
    }

    override suspend fun delete(id: UUID): Boolean = withContext(Dispatchers.IO) {
        transaction {
            ReportEntity.findById(id)?.let {
                it.delete()
                true
            } ?: false
        }
    }
} 