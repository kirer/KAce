package com.kace.content.domain.service.impl

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentFeedback
import com.kace.content.domain.repository.ContentFeedbackRepository
import com.kace.content.domain.repository.ContentRepository
import com.kace.content.domain.service.ContentFeedbackService
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

/**
 * 内容反馈服务实现类
 */
class ContentFeedbackServiceImpl(
    private val contentFeedbackRepository: ContentFeedbackRepository,
    private val contentRepository: ContentRepository
) : ContentFeedbackService {
    
    private val logger = LoggerFactory.getLogger(ContentFeedbackServiceImpl::class.java)
    
    override fun createOrUpdateFeedback(
        contentId: UUID,
        userId: UUID,
        type: String,
        value: Int,
        metadata: Map<String, String>?
    ): ContentFeedback {
        // 检查内容是否存在
        val existingContent = contentRepository.findById(contentId)
            ?: throw IllegalArgumentException("内容不存在: $contentId")
        
        // 检查是否已经有该类型的反馈
        val existingFeedback = contentFeedbackRepository.findUserFeedbackForContent(
            contentId = contentId,
            userId = userId,
            type = type
        )
        
        val feedback = if (existingFeedback != null) {
            // 更新现有反馈
            existingFeedback.updateValue(value).let { updated ->
                metadata?.entries?.forEach { (key, value) ->
                    updated.addMetadata(key, value)
                }
                updated
            }
        } else {
            // 创建新反馈
            ContentFeedback(
                contentId = contentId,
                userId = userId,
                type = type,
                value = value,
                metadata = metadata,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
        
        // 验证反馈
        val validationErrors = feedback.validate()
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("反馈验证失败: ${validationErrors.joinToString(", ")}")
        }
        
        logger.info("创建/更新反馈: contentId=$contentId, userId=$userId, type=$type, value=$value")
        
        return contentFeedbackRepository.createFeedback(feedback)
    }
    
    override fun deleteFeedback(id: UUID, userId: UUID, isAdmin: Boolean): Boolean {
        // 查找反馈
        val feedback = contentFeedbackRepository.findFeedbackById(id) ?: return false
        
        // 检查权限
        if (!isAdmin && feedback.userId != userId) {
            throw IllegalStateException("您没有权限删除此反馈")
        }
        
        logger.info("删除反馈: id=$id, userId=$userId, isAdmin=$isAdmin")
        
        return contentFeedbackRepository.deleteFeedback(id)
    }
    
    override fun findFeedbackById(id: UUID): ContentFeedback? {
        return contentFeedbackRepository.findFeedbackById(id)
    }
    
    override fun getUserFeedbackForContent(contentId: UUID, userId: UUID, type: String): ContentFeedback? {
        return contentFeedbackRepository.findUserFeedbackForContent(
            contentId = contentId,
            userId = userId,
            type = type
        )
    }
    
    override fun findFeedbacksByContent(contentId: UUID, pageRequest: PageRequest, type: String?): PageResponse<ContentFeedback> {
        return contentFeedbackRepository.findFeedbacksByContent(contentId, pageRequest, type)
    }
    
    override fun findFeedbacksByUser(userId: UUID, pageRequest: PageRequest, type: String?): PageResponse<ContentFeedback> {
        return contentFeedbackRepository.findFeedbacksByUser(userId, pageRequest, type)
    }
    
    override fun getContentFeedbackStats(contentId: UUID): Map<String, Any> {
        return contentFeedbackRepository.calculateContentFeedbackStats(contentId)
    }
    
    override fun getBatchContentFeedbackStats(contentIds: List<UUID>): Map<UUID, Map<String, Any>> {
        return contentFeedbackRepository.calculateBatchContentFeedbackStats(contentIds)
    }
    
    override fun likeContent(contentId: UUID, userId: UUID, isLiked: Boolean): ContentFeedback {
        return createOrUpdateFeedback(
            contentId = contentId,
            userId = userId,
            type = ContentFeedback.TYPE_LIKE,
            value = if (isLiked) 1 else 0
        )
    }
    
    override fun rateContent(contentId: UUID, userId: UUID, rating: Int): ContentFeedback {
        if (rating !in 1..5) {
            throw IllegalArgumentException("评分必须在1到5之间")
        }
        
        return createOrUpdateFeedback(
            contentId = contentId,
            userId = userId,
            type = ContentFeedback.TYPE_RATING,
            value = rating
        )
    }
    
    override fun markContentHelpfulness(contentId: UUID, userId: UUID, value: Int): ContentFeedback {
        if (value !in -1..1) {
            throw IllegalArgumentException("有用值必须是-1、0或1")
        }
        
        return createOrUpdateFeedback(
            contentId = contentId,
            userId = userId,
            type = ContentFeedback.TYPE_HELPFUL,
            value = value
        )
    }
} 