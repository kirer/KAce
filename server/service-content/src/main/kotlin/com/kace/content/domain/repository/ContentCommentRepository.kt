package com.kace.content.domain.repository

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentComment
import java.util.*

/**
 * 内容评论仓库接口
 */
interface ContentCommentRepository {
    /**
     * 创建评论
     */
    fun createComment(comment: ContentComment): ContentComment
    
    /**
     * 更新评论
     */
    fun updateComment(comment: ContentComment): ContentComment?
    
    /**
     * 根据ID查找评论
     */
    fun findCommentById(id: UUID): ContentComment?
    
    /**
     * 删除评论
     */
    fun deleteComment(id: UUID): Boolean
    
    /**
     * 分页查找内容的评论
     */
    fun findCommentsByContent(
        contentId: UUID,
        pageRequest: PageRequest,
        parentId: UUID? = null,
        status: String? = null
    ): PageResponse<ContentComment>
    
    /**
     * 分页查找用户的评论
     */
    fun findCommentsByUser(
        userId: UUID,
        pageRequest: PageRequest,
        status: String? = null
    ): PageResponse<ContentComment>
    
    /**
     * 查找评论的回复
     */
    fun findReplies(
        commentId: UUID,
        pageRequest: PageRequest,
        status: String? = null
    ): PageResponse<ContentComment>
    
    /**
     * 计算内容的评论数量
     */
    fun countCommentsByContent(contentId: UUID, status: String? = null): Long
    
    /**
     * 计算用户的评论数量
     */
    fun countCommentsByUser(userId: UUID, status: String? = null): Long
    
    /**
     * 批量更新评论状态
     */
    fun updateCommentsStatus(ids: List<UUID>, status: String): Int
    
    /**
     * 获取最近的评论
     */
    fun getRecentComments(pageRequest: PageRequest, status: String? = null): PageResponse<ContentComment>
} 