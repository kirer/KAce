package com.kace.content.domain.service

import com.kace.content.domain.model.Content
import com.kace.content.domain.model.ContentRelation
import com.kace.content.domain.model.ContentRelationType
import java.util.UUID

/**
 * 内容关联服务接口
 */
interface ContentRelationService {
    /**
     * 创建内容关联
     */
    suspend fun createRelation(
        sourceContentId: UUID,
        targetContentId: UUID,
        type: ContentRelationType,
        createdBy: UUID,
        metadata: Map<String, String>? = null
    ): ContentRelation
    
    /**
     * 更新内容关联
     */
    suspend fun updateRelation(
        id: UUID,
        type: ContentRelationType? = null,
        metadata: Map<String, String>? = null,
        updatedBy: UUID
    ): ContentRelation
    
    /**
     * 删除内容关联
     */
    suspend fun deleteRelation(id: UUID): Boolean
    
    /**
     * 获取内容关联
     */
    suspend fun getRelation(id: UUID): ContentRelation?
    
    /**
     * 获取源内容的所有关联
     */
    suspend fun getRelationsBySourceContent(
        sourceContentId: UUID,
        type: ContentRelationType? = null
    ): List<ContentRelation>
    
    /**
     * 获取目标内容的所有关联
     */
    suspend fun getRelationsByTargetContent(
        targetContentId: UUID,
        type: ContentRelationType? = null
    ): List<ContentRelation>
    
    /**
     * 获取源内容的所有关联内容
     */
    suspend fun getRelatedContents(
        sourceContentId: UUID,
        type: ContentRelationType? = null
    ): List<Content>
    
    /**
     * 获取引用该内容的所有内容
     */
    suspend fun getReferencingContents(
        targetContentId: UUID,
        type: ContentRelationType? = null
    ): List<Content>
    
    /**
     * 检查两个内容之间是否存在关联
     */
    suspend fun hasRelation(
        sourceContentId: UUID,
        targetContentId: UUID,
        type: ContentRelationType? = null
    ): Boolean
} 