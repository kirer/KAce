package com.kace.content.infrastructure.persistence.repository

import com.kace.content.domain.model.Category
import com.kace.content.domain.repository.CategoryRepository
import com.kace.content.infrastructure.persistence.entity.CategoryEntity
import com.kace.content.infrastructure.persistence.mapper.toCategory
import com.kace.content.infrastructure.persistence.mapper.toEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

/**
 * 分类仓库实现
 */
class CategoryRepositoryImpl(private val database: Database) : CategoryRepository {
    /**
     * 创建分类
     */
    override suspend fun create(category: Category): Category = newSuspendedTransaction(db = database) {
        val entity = category.toEntity()
        entity.id
        category.copy(id = entity.id.value)
    }

    /**
     * 更新分类
     */
    override suspend fun update(category: Category): Category = newSuspendedTransaction(db = database) {
        val entity = CategoryEntity.findById(category.id)
            ?: throw IllegalArgumentException("分类不存在: ${category.id}")
        
        entity.name = category.name
        entity.slug = category.slug
        entity.description = category.description
        entity.parentId = category.parentId
        entity.updatedAt = category.updatedAt
        
        category
    }

    /**
     * 获取分类
     */
    override suspend fun getById(id: UUID): Category? = newSuspendedTransaction(db = database) {
        CategoryEntity.findById(id)?.toCategory()
    }

    /**
     * 根据slug获取分类
     */
    override suspend fun getBySlug(slug: String): Category? = newSuspendedTransaction(db = database) {
        CategoryEntity.find { CategoryEntity.slug eq slug }
            .firstOrNull()?.toCategory()
    }

    /**
     * 根据父分类获取子分类
     */
    override suspend fun getByParentId(parentId: UUID): List<Category> = newSuspendedTransaction(db = database) {
        CategoryEntity.find { CategoryEntity.parentId eq parentId }
            .map { it.toCategory() }
    }

    /**
     * 获取分类列表
     */
    override suspend fun getAll(parentId: UUID?, offset: Int, limit: Int): List<Category> = newSuspendedTransaction(db = database) {
        (if (parentId != null) {
            CategoryEntity.find { CategoryEntity.parentId eq parentId }
        } else {
            CategoryEntity.all()
        }).orderBy(CategoryEntity.name to SortOrder.ASC)
            .limit(limit, offset.toLong())
            .map { it.toCategory() }
    }

    /**
     * 删除分类
     */
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val entity = CategoryEntity.findById(id) ?: return@newSuspendedTransaction false
        entity.delete()
        true
    }

    /**
     * 获取分类数量
     */
    override suspend fun count(parentId: UUID?): Long = newSuspendedTransaction(db = database) {
        (if (parentId != null) {
            CategoryEntity.find { CategoryEntity.parentId eq parentId }
        } else {
            CategoryEntity.all()
        }).count()
    }
} 