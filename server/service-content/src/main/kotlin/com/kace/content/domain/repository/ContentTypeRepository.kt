package com.kace.content.domain.repository

import com.kace.content.domain.model.ContentType
import java.util.UUID

/**
 * 内容类型仓库接口
 */
interface ContentTypeRepository {
    /**
     * 创建内容类型
     */
    suspend fun create(contentType: ContentType): ContentType
    
    /**
     * 根据ID获取内容类型
     */
    suspend fun getById(id: UUID): ContentType?
    
    /**
     * 根据名称获取内容类型
     */
    suspend fun getByName(name: String): ContentType?
    
    /**
     * 获取所有内容类型
     */
    suspend fun getAll(offset: Int = 0, limit: Int = 100): List<ContentType>
    
    /**
     * 更新内容类型
     */
    suspend fun update(contentType: ContentType): ContentType
    
    /**
     * 删除内容类型
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 计算内容类型总数
     */
    suspend fun count(): Long
} 