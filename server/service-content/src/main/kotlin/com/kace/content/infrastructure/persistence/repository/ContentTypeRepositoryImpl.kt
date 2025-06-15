package com.kace.content.infrastructure.persistence.repository

import com.kace.content.domain.model.ContentType
import com.kace.content.domain.model.ContentTypeField
import com.kace.content.domain.repository.ContentTypeRepository
import com.kace.content.infrastructure.persistence.entity.ContentTypeEntity
import com.kace.content.infrastructure.persistence.entity.ContentTypeFieldEntity
import com.kace.content.infrastructure.persistence.entity.ContentTypeFields
import com.kace.content.infrastructure.persistence.entity.ContentTypes
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.util.UUID

/**
 * 内容类型仓库实现
 */
class ContentTypeRepositoryImpl : ContentTypeRepository {
    /**
     * 创建内容类型
     */
    override suspend fun create(contentType: ContentType): ContentType = newSuspendedTransaction {
        val now = Instant.now()
        
        val contentTypeEntity = ContentTypeEntity.new(contentType.id) {
            code = contentType.code
            name = contentType.name
            description = contentType.description
            createdBy = contentType.createdBy
            createdAt = now
            updatedAt = now
            isSystem = contentType.isSystem
        }
        
        // 创建字段
        contentType.fields.forEach { field ->
            ContentTypeFieldEntity.new(field.id) {
                this.contentType = contentTypeEntity
                name = field.name
                code = field.code
                description = field.description
                fieldType = field.type
                isRequired = field.isRequired
                defaultValue = field.defaultValue
                validationRules = field.validationRules
                ordering = field.ordering
                createdAt = now
                updatedAt = now
            }
        }
        
        mapToContentType(contentTypeEntity)
    }
    
    /**
     * 更新内容类型
     */
    override suspend fun update(contentType: ContentType): ContentType = newSuspendedTransaction {
        val now = Instant.now()
        
        val contentTypeEntity = ContentTypeEntity.findById(contentType.id)
            ?: throw IllegalArgumentException("内容类型不存在: ${contentType.id}")
        
        // 更新内容类型
        contentTypeEntity.apply {
            code = contentType.code
            name = contentType.name
            description = contentType.description
            updatedAt = now
            isSystem = contentType.isSystem
        }
        
        // 更新字段
        val existingFields = contentTypeEntity.fields.associateBy { it.id.value }
        
        // 删除不再存在的字段
        val newFieldIds = contentType.fields.map { it.id }.toSet()
        existingFields.keys.filterNot { it in newFieldIds }.forEach { fieldId ->
            existingFields[fieldId]?.delete()
        }
        
        // 更新或创建字段
        contentType.fields.forEach { field ->
            val existingField = existingFields[field.id]
            if (existingField != null) {
                // 更新现有字段
                existingField.apply {
                    name = field.name
                    code = field.code
                    description = field.description
                    fieldType = field.type
                    isRequired = field.isRequired
                    defaultValue = field.defaultValue
                    validationRules = field.validationRules
                    ordering = field.ordering
                    updatedAt = now
                }
            } else {
                // 创建新字段
                ContentTypeFieldEntity.new(field.id) {
                    this.contentType = contentTypeEntity
                    name = field.name
                    code = field.code
                    description = field.description
                    fieldType = field.type
                    isRequired = field.isRequired
                    defaultValue = field.defaultValue
                    validationRules = field.validationRules
                    ordering = field.ordering
                    createdAt = now
                    updatedAt = now
                }
            }
        }
        
        mapToContentType(contentTypeEntity)
    }
    
    /**
     * 删除内容类型
     */
    override suspend fun delete(id: UUID): Boolean = newSuspendedTransaction {
        val contentType = ContentTypeEntity.findById(id) ?: return@newSuspendedTransaction false
        contentType.delete()
        true
    }
    
    /**
     * 获取内容类型
     */
    override suspend fun getById(id: UUID): ContentType? = newSuspendedTransaction {
        ContentTypeEntity.findById(id)?.let { mapToContentType(it) }
    }
    
    /**
     * 根据代码获取内容类型
     */
    override suspend fun getByCode(code: String): ContentType? = newSuspendedTransaction {
        ContentTypeEntity.find { ContentTypes.code eq code }.firstOrNull()?.let { mapToContentType(it) }
    }
    
    /**
     * 获取所有内容类型
     */
    override suspend fun getAll(offset: Int, limit: Int): List<ContentType> = newSuspendedTransaction {
        ContentTypeEntity.all()
            .orderBy(ContentTypes.name to SortOrder.ASC)
            .limit(limit, offset.toLong())
            .map { mapToContentType(it) }
    }
    
    /**
     * 获取内容类型数量
     */
    override suspend fun count(): Long = newSuspendedTransaction {
        ContentTypeEntity.count()
    }
    
    /**
     * 将实体映射为领域模型
     */
    private fun mapToContentType(entity: ContentTypeEntity): ContentType {
        val fields = entity.fields
            .sortedBy { it.ordering }
            .map { fieldEntity ->
                ContentTypeField(
                    id = fieldEntity.id.value,
                    name = fieldEntity.name,
                    code = fieldEntity.code,
                    description = fieldEntity.description,
                    type = fieldEntity.fieldType,
                    isRequired = fieldEntity.isRequired,
                    defaultValue = fieldEntity.defaultValue,
                    validationRules = fieldEntity.validationRules,
                    ordering = fieldEntity.ordering
                )
            }
        
        return ContentType(
            id = entity.id.value,
            code = entity.code,
            name = entity.name,
            description = entity.description,
            createdBy = entity.createdBy,
            createdAt = entity.createdAt.toEpochMilli(),
            updatedAt = entity.updatedAt.toEpochMilli(),
            isSystem = entity.isSystem,
            fields = fields
        )
    }
} 