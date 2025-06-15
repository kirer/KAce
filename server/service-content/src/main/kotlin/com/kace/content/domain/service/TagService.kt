package com.kace.content.domain.service

import com.kace.content.domain.model.Tag
import java.util.UUID

/**
 * 标签服务接口
 */
interface TagService {
    /**
     * 创建标签
     */
    suspend fun createTag(
        name: String,
        slug: String,
        description: String? = null,
        createdBy: UUID
    ): Tag
    
    /**
     * 更新标签
     */
    suspend fun updateTag(
        id: UUID,
        name: String? = null,
        slug: String? = null,
        description: String? = null,
        updatedBy: UUID
    ): Tag
    
    /**
     * 删除标签
     */
    suspend fun deleteTag(id: UUID): Boolean
    
    /**
     * 获取标签
     */
    suspend fun getTag(id: UUID): Tag?
    
    /**
     * 根据slug获取标签
     */
    suspend fun getTagBySlug(slug: String): Tag?
    
    /**
     * 获取标签列表
     */
    suspend fun getTags(offset: Int = 0, limit: Int = 100): List<Tag>
    
    /**
     * 获取标签数量
     */
    suspend fun getTagCount(): Long
    
    /**
     * 将内容添加到标签
     */
    suspend fun addContentToTag(contentId: UUID, tagId: UUID): Boolean
    
    /**
     * 从标签中移除内容
     */
    suspend fun removeContentFromTag(contentId: UUID, tagId: UUID): Boolean
    
    /**
     * 获取标签下的内容
     */
    suspend fun getContentsByTag(
        tagId: UUID,
        offset: Int = 0,
        limit: Int = 100
    ): List<UUID>
    
    /**
     * 获取内容的标签
     */
    suspend fun getTagsByContent(contentId: UUID): List<Tag>
} 