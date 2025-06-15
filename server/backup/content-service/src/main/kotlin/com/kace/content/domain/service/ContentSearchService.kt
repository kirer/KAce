package com.kace.content.domain.service

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentStatus
import java.util.UUID

/**
 * 内容搜索服务接口
 */
interface ContentSearchService {
    /**
     * 索引内容
     */
    suspend fun indexContent(content: Content): Boolean
    
    /**
     * 删除内容索引
     */
    suspend fun deleteContentIndex(id: UUID): Boolean
    
    /**
     * 搜索内容
     */
    suspend fun searchContent(
        query: String,
        contentTypeId: UUID? = null,
        status: ContentStatus? = null,
        languageCode: String? = null,
        categoryId: UUID? = null,
        tagId: UUID? = null,
        offset: Int = 0,
        limit: Int = 10
    ): List<Content>
    
    /**
     * 获取搜索结果数量
     */
    suspend fun countSearchResults(
        query: String,
        contentTypeId: UUID? = null,
        status: ContentStatus? = null,
        languageCode: String? = null,
        categoryId: UUID? = null,
        tagId: UUID? = null
    ): Long
    
    /**
     * 重新索引所有内容
     */
    suspend fun reindexAllContent(): Boolean
    
    /**
     * 根据内容类型重新索引内容
     */
    suspend fun reindexContentByType(contentTypeId: UUID): Boolean
    
    /**
     * 获取相关内容
     */
    suspend fun getRelatedContent(
        contentId: UUID,
        limit: Int = 5
    ): List<Content>
} 