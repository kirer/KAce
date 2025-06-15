package com.kace.content.domain.repository

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentFeedback
import java.util.*

/**
 * 内容反馈仓库接口
 */
interface ContentFeedbackRepository {
    /**
     * 创建反馈
     */
    fun createFeedback(feedback: ContentFeedback): ContentFeedback
    
    /**
     * 更新反馈
     */
    fun updateFeedback(feedback: ContentFeedback): ContentFeedback?
    
    /**
     * 根据ID查找反馈
     */
    fun findFeedbackById(id: UUID): ContentFeedback?
    
    /**
     * 删除反馈
     */
    fun deleteFeedback(id: UUID): Boolean
    
    /**
     * 查找用户对内容的特定类型反馈
     */
    fun findUserFeedbackForContent(
        contentId: UUID,
        userId: UUID,
        type: String
    ): ContentFeedback?
    
    /**
     * 分页查找内容的所有反馈
     */
    fun findFeedbacksByContent(
        contentId: UUID,
        pageRequest: PageRequest,
        type: String? = null
    ): PageResponse<ContentFeedback>
    
    /**
     * 分页查找用户的所有反馈
     */
    fun findFeedbacksByUser(
        userId: UUID,
        pageRequest: PageRequest,
        type: String? = null
    ): PageResponse<ContentFeedback>
    
    /**
     * 计算内容反馈统计信息
     */
    fun calculateContentFeedbackStats(contentId: UUID): Map<String, Any>
    
    /**
     * 批量计算多个内容的反馈统计信息
     */
    fun calculateBatchContentFeedbackStats(contentIds: List<UUID>): Map<UUID, Map<String, Any>>
} 