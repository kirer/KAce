package com.kace.content.domain.repository

import com.kace.content.domain.model.Tag
import java.util.UUID

/**
 * 标签仓库接口
 */
interface TagRepository {
    /**
     * 创建标签
     */
    suspend fun create(tag: Tag): Tag
    
    /**
     * 更新标签
     */
    suspend fun update(tag: Tag): Tag
    
    /**
     * 删除标签
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 获取标签
     */
    suspend fun getById(id: UUID): Tag?
    
    /**
     * 根据slug获取标签
     */
    suspend fun getBySlug(slug: String): Tag?
    
    /**
     * 获取标签列表
     */
    suspend fun getAll(offset: Int = 0, limit: Int = 100): List<Tag>
    
    /**
     * 获取标签数量
     */
    suspend fun count(): Long
    
    /**
     * 将内容添加到标签
     */
    suspend fun addContent(contentId: UUID, tagId: UUID): Boolean
    
    /**
     * 从标签中移除内容
     */
    suspend fun removeContent(contentId: UUID, tagId: UUID): Boolean
    
    /**
     * 获取标签下的内容
     */
    suspend fun getContentIds(
        tagId: UUID,
        offset: Int = 0,
        limit: Int = 100
    ): List<UUID>
    
    /**
     * 获取内容的标签
     */
    suspend fun getTagsByContent(contentId: UUID): List<Tag>
    
    /**
     * 根据名称搜索标签
     */
    suspend fun searchByName(
        query: String,
        offset: Int = 0,
        limit: Int = 100
    ): List<Tag>
} 