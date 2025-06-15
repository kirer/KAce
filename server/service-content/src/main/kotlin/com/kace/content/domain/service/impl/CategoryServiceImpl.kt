package com.kace.content.domain.service.impl

import com.kace.content.domain.model.Category
import com.kace.content.domain.repository.CategoryRepository
import com.kace.content.domain.service.CategoryService
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

/**
 * 分类服务实现
 */
class CategoryServiceImpl(private val categoryRepository: CategoryRepository) : CategoryService {
    private val logger = LoggerFactory.getLogger(CategoryServiceImpl::class.java)
    
    /**
     * 创建分类
     */
    override suspend fun createCategory(
        name: String,
        slug: String,
        description: String?,
        parentId: UUID?,
        createdBy: UUID
    ): Category {
        logger.info("创建分类: $name")
        
        // 检查slug是否已存在
        val existingCategory = categoryRepository.getBySlug(slug)
        if (existingCategory != null) {
            throw IllegalArgumentException("分类slug已存在: $slug")
        }
        
        // 如果有父分类，检查父分类是否存在
        if (parentId != null) {
            val parent = categoryRepository.getById(parentId)
                ?: throw IllegalArgumentException("父分类不存在: $parentId")
        }
        
        // 创建分类
        val category = Category.create(
            name = name,
            slug = slug,
            description = description,
            parentId = parentId,
            createdBy = createdBy
        )
        
        return categoryRepository.create(category)
    }
    
    /**
     * 更新分类
     */
    override suspend fun updateCategory(
        id: UUID,
        name: String?,
        slug: String?,
        description: String?,
        parentId: UUID?,
        updatedBy: UUID
    ): Category {
        logger.info("更新分类: $id")
        
        // 获取分类
        val category = categoryRepository.getById(id)
            ?: throw IllegalArgumentException("分类不存在: $id")
        
        // 检查slug是否已存在
        if (slug != null && slug != category.slug) {
            val existingCategory = categoryRepository.getBySlug(slug)
            if (existingCategory != null) {
                throw IllegalArgumentException("分类slug已存在: $slug")
            }
        }
        
        // 如果有父分类，检查父分类是否存在
        if (parentId != null && parentId != category.parentId) {
            // 检查是否形成循环引用
            if (parentId == id) {
                throw IllegalArgumentException("分类不能作为自己的父分类")
            }
            
            val parent = categoryRepository.getById(parentId)
                ?: throw IllegalArgumentException("父分类不存在: $parentId")
                
            // 检查是否是当前分类的子分类
            checkForCyclicReference(id, parentId)
        }
        
        // 更新分类
        val updatedCategory = category.copy(
            name = name ?: category.name,
            slug = slug ?: category.slug,
            description = description ?: category.description,
            parentId = parentId,
            updatedAt = Instant.now().toEpochMilli()
        )
        
        return categoryRepository.update(updatedCategory)
    }
    
    /**
     * 检查是否形成循环引用
     */
    private suspend fun checkForCyclicReference(categoryId: UUID, parentId: UUID) {
        // 获取所有子分类
        val children = categoryRepository.getByParentId(categoryId)
        
        // 检查子分类中是否包含父分类
        for (child in children) {
            if (child.id == parentId) {
                throw IllegalArgumentException("不能将子分类设置为父分类，会形成循环引用")
            }
            
            // 递归检查
            checkForCyclicReference(child.id, parentId)
        }
    }
    
    /**
     * 获取分类
     */
    override suspend fun getCategory(id: UUID): Category? {
        return categoryRepository.getById(id)
    }
    
    /**
     * 根据slug获取分类
     */
    override suspend fun getCategoryBySlug(slug: String): Category? {
        return categoryRepository.getBySlug(slug)
    }
    
    /**
     * 获取分类列表
     */
    override suspend fun getCategories(parentId: UUID?, offset: Int, limit: Int): List<Category> {
        return categoryRepository.getAll(parentId, offset, limit)
    }
    
    /**
     * 获取所有分类
     */
    override suspend fun getAllCategories(): List<Category> {
        return categoryRepository.getAll(null, 0, Int.MAX_VALUE)
    }
    
    /**
     * 获取分类树
     */
    override suspend fun getCategoryTree(): List<Category> {
        // 获取所有分类
        val allCategories = getAllCategories()
        
        // 构建分类树
        return buildCategoryTree(allCategories, null)
    }
    
    /**
     * 构建分类树
     */
    private fun buildCategoryTree(categories: List<Category>, parentId: UUID?): List<Category> {
        return categories.filter { it.parentId == parentId }
            .map { category ->
                val children = buildCategoryTree(categories, category.id)
                category.copy(children = children)
            }
    }
    
    /**
     * 删除分类
     */
    override suspend fun deleteCategory(id: UUID): Boolean {
        logger.info("删除分类: $id")
        
        // 检查是否有子分类
        val children = categoryRepository.getByParentId(id)
        if (children.isNotEmpty()) {
            throw IllegalArgumentException("分类有子分类，不能删除")
        }
        
        return categoryRepository.delete(id)
    }
    
    /**
     * 获取分类数量
     */
    override suspend fun getCategoryCount(parentId: UUID?): Long {
        return categoryRepository.count(parentId)
    }
} 