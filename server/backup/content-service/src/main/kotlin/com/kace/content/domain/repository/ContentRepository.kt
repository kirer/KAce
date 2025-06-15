package com.kace.content.domain.repository

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentStatus
import java.util.UUID

/**
 * 内容仓库接口
 */
interface ContentRepository {
    /**
     * 创建内容
     */
    suspend fun create(content: Content): Content
    
    /**
     * 根据ID获取内容
     */
    suspend fun getById(id: UUID): Content?
    
    /**
     * 根据slug获取内容
     */
    suspend fun getBySlug(slug: String): Content?
    
    /**
     * 获取所有内容
     */
    suspend fun getAll(
        contentTypeId: UUID? = null,
        status: ContentStatus? = null,
        languageCode: String? = null,
        offset: Int = 0,
        limit: Int = 100
    ): List<Content>
    
    /**
     * 更新内容
     */
    suspend fun update(content: Content): Content
    
    /**
     * 删除内容
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 计算内容总数
     */
    suspend fun count(
        contentTypeId: UUID? = null,
        status: ContentStatus? = null,
        languageCode: String? = null
    ): Long
    
    /**
     * 根据内容类型ID获取内容
     */
    suspend fun getByContentTypeId(
        contentTypeId: UUID,
        offset: Int = 0,
        limit: Int = 100
    ): List<Content>
    
    /**
     * 根据创建者ID获取内容
     */
    suspend fun getByCreatedBy(
        createdBy: UUID,
        offset: Int = 0,
        limit: Int = 100
    ): List<Content>
    
    /**
     * 根据状态获取内容
     */
    suspend fun getByStatus(
        status: ContentStatus,
        offset: Int = 0,
        limit: Int = 100
    ): List<Content>
} 