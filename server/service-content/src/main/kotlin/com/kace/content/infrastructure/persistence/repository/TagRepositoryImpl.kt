package com.kace.content.infrastructure.persistence.repository

import com.kace.content.domain.model.Tag
import com.kace.content.domain.repository.TagRepository
import com.kace.content.infrastructure.persistence.entity.TagEntity
import com.kace.content.infrastructure.persistence.mapper.toEntity
import com.kace.content.infrastructure.persistence.mapper.toTag
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

/**
 * 标签仓库实现
 */
class TagRepositoryImpl(private val database: Database) : TagRepository {
    /**
     * 创建标签
     */
    override suspend fun create(tag: Tag): Tag = newSuspendedTransaction(db = database) {
        val entity = tag.toEntity()
        entity.id
        tag.copy(id = entity.id.value)
    }

    /**
     * 更新标签
     */
    override suspend fun update(tag: Tag): Tag = newSuspendedTransaction(db = database) {
        val entity = TagEntity.findById(tag.id)
            ?: throw IllegalArgumentException("标签不存在: ${tag.id}")
        
        entity.name = tag.name
        entity.slug = tag.slug
        entity.description = tag.description
        entity.updatedAt = tag.updatedAt
        
        tag
    }

    /**
     * 获取标签
     */
    override suspend fun getById(id: UUID): Tag? = newSuspendedTransaction(db = database) {
        TagEntity.findById(id)?.toTag()
    }

    /**
     * 根据slug获取标签
     */
    override suspend fun getBySlug(slug: String): Tag? = newSuspendedTransaction(db = database) {
        TagEntity.find { TagEntity.slug eq slug }
            .firstOrNull()?.toTag()
    }

    /**
     * 获取标签列表
     */
    override suspend fun getAll(offset: Int, limit: Int): List<Tag> = newSuspendedTransaction(db = database) {
        TagEntity.all()
            .orderBy(TagEntity.name to SortOrder.ASC)
            .limit(limit, offset.toLong())
            .map { it.toTag() }
    }

    /**
     * 删除标签
     */
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entity = TagEntity.findById(id) ?: return@newSuspendedTransaction false
        entity.delete()
        true
    }

    /**
     * 获取标签数量
     */
    override suspend fun count(): Long = newSuspendedTransaction(db = database) {
        TagEntity.all().count()
    }

    /**
     * 根据名称搜索标签
     */
    override suspend fun search(query: String, offset: Int, limit: Int): List<Tag> = newSuspendedTransaction(db = database) {
        val lowerQuery = query.lowercase()
        
        TagEntity.find {
            (TagEntity.name.lowerCase() like "%$lowerQuery%") or
                    (TagEntity.slug.lowerCase() like "%$lowerQuery%")
        }.orderBy(TagEntity.name to SortOrder.ASC)
            .limit(limit, offset.toLong())
            .map { it.toTag() }
    }
} 