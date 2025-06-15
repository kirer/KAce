package com.kace.content.domain.repository

import com.kace.content.domain.model.Category
import java.util.UUID

/**
 * 分类仓库接口
 */
interface CategoryRepository {
    /**
     * 创建分类
     */
    suspend fun create(category: Category): Category
    
    /**
     * 更新分类
     */
    suspend fun update(category: Category): Category
    
    /**
     * 删除分类
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 获取分类
     */
    suspend fun getById(id: UUID): Category?
    
    /**
     * 根据slug获取分类
     */
    suspend fun getBySlug(slug: String): Category?
    
    /**
     * 获取分类列表
     */
    suspend fun getAll(
        parentId: UUID? = null,
        offset: Int = 0,
        limit: Int = 100
    ): List<Category>
    
    /**
     * 获取分类数量
     */
    suspend fun count(parentId: UUID? = null): Long
    
    /**
     * 将内容添加到分类
     */
    suspend fun addContent(contentId: UUID, categoryId: UUID): Boolean
    
    /**
     * 从分类中移除内容
     */
    suspend fun removeContent(contentId: UUID, categoryId: UUID): Boolean
    
    /**
     * 获取分类下的内容
     */
    suspend fun getContentIds(
        categoryId: UUID,
        offset: Int = 0,
        limit: Int = 100
    ): List<UUID>
    
    /**
     * 获取内容的分类
     */
    suspend fun getCategoriesByContent(contentId: UUID): List<Category>
} 