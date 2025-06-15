package com.kace.content.domain.service.impl

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentStatus
import com.kace.content.domain.repository.ContentRepository
import com.kace.content.domain.service.ContentSearchService
import com.kace.content.infrastructure.search.ContentSearchClient
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 内容搜索服务实现
 */
class ContentSearchServiceImpl(
    private val contentRepository: ContentRepository,
    private val searchClient: ContentSearchClient
) : ContentSearchService {
    private val logger = LoggerFactory.getLogger(ContentSearchServiceImpl::class.java)
    
    /**
     * 索引内容
     */
    override suspend fun indexContent(content: Content): Boolean {
        logger.info("索引内容: ${content.id}")
        return try {
            searchClient.indexContent(content)
            true
        } catch (e: Exception) {
            logger.error("索引内容失败: ${content.id}", e)
            false
        }
    }
    
    /**
     * 删除内容索引
     */
    override suspend fun deleteContentIndex(id: UUID): Boolean {
        logger.info("删除内容索引: $id")
        return try {
            searchClient.deleteContent(id)
            true
        } catch (e: Exception) {
            logger.error("删除内容索引失败: $id", e)
            false
        }
    }
    
    /**
     * 搜索内容
     */
    override suspend fun searchContent(
        query: String,
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?,
        categoryId: UUID?,
        tagId: UUID?,
        offset: Int,
        limit: Int
    ): List<Content> {
        logger.info("搜索内容: $query, 类型: $contentTypeId, 状态: $status, 语言: $languageCode, 分类: $categoryId, 标签: $tagId")
        
        // 搜索内容ID
        val contentIds = searchClient.searchContent(
            query = query,
            contentTypeId = contentTypeId,
            status = status?.name,
            languageCode = languageCode,
            categoryId = categoryId,
            tagId = tagId,
            offset = offset,
            limit = limit
        )
        
        if (contentIds.isEmpty()) {
            return emptyList()
        }
        
        // 获取内容详情
        return contentIds.mapNotNull { contentId ->
            contentRepository.getById(contentId)
        }
    }
    
    /**
     * 获取搜索结果数量
     */
    override suspend fun countSearchResults(
        query: String,
        contentTypeId: UUID?,
        status: ContentStatus?,
        languageCode: String?,
        categoryId: UUID?,
        tagId: UUID?
    ): Long {
        logger.info("计算搜索结果数量: $query")
        
        return searchClient.countSearchResults(
            query = query,
            contentTypeId = contentTypeId,
            status = status?.name,
            languageCode = languageCode,
            categoryId = categoryId,
            tagId = tagId
        )
    }
    
    /**
     * 重新索引所有内容
     */
    override suspend fun reindexAllContent(): Boolean {
        logger.info("重新索引所有内容")
        
        try {
            // 清空索引
            searchClient.deleteIndex()
            searchClient.createIndex()
            
            // 分批获取所有内容
            val batchSize = 100
            var offset = 0
            var hasMore = true
            
            while (hasMore) {
                val contents = contentRepository.getAll(
                    contentTypeId = null,
                    status = null,
                    languageCode = null,
                    offset = offset,
                    limit = batchSize
                )
                
                if (contents.isEmpty()) {
                    hasMore = false
                } else {
                    // 索引内容
                    contents.forEach { content ->
                        indexContent(content)
                    }
                    
                    offset += batchSize
                }
            }
            
            return true
        } catch (e: Exception) {
            logger.error("重新索引所有内容失败", e)
            return false
        }
    }
    
    /**
     * 根据内容类型重新索引内容
     */
    override suspend fun reindexContentByType(contentTypeId: UUID): Boolean {
        logger.info("重新索引内容类型: $contentTypeId")
        
        try {
            // 删除该类型的所有内容索引
            searchClient.deleteContentByType(contentTypeId)
            
            // 分批获取该类型的所有内容
            val batchSize = 100
            var offset = 0
            var hasMore = true
            
            while (hasMore) {
                val contents = contentRepository.getAll(
                    contentTypeId = contentTypeId,
                    status = null,
                    languageCode = null,
                    offset = offset,
                    limit = batchSize
                )
                
                if (contents.isEmpty()) {
                    hasMore = false
                } else {
                    // 索引内容
                    contents.forEach { content ->
                        indexContent(content)
                    }
                    
                    offset += batchSize
                }
            }
            
            return true
        } catch (e: Exception) {
            logger.error("重新索引内容类型失败: $contentTypeId", e)
            return false
        }
    }
    
    /**
     * 获取相关内容
     */
    override suspend fun getRelatedContent(contentId: UUID, limit: Int): List<Content> {
        logger.info("获取相关内容: $contentId")
        
        // 获取内容
        val content = contentRepository.getById(contentId)
            ?: return emptyList()
        
        // 获取相关内容ID
        val relatedContentIds = searchClient.findRelatedContent(
            contentId = contentId,
            contentTypeId = content.contentTypeId,
            title = content.title,
            fields = content.fields,
            limit = limit
        )
        
        if (relatedContentIds.isEmpty()) {
            return emptyList()
        }
        
        // 获取内容详情
        return relatedContentIds.mapNotNull { id ->
            contentRepository.getById(id)
        }
    }
} 