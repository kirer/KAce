package com.kace.content.infrastructure.search

import com.kace.content.domain.model.Content
import java.util.UUID

/**
 * 内容搜索客户端接口
 */
interface ContentSearchClient {
    /**
     * 创建索引
     */
    suspend fun createIndex(): Boolean
    
    /**
     * 删除索引
     */
    suspend fun deleteIndex(): Boolean
    
    /**
     * 索引内容
     */
    suspend fun indexContent(content: Content): Boolean
    
    /**
     * 删除内容索引
     */
    suspend fun deleteContent(id: UUID): Boolean
    
    /**
     * 删除指定类型的所有内容索引
     */
    suspend fun deleteContentByType(contentTypeId: UUID): Boolean
    
    /**
     * 搜索内容
     */
    suspend fun searchContent(
        query: String,
        contentTypeId: UUID? = null,
        status: String? = null,
        languageCode: String? = null,
        categoryId: UUID? = null,
        tagId: UUID? = null,
        offset: Int = 0,
        limit: Int = 10
    ): List<UUID>
    
    /**
     * 获取搜索结果数量
     */
    suspend fun countSearchResults(
        query: String,
        contentTypeId: UUID? = null,
        status: String? = null,
        languageCode: String? = null,
        categoryId: UUID? = null,
        tagId: UUID? = null
    ): Long
    
    /**
     * 查找相关内容
     */
    suspend fun findRelatedContent(
        contentId: UUID,
        contentTypeId: UUID,
        title: String,
        fields: Map<String, String>,
        limit: Int = 5
    ): List<UUID>
} 