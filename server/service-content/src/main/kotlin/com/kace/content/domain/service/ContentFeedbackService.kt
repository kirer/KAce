package com.kace.content.domain.service

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentFeedback
import java.util.*

/**
 * 内容反馈服务接口
 */
interface ContentFeedbackService {
    /**
     * 创建或更新反馈
     */
    fun createOrUpdateFeedback(
        contentId: UUID,
        userId: UUID,
        type: String,
        value: Int,
        metadata: Map<String, String>? = null
    ): ContentFeedback
    
    /**
     * 删除反馈
     */
    fun deleteFeedback(id: UUID, userId: UUID, isAdmin: Boolean = false): Boolean
    
    /**
     * 根据ID查找反馈
     */
    fun findFeedbackById(id: UUID): ContentFeedback?
    
    /**
     * 获取用户对内容的特定类型反馈
     */
    fun getUserFeedbackForContent(
        contentId: UUID,
        userId: UUID,
        type: String
    ): ContentFeedback?
    
    /**
     * 获取内容的所有反馈
     */
    fun findFeedbacksByContent(
        contentId: UUID,
        pageRequest: PageRequest,
        type: String? = null
    ): PageResponse<ContentFeedback>
    
    /**
     * 获取用户的所有反馈
     */
    fun findFeedbacksByUser(
        userId: UUID,
        pageRequest: PageRequest,
        type: String? = null
    ): PageResponse<ContentFeedback>
    
    /**
     * 获取内容反馈统计信息
     */
    fun getContentFeedbackStats(contentId: UUID): Map<String, Any>
    
    /**
     * 批量获取多个内容的反馈统计信息
     */
    fun getBatchContentFeedbackStats(contentIds: List<UUID>): Map<UUID, Map<String, Any>>
    
    /**
     * 点赞内容（快捷方法）
     */
    fun likeContent(contentId: UUID, userId: UUID, isLiked: Boolean): ContentFeedback
    
    /**
     * 评分内容（快捷方法）
     */
    fun rateContent(contentId: UUID, userId: UUID, rating: Int): ContentFeedback
    
    /**
     * 标记内容是否有用（快捷方法）
     */
    fun markContentHelpfulness(contentId: UUID, userId: UUID, value: Int): ContentFeedback
} 