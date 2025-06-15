package com.kace.content.domain.service.impl

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentStatus
import com.kace.content.domain.model.ContentVersion
import com.kace.content.domain.repository.ContentRepository
import com.kace.content.domain.repository.ContentVersionRepository
import com.kace.content.domain.service.ContentService
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 内容服务实现
 */
class ContentServiceImpl(
    private val contentRepository: ContentRepository,
    private val contentVersionRepository: ContentVersionRepository
) : ContentService {
    private val logger = LoggerFactory.getLogger(ContentServiceImpl::class.java)
    
    /**
     * 创建内容
     */
    override suspend fun createContent(
        contentTypeId: UUID,
        title: String,
        slug: String,
        createdBy: UUID,
        fields: Map<String, String>,
        languageCode: String
    ): Content {
        logger.info("创建内容: $title, 类型: $contentTypeId")
        
        // 检查slug是否已存在
        val existingContent = contentRepository.getBySlug(slug)
        if (existingContent != null) {
            throw IllegalArgumentException("内容slug已存在: $slug")
        }
        
        // 创建内容
        val content = Content.create(
            contentTypeId = contentTypeId,
            title = title,
            slug = slug,
            createdBy = createdBy,
            fields = fields,
            languageCode = languageCode
        )
        
        val savedContent = contentRepository.create(content)
        
        // 创建第一个版本
        createVersion(savedContent, createdBy, "初始版本")
        
        return savedContent
    }
    
    /**
     * 更新内容
     */
    override suspend fun updateContent(
        id: UUID,
        title: String?,
        slug: String?,
        fields: Map<String, String>?,
        updatedBy: UUID
    ): Content {
        logger.info("更新内容: $id")
        
        // 获取内容
        val content = contentRepository.getById(id)
            ?: throw IllegalArgumentException("内容不存在: $id")
        
        // 检查slug是否已存在
        if (slug != null && slug != content.slug) {
            val existingContent = contentRepository.getBySlug(slug)
            if (existingContent != null) {
                throw IllegalArgumentException("内容slug已存在: $slug")
            }
        }
        
        // 更新内容
        val updatedContent = content.update(
            title = title,
            slug = slug,
            fields = fields
        )
        
        val savedContent = contentRepository.update(updatedContent)
        
        // 创建新版本
        createVersion(savedContent, updatedBy, "更新内容")
        
        return savedContent
    }
    
    /**
     * 更改内容状态
     */
    override suspend fun changeContentStatus(
        id: UUID,
        status: ContentStatus,
        updatedBy: UUID
    ): Content {
        logger.info("更改内容状态: $id, 状态: $status")
        
        // 获取内容
        val content = contentRepository.getById(id)
            ?: throw IllegalArgumentException("内容不存在: $id")
        
        // 根据状态执行相应操作
        val updatedContent = when (status) {
            ContentStatus.PUBLISHED -> content.publish()
            ContentStatus.ARCHIVED -> content.archive()
            ContentStatus.REVIEW -> content.sendToReview()
            ContentStatus.DRAFT -> content.copy(
                status = ContentStatus.DRAFT,
                updatedAt = Instant.now().toEpochMilli()
            )
        }
        
        val savedContent = contentRepository.update(updatedContent)
        
        // 创建新版本
        createVersion(savedContent, updatedBy, "更改状态为 ${status.name}")
        
        return savedContent
    }
    
    /**
     * 发布内容
     */
    override suspend fun publishContent(id: UUID, publishedBy: UUID): Content {
        logger.info("发布内容: $id")
        return changeContentStatus(id, ContentStatus.PUBLISHED, publishedBy)
    }
    
    /**
     * 归档内容
     */
    override suspend fun archiveContent(id: UUID, archivedBy: UUID): Content {
        logger.info("归档内容: $id")
        return changeContentStatus(id, ContentStatus.ARCHIVED, archivedBy)
    }
    
    /**
     * 发送内容审核
     */
    override suspend fun sendContentToReview(id: UUID, updatedBy: UUID): Content {
        logger.info("发送内容审核: $id")
        return changeContentStatus(id, ContentStatus.REVIEW, updatedBy)
    }
    
    /**
     * 获取内容
     */
    override suspend fun getContent(id: UUID): Content? {
        return contentRepository.getById(id)
    }
    
    /**
     * 根据slug获取内容
     */
    override suspend fun getContentBySlug(slug: String): Content? {
        return contentRepository.getBySlug(slug)
    }
    
    /**
     * 获取内容列表
     */
    override suspend fun getContents(
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?,
        offset: Int,
        limit: Int
    ): List<Content> {
        return contentRepository.getAll(
            contentTypeId = contentTypeId,
            status = status,
            languageCode = languageCode,
            offset = offset,
            limit = limit
        )
    }
    
    /**
     * 删除内容
     */
    override suspend fun deleteContent(id: UUID): Boolean {
        logger.info("删除内容: $id")
        
        // 删除内容版本
        contentVersionRepository.deleteByContentId(id)
        
        // 删除内容
        return contentRepository.delete(id)
    }
    
    /**
     * 获取内容版本历史
     */
    override suspend fun getContentVersions(contentId: UUID): List<ContentVersion> {
        return contentVersionRepository.getByContentId(contentId)
    }
    
    /**
     * 获取特定版本的内容
     */
    override suspend fun getContentVersion(contentId: UUID, version: Int): ContentVersion? {
        return contentVersionRepository.getByContentIdAndVersion(contentId, version)
    }
    
    /**
     * 恢复到特定版本
     */
    override suspend fun revertToVersion(contentId: UUID, version: Int, updatedBy: UUID): Content {
        logger.info("恢复内容版本: $contentId, 版本: $version")
        
        // 获取内容
        val content = contentRepository.getById(contentId)
            ?: throw IllegalArgumentException("内容不存在: $contentId")
        
        // 获取版本
        val contentVersion = contentVersionRepository.getByContentIdAndVersion(contentId, version)
            ?: throw IllegalArgumentException("内容版本不存在: $contentId, 版本: $version")
        
        // 更新内容
        val updatedContent = content.copy(
            fields = contentVersion.fields,
            updatedAt = Instant.now().toEpochMilli(),
            version = content.version + 1
        )
        
        val savedContent = contentRepository.update(updatedContent)
        
        // 创建新版本
        createVersion(savedContent, updatedBy, "恢复到版本 $version")
        
        return savedContent
    }
    
    /**
     * 获取内容数量
     */
    override suspend fun getContentCount(
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?
    ): Long {
        return contentRepository.count(
            contentTypeId = contentTypeId,
            status = status,
            languageCode = languageCode
        )
    }
    
    /**
     * 创建内容版本
     */
    private suspend fun createVersion(
        content: Content,
        createdBy: UUID,
        comment: String? = null
    ): ContentVersion {
        val contentVersion = ContentVersion.create(
            contentId = content.id,
            version = content.version,
            fields = content.fields,
            createdBy = createdBy,
            comment = comment
        )
        
        return contentVersionRepository.create(contentVersion)
    }
} 