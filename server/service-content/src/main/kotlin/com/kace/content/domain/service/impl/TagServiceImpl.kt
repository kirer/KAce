package com.kace.content.domain.service.impl

import com.kace.content.domain.model.Tag
import com.kace.content.domain.repository.TagRepository
import com.kace.content.domain.service.TagService
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 标签服务实现
 */
class TagServiceImpl(private val tagRepository: TagRepository) : TagService {
    private val logger = LoggerFactory.getLogger(TagServiceImpl::class.java)
    
    /**
     * 创建标签
     */
    override suspend fun createTag(
        name: String,
        slug: String,
        description: String?,
        createdBy: UUID
    ): Tag {
        logger.info("创建标签: $name")
        
        // 检查slug是否已存在
        val existingTag = tagRepository.getBySlug(slug)
        if (existingTag != null) {
            throw IllegalArgumentException("标签slug已存在: $slug")
        }
        
        // 创建标签
        val tag = Tag.create(
            name = name,
            slug = slug,
            description = description,
            createdBy = createdBy
        )
        
        return tagRepository.create(tag)
    }
    
    /**
     * 更新标签
     */
    override suspend fun updateTag(
        id: UUID,
        name: String?,
        slug: String?,
        description: String?,
        updatedBy: UUID
    ): Tag {
        logger.info("更新标签: $id")
        
        // 获取标签
        val tag = tagRepository.getById(id)
            ?: throw IllegalArgumentException("标签不存在: $id")
        
        // 检查slug是否已存在
        if (slug != null && slug != tag.slug) {
            val existingTag = tagRepository.getBySlug(slug)
            if (existingTag != null) {
                throw IllegalArgumentException("标签slug已存在: $slug")
            }
        }
        
        // 更新标签
        val updatedTag = tag.copy(
            name = name ?: tag.name,
            slug = slug ?: tag.slug,
            description = description ?: tag.description,
            updatedAt = Instant.now().toEpochMilli()
        )
        
        return tagRepository.update(updatedTag)
    }
    
    /**
     * 获取标签
     */
    override suspend fun getTag(id: UUID): Tag? {
        return tagRepository.getById(id)
    }
    
    /**
     * 根据slug获取标签
     */
    override suspend fun getTagBySlug(slug: String): Tag? {
        return tagRepository.getBySlug(slug)
    }
    
    /**
     * 获取标签列表
     */
    override suspend fun getTags(offset: Int, limit: Int): List<Tag> {
        return tagRepository.getAll(offset, limit)
    }
    
    /**
     * 获取所有标签
     */
    override suspend fun getAllTags(): List<Tag> {
        return tagRepository.getAll(0, Int.MAX_VALUE)
    }
    
    /**
     * 删除标签
     */
    override suspend fun deleteTag(id: UUID): Boolean {
        logger.info("删除标签: $id")
        return tagRepository.delete(id)
    }
    
    /**
     * 获取标签数量
     */
    override suspend fun getTagCount(): Long {
        return tagRepository.count()
    }
    
    /**
     * 根据名称搜索标签
     */
    override suspend fun searchTags(query: String, offset: Int, limit: Int): List<Tag> {
        return tagRepository.search(query, offset, limit)
    }
} 