package com.kace.content.domain.service

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentStatus
import com.kace.content.domain.model.ContentVersion
import java.util.UUID

/**
 * 内容服务接口
 */
interface ContentService {
    /**
     * 创建内容
     */
    suspend fun createContent(
        contentTypeId: UUID,
        title: String,
        slug: String,
        createdBy: UUID,
        fields: Map<String, String>,
        languageCode: String
    ): Content
    
    /**
     * 更新内容
     */
    suspend fun updateContent(
        id: UUID,
        title: String? = null,
        slug: String? = null,
        fields: Map<String, String>? = null,
        updatedBy: UUID
    ): Content
    
    /**
     * 更改内容状态
     */
    suspend fun changeContentStatus(
        id: UUID,
        status: ContentStatus,
        updatedBy: UUID
    ): Content
    
    /**
     * 发布内容
     */
    suspend fun publishContent(id: UUID, publishedBy: UUID): Content
    
    /**
     * 归档内容
     */
    suspend fun archiveContent(id: UUID, archivedBy: UUID): Content
    
    /**
     * 发送内容审核
     */
    suspend fun sendContentToReview(id: UUID, updatedBy: UUID): Content
    
    /**
     * 获取内容
     */
    suspend fun getContent(id: UUID): Content?
    
    /**
     * 根据slug获取内容
     */
    suspend fun getContentBySlug(slug: String): Content?
    
    /**
     * 获取内容列表
     */
    suspend fun getContents(
        contentTypeId: UUID? = null,
        status: ContentStatus? = null,
        languageCode: String? = null,
        offset: Int = 0,
        limit: Int = 100
    ): List<Content>
    
    /**
     * 删除内容
     */
    suspend fun deleteContent(id: UUID): Boolean
    
    /**
     * 获取内容版本历史
     */
    suspend fun getContentVersions(contentId: UUID): List<ContentVersion>
    
    /**
     * 获取特定版本的内容
     */
    suspend fun getContentVersion(contentId: UUID, version: Int): ContentVersion?
    
    /**
     * 恢复到特定版本
     */
    suspend fun revertToVersion(contentId: UUID, version: Int, updatedBy: UUID): Content
    
    /**
     * 获取内容数量
     */
    suspend fun getContentCount(
        contentTypeId: UUID? = null,
        status: ContentStatus? = null,
        languageCode: String? = null
    ): Long
} 