package com.kace.content.domain.service

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentComment
import java.util.*

/**
 * 内容评论服务接口
 */
interface ContentCommentService {
    /**
     * 创建评论
     */
    fun createComment(
        contentId: UUID,
        userId: UUID,
        content: String,
        parentId: UUID? = null,
        metadata: Map<String, String>? = null
    ): ContentComment
    
    /**
     * 更新评论内容
     */
    fun updateComment(
        id: UUID,
        userId: UUID,
        content: String
    ): ContentComment?
    
    /**
     * 更新评论状态
     */
    fun updateCommentStatus(
        id: UUID,
        status: String
    ): ContentComment?
    
    /**
     * 删除评论
     */
    fun deleteComment(id: UUID, userId: UUID, isAdmin: Boolean = false): Boolean
    
    /**
     * 根据ID查找评论
     */
    fun findCommentById(id: UUID): ContentComment?
    
    /**
     * 获取内容的评论
     */
    fun findCommentsByContent(
        contentId: UUID,
        pageRequest: PageRequest,
        parentId: UUID? = null,
        status: String? = ContentComment.STATUS_PUBLISHED
    ): PageResponse<ContentComment>
    
    /**
     * 获取用户的评论
     */
    fun findCommentsByUser(
        userId: UUID,
        pageRequest: PageRequest,
        status: String? = null
    ): PageResponse<ContentComment>
    
    /**
     * 获取评论的回复
     */
    fun findReplies(
        commentId: UUID,
        pageRequest: PageRequest,
        status: String? = ContentComment.STATUS_PUBLISHED
    ): PageResponse<ContentComment>
    
    /**
     * 批量更新评论状态
     */
    fun batchUpdateStatus(ids: List<UUID>, status: String): Int
    
    /**
     * 获取最近的评论
     */
    fun getRecentComments(
        pageRequest: PageRequest,
        status: String? = ContentComment.STATUS_PUBLISHED
    ): PageResponse<ContentComment>
    
    /**
     * 计算内容的评论数量
     */
    fun countCommentsByContent(contentId: UUID, status: String? = ContentComment.STATUS_PUBLISHED): Long
} 