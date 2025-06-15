package com.kace.content.domain.service

import com.kace.content.domain.model.ContentType
import java.util.UUID

/**
 * 内容类型服务接口
 */
interface ContentTypeService {
    /**
     * 创建内容类型
     */
    suspend fun createContentType(contentType: ContentType): ContentType
    
    /**
     * 更新内容类型
     */
    suspend fun updateContentType(contentType: ContentType): ContentType
    
    /**
     * 删除内容类型
     */
    suspend fun deleteContentType(id: UUID): Boolean
    
    /**
     * 获取内容类型
     */
    suspend fun getContentType(id: UUID): ContentType?
    
    /**
     * 获取内容类型列表
     */
    suspend fun getContentTypes(offset: Int = 0, limit: Int = 100): List<ContentType>
    
    /**
     * 根据代码获取内容类型
     */
    suspend fun getContentTypeByCode(code: String): ContentType?
    
    /**
     * 获取内容类型数量
     */
    suspend fun getContentTypeCount(): Long
} 