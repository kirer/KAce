package com.kace.content.domain.service.impl

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentRelation
import com.kace.content.domain.model.ContentRelationType
import com.kace.content.domain.repository.ContentRelationRepository
import com.kace.content.domain.repository.ContentRepository
import com.kace.content.domain.service.ContentRelationService
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * 内容关联服务实现
 */
class ContentRelationServiceImpl(
    private val contentRepository: ContentRepository,
    private val contentRelationRepository: ContentRelationRepository
) : ContentRelationService {
    private val logger = LoggerFactory.getLogger(ContentRelationServiceImpl::class.java)
    
    /**
     * 创建内容关联
     */
    override suspend fun createRelation(
        sourceContentId: UUID,
        targetContentId: UUID,
        type: ContentRelationType,
        createdBy: UUID,
        metadata: Map<String, String>?
    ): ContentRelation {
        logger.info("创建内容关联: 源内容 $sourceContentId, 目标内容 $targetContentId, 类型 $type")
        
        // 检查源内容是否存在
        val sourceContent = contentRepository.getById(sourceContentId)
            ?: throw IllegalArgumentException("源内容不存在: $sourceContentId")
        
        // 检查目标内容是否存在
        val targetContent = contentRepository.getById(targetContentId)
            ?: throw IllegalArgumentException("目标内容不存在: $targetContentId")
        
        // 检查关联是否已存在
        val existingRelation = contentRelationRepository.getBySourceAndTarget(
            sourceContentId = sourceContentId,
            targetContentId = targetContentId,
            type = type
        )
        
        if (existingRelation != null) {
            return existingRelation
        }
        
        // 创建关联
        val relation = ContentRelation.create(
            sourceContentId = sourceContentId,
            targetContentId = targetContentId,
            type = type,
            metadata = metadata ?: emptyMap(),
            createdBy = createdBy
        )
        
        return contentRelationRepository.create(relation)
    }
    
    /**
     * 更新内容关联
     */
    override suspend fun updateRelation(
        id: UUID,
        type: ContentRelationType?,
        metadata: Map<String, String>?,
        updatedBy: UUID
    ): ContentRelation {
        logger.info("更新内容关联: $id")
        
        // 获取关联
        val relation = contentRelationRepository.getById(id)
            ?: throw IllegalArgumentException("内容关联不存在: $id")
        
        // 更新关联
        val updatedRelation = relation.update(
            type = type,
            metadata = metadata
        )
        
        return contentRelationRepository.update(updatedRelation)
    }
    
    /**
     * 删除内容关联
     */
    override suspend fun deleteRelation(id: UUID): Boolean {
        logger.info("删除内容关联: $id")
        return contentRelationRepository.delete(id)
    }
    
    /**
     * 获取内容关联
     */
    override suspend fun getRelation(id: UUID): ContentRelation? {
        return contentRelationRepository.getById(id)
    }
    
    /**
     * 获取源内容的所有关联
     */
    override suspend fun getRelationsBySourceContent(
        sourceContentId: UUID,
        type: ContentRelationType?
    ): List<ContentRelation> {
        return contentRelationRepository.getBySourceContent(sourceContentId, type)
    }
    
    /**
     * 获取目标内容的所有关联
     */
    override suspend fun getRelationsByTargetContent(
        targetContentId: UUID,
        type: ContentRelationType?
    ): List<ContentRelation> {
        return contentRelationRepository.getByTargetContent(targetContentId, type)
    }
    
    /**
     * 获取源内容的所有关联内容
     */
    override suspend fun getRelatedContents(
        sourceContentId: UUID,
        type: ContentRelationType?
    ): List<Content> {
        // 获取所有关联
        val relations = contentRelationRepository.getBySourceContent(sourceContentId, type)
        
        if (relations.isEmpty()) {
            return emptyList()
        }
        
        // 获取目标内容
        val targetContentIds = relations.map { it.targetContentId }
        return targetContentIds.mapNotNull { contentRepository.getById(it) }
    }
    
    /**
     * 获取引用该内容的所有内容
     */
    override suspend fun getReferencingContents(
        targetContentId: UUID,
        type: ContentRelationType?
    ): List<Content> {
        // 获取所有关联
        val relations = contentRelationRepository.getByTargetContent(targetContentId, type)
        
        if (relations.isEmpty()) {
            return emptyList()
        }
        
        // 获取源内容
        val sourceContentIds = relations.map { it.sourceContentId }
        return sourceContentIds.mapNotNull { contentRepository.getById(it) }
    }
    
    /**
     * 检查两个内容之间是否存在关联
     */
    override suspend fun hasRelation(
        sourceContentId: UUID,
        targetContentId: UUID,
        type: ContentRelationType?
    ): Boolean {
        return contentRelationRepository.getBySourceAndTarget(
            sourceContentId = sourceContentId,
            targetContentId = targetContentId,
            type = type
        ) != null
    }
} 