package com.kace.content.domain.service.impl

import com.kace.content.domain.model.ContentType
import com.kace.content.domain.repository.ContentTypeRepository
import com.kace.content.domain.service.ContentTypeService
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 内容类型服务实现
 */
class ContentTypeServiceImpl(
    private val contentTypeRepository: ContentTypeRepository
) : ContentTypeService {
    private val logger = LoggerFactory.getLogger(ContentTypeServiceImpl::class.java)
    
    /**
     * 创建内容类型
     */
    override suspend fun createContentType(contentType: ContentType): ContentType {
        logger.info("创建内容类型: ${contentType.name}")
        
        // 检查代码是否已存在
        val existingContentType = contentTypeRepository.getByCode(contentType.code)
        if (existingContentType != null) {
            throw IllegalArgumentException("内容类型代码已存在: ${contentType.code}")
        }
        
        return contentTypeRepository.create(contentType)
    }
    
    /**
     * 更新内容类型
     */
    override suspend fun updateContentType(contentType: ContentType): ContentType {
        logger.info("更新内容类型: ${contentType.id}")
        
        // 检查内容类型是否存在
        val existingContentType = contentTypeRepository.getById(contentType.id)
            ?: throw IllegalArgumentException("内容类型不存在: ${contentType.id}")
        
        // 如果代码已更改，检查新代码是否已存在
        if (existingContentType.code != contentType.code) {
            val contentTypeWithSameCode = contentTypeRepository.getByCode(contentType.code)
            if (contentTypeWithSameCode != null && contentTypeWithSameCode.id != contentType.id) {
                throw IllegalArgumentException("内容类型代码已存在: ${contentType.code}")
            }
        }
        
        return contentTypeRepository.update(contentType)
    }
    
    /**
     * 删除内容类型
     */
    override suspend fun deleteContentType(id: UUID): Boolean {
        logger.info("删除内容类型: $id")
        
        // 检查内容类型是否存在
        val existingContentType = contentTypeRepository.getById(id)
            ?: throw IllegalArgumentException("内容类型不存在: $id")
        
        // 检查是否为系统内容类型
        if (existingContentType.isSystem) {
            throw IllegalArgumentException("不能删除系统内容类型: $id")
        }
        
        return contentTypeRepository.delete(id)
    }
    
    /**
     * 获取内容类型
     */
    override suspend fun getContentType(id: UUID): ContentType? {
        return contentTypeRepository.getById(id)
    }
    
    /**
     * 获取内容类型列表
     */
    override suspend fun getContentTypes(offset: Int, limit: Int): List<ContentType> {
        return contentTypeRepository.getAll(offset, limit)
    }
    
    /**
     * 根据代码获取内容类型
     */
    override suspend fun getContentTypeByCode(code: String): ContentType? {
        return contentTypeRepository.getByCode(code)
    }
    
    /**
     * 获取内容类型数量
     */
    override suspend fun getContentTypeCount(): Long {
        return contentTypeRepository.count()
    }
} 