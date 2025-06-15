package com.kace.content.infrastructure.persistence.repository

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentStatus
import com.kace.content.domain.repository.ContentRepository
import com.kace.content.infrastructure.persistence.entity.ContentEntity
import com.kace.content.infrastructure.persistence.mapper.toContent
import com.kace.content.infrastructure.persistence.mapper.toEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

/**
 * 内容仓库实现
 */
class ContentRepositoryImpl(private val database: Database) : ContentRepository {
    /**
     * 创建内容
     */
    override suspend fun create(content: Content): Content = newSuspendedTransaction(db = database) {
        val entity = content.toEntity()
        entity.id
        content.copy(id = entity.id.value)
    }

    /**
     * 更新内容
     */
    override suspend fun update(content: Content): Content = newSuspendedTransaction(db = database) {
        val entity = ContentEntity.findById(content.id)
            ?: throw IllegalArgumentException("内容不存在: ${content.id}")
        
        entity.title = content.title
        entity.slug = content.slug
        entity.status = content.status.name
        entity.fields = content.fields
        entity.version = content.version
        entity.updatedAt = content.updatedAt
        entity.publishedAt = content.publishedAt
        entity.archivedAt = content.archivedAt
        
        content
    }

    /**
     * 获取内容
     */
    override suspend fun getById(id: UUID): Content? = newSuspendedTransaction(db = database) {
        ContentEntity.findById(id)?.toContent()
    }

    /**
     * 根据slug获取内容
     */
    override suspend fun getBySlug(slug: String): Content? = newSuspendedTransaction(db = database) {
        ContentEntity.find { ContentEntity.slug eq slug }
            .firstOrNull()?.toContent()
    }

    /**
     * 获取内容列表
     */
    override suspend fun getAll(
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?,
        offset: Int,
        limit: Int
    ): List<Content> = newSuspendedTransaction(db = database) {
        var query = if (contentTypeId != null) {
            ContentEntity.find { ContentEntity.contentTypeId eq contentTypeId }
        } else {
            ContentEntity.all()
        }
        
        if (status != null) {
            query = query.andWhere { ContentEntity.status eq status.name }
        }
        
        if (languageCode != null) {
            query = query.andWhere { ContentEntity.languageCode eq languageCode }
        }
        
        query.orderBy(ContentEntity.updatedAt to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toContent() }
    }

    /**
     * 删除内容
     */
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entity = ContentEntity.findById(id) ?: return@newSuspendedTransaction false
        entity.delete()
        true
    }

    /**
     * 获取内容数量
     */
    override suspend fun count(
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?
    ): Long = newSuspendedTransaction(db = database) {
        var query = if (contentTypeId != null) {
            ContentEntity.find { ContentEntity.contentTypeId eq contentTypeId }
        } else {
            ContentEntity.all()
        }
        
        if (status != null) {
            query = query.andWhere { ContentEntity.status eq status.name }
        }
        
        if (languageCode != null) {
            query = query.andWhere { ContentEntity.languageCode eq languageCode }
        }
        
        query.count()
    }
} 