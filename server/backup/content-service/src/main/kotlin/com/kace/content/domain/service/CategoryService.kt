package com.kace.content.domain.service

import com.kace.content.domain.model.Category
import java.util.UUID

/**
 * 分类服务接口
 */
interface CategoryService {
    /**
     * 创建分类
     */
    suspend fun createCategory(
        name: String,
        slug: String,
        description: String? = null,
        parentId: UUID? = null,
        createdBy: UUID
    ): Category
    
    /**
     * 更新分类
     */
    suspend fun updateCategory(
        id: UUID,
        name: String? = null,
        slug: String? = null,
        description: String? = null,
        parentId: UUID? = null,
        updatedBy: UUID
    ): Category
    
    /**
     * 删除分类
     */
    suspend fun deleteCategory(id: UUID): Boolean
    
    /**
     * 获取分类
     */
    suspend fun getCategory(id: UUID): Category?
    
    /**
     * 根据slug获取分类
     */
    suspend fun getCategoryBySlug(slug: String): Category?
    
    /**
     * 获取分类列表
     */
    suspend fun getCategories(
        parentId: UUID? = null,
        offset: Int = 0,
        limit: Int = 100
    ): List<Category>
    
    /**
     * 获取分类树
     */
    suspend fun getCategoryTree(): List<Category>
    
    /**
     * 获取分类数量
     */
    suspend fun getCategoryCount(parentId: UUID? = null): Long
    
    /**
     * 将内容添加到分类
     */
    suspend fun addContentToCategory(contentId: UUID, categoryId: UUID): Boolean
    
    /**
     * 从分类中移除内容
     */
    suspend fun removeContentFromCategory(contentId: UUID, categoryId: UUID): Boolean
    
    /**
     * 获取分类下的内容
     */
    suspend fun getContentsByCategory(
        categoryId: UUID,
        offset: Int = 0,
        limit: Int = 100
    ): List<UUID>
} 