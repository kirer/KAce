package com.kace.content.domain.repository

import com.kace.content.domain.model.ContentVersion
import java.util.UUID

/**
 * 内容版本仓库接口
 */
interface ContentVersionRepository {
    /**
     * 创建内容版本
     */
    suspend fun create(contentVersion: ContentVersion): ContentVersion
    
    /**
     * 获取内容版本
     */
    suspend fun getById(id: UUID): ContentVersion?
    
    /**
     * 根据内容ID获取所有版本
     */
    suspend fun getByContentId(contentId: UUID): List<ContentVersion>
    
    /**
     * 根据内容ID和版本号获取特定版本
     */
    suspend fun getByContentIdAndVersion(contentId: UUID, version: Int): ContentVersion?
    
    /**
     * 获取内容的最新版本
     */
    suspend fun getLatestVersion(contentId: UUID): ContentVersion?
    
    /**
     * 删除内容版本
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 删除内容的所有版本
     */
    suspend fun deleteByContentId(contentId: UUID): Boolean
} 