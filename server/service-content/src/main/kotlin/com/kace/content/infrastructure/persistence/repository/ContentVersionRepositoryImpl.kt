package com.kace.content.infrastructure.persistence.repository

import com.kace.content.domain.model.ContentVersion
import com.kace.content.domain.repository.ContentVersionRepository
import com.kace.content.infrastructure.persistence.entity.ContentVersionEntity
import com.kace.content.infrastructure.persistence.mapper.toContentVersion
import com.kace.content.infrastructure.persistence.mapper.toEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

/**
 * 内容版本仓库实现
 */
class ContentVersionRepositoryImpl(private val database: Database) : ContentVersionRepository {
    /**
     * 创建内容版本
     */
    override suspend fun create(contentVersion: ContentVersion): ContentVersion = newSuspendedTransaction(db = database) {
        val entity = contentVersion.toEntity()
        entity.id
        contentVersion
    }

    /**
     * 获取内容的所有版本
     */
    override suspend fun getByContentId(contentId: UUID): List<ContentVersion> = newSuspendedTransaction(db = database) {
        ContentVersionEntity.find {
            ContentVersionEntity.contentId eq contentId
        }.orderBy(ContentVersionEntity.version to SortOrder.DESC)
            .map { it.toContentVersion() }
    }

    /**
     * 获取内容的特定版本
     */
    override suspend fun getByContentIdAndVersion(contentId: UUID, version: Int): ContentVersion? = newSuspendedTransaction(db = database) {
        ContentVersionEntity.find {
            (ContentVersionEntity.contentId eq contentId) and
                    (ContentVersionEntity.version eq version)
        }.firstOrNull()?.toContentVersion()
    }

    /**
     * 删除内容的所有版本
     */
    override suspend fun deleteByContentId(contentId: UUID): Boolean = newSuspendedTransaction(db = database) {
        val count = ContentVersionEntity.find {
            ContentVersionEntity.contentId eq contentId
        }.count()
        
        ContentVersionEntity.find {
            ContentVersionEntity.contentId eq contentId
        }.forEach { it.delete() }
        
        count > 0
    }
} 