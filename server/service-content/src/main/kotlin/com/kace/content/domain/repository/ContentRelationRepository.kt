package com.kace.content.domain.repository

import com.kace.content.domain.model.ContentRelation
import com.kace.content.domain.model.ContentRelationType
import java.util.UUID

/**
 * 内容关联仓库接口
 */
interface ContentRelationRepository {
    /**
     * 创建内容关联
     */
    suspend fun create(relation: ContentRelation): ContentRelation
    
    /**
     * 更新内容关联
     */
    suspend fun update(relation: ContentRelation): ContentRelation
    
    /**
     * 删除内容关联
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 获取内容关联
     */
    suspend fun getById(id: UUID): ContentRelation?
    
    /**
     * 获取源内容的所有关联
     */
    suspend fun getBySourceContent(
        sourceContentId: UUID,
        type: ContentRelationType? = null
    ): List<ContentRelation>
    
    /**
     * 获取目标内容的所有关联
     */
    suspend fun getByTargetContent(
        targetContentId: UUID,
        type: ContentRelationType? = null
    ): List<ContentRelation>
    
    /**
     * 获取源内容和目标内容之间的关联
     */
    suspend fun getBySourceAndTarget(
        sourceContentId: UUID,
        targetContentId: UUID,
        type: ContentRelationType? = null
    ): ContentRelation?
    
    /**
     * 删除内容的所有关联
     */
    suspend fun deleteByContent(contentId: UUID): Boolean
} 