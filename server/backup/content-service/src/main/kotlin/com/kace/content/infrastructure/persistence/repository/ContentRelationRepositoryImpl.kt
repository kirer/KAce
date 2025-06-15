package com.kace.content.infrastructure.persistence.repository

import com.kace.content.domain.model.ContentRelation
import com.kace.content.domain.model.ContentRelationType
import com.kace.content.domain.repository.ContentRelationRepository
import com.kace.content.infrastructure.persistence.entity.ContentRelationEntity
import com.kace.content.infrastructure.persistence.entity.ContentRelationTable
import com.kace.content.infrastructure.persistence.mapper.toContentRelation
import com.kace.content.infrastructure.persistence.mapper.toEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

/**
 * 内容关联仓库实现
 */
class ContentRelationRepositoryImpl(private val database: Database) : ContentRelationRepository {
    /**
     * 创建内容关联
     */
    override suspend fun create(relation: ContentRelation): ContentRelation = newSuspendedTransaction(db = database) {
        val entity = relation.toEntity()
        entity.id
        relation.copy(id = entity.id.value)
    }

    /**
     * 更新内容关联
     */
    override suspend fun update(relation: ContentRelation): ContentRelation = newSuspendedTransaction(db = database) {
        val entity = ContentRelationEntity.findById(relation.id)
            ?: throw IllegalArgumentException("内容关联不存在: ${relation.id}")
        
        entity.type = relation.type.name
        entity.metadata = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.serializer<Map<String, String>>(), 
            relation.metadata
        )
        entity.updatedAt = relation.updatedAt
        
        relation
    }

    /**
     * 删除内容关联
     */
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entity = ContentRelationEntity.findById(id) ?: return@newSuspendedTransaction false
        entity.delete()
        true
    }

    /**
     * 获取内容关联
     */
    override suspend fun getById(id: UUID): ContentRelation? = newSuspendedTransaction(db = database) {
        ContentRelationEntity.findById(id)?.toContentRelation()
    }

    /**
     * 获取源内容的所有关联
     */
    override suspend fun getBySourceContent(
        sourceContentId: UUID,
        type: ContentRelationType?
    ): List<ContentRelation> = newSuspendedTransaction(db = database) {
        val query = if (type != null) {
            ContentRelationEntity.find {
                (ContentRelationTable.sourceContentId eq sourceContentId) and
                        (ContentRelationTable.type eq type.name)
            }
        } else {
            ContentRelationEntity.find {
                ContentRelationTable.sourceContentId eq sourceContentId
            }
        }
        
        query.map { it.toContentRelation() }
    }

    /**
     * 获取目标内容的所有关联
     */
    override suspend fun getByTargetContent(
        targetContentId: UUID,
        type: ContentRelationType?
    ): List<ContentRelation> = newSuspendedTransaction(db = database) {
        val query = if (type != null) {
            ContentRelationEntity.find {
                (ContentRelationTable.targetContentId eq targetContentId) and
                        (ContentRelationTable.type eq type.name)
            }
        } else {
            ContentRelationEntity.find {
                ContentRelationTable.targetContentId eq targetContentId
            }
        }
        
        query.map { it.toContentRelation() }
    }

    /**
     * 获取源内容和目标内容之间的关联
     */
    override suspend fun getBySourceAndTarget(
        sourceContentId: UUID,
        targetContentId: UUID,
        type: ContentRelationType?
    ): ContentRelation? = newSuspendedTransaction(db = database) {
        val query = if (type != null) {
            ContentRelationEntity.find {
                (ContentRelationTable.sourceContentId eq sourceContentId) and
                        (ContentRelationTable.targetContentId eq targetContentId) and
                        (ContentRelationTable.type eq type.name)
            }
        } else {
            ContentRelationEntity.find {
                (ContentRelationTable.sourceContentId eq sourceContentId) and
                        (ContentRelationTable.targetContentId eq targetContentId)
            }
        }
        
        query.firstOrNull()?.toContentRelation()
    }

    /**
     * 删除内容的所有关联
     */
    override suspend fun deleteByContent(contentId: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entities = ContentRelationEntity.find {
            (ContentRelationTable.sourceContentId eq contentId) or
                    (ContentRelationTable.targetContentId eq contentId)
        }
        
        val count = entities.count()
        entities.forEach { it.delete() }
        
        count > 0
    }
} 