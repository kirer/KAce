package com.kace.content.domain.service.impl

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentComment
import com.kace.content.domain.repository.ContentCommentRepository
import com.kace.content.domain.repository.ContentRepository
import com.kace.content.domain.service.ContentCommentService
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

/**
 * 内容评论服务实现类
 */
class ContentCommentServiceImpl(
    private val contentCommentRepository: ContentCommentRepository,
    private val contentRepository: ContentRepository
) : ContentCommentService {
    
    private val logger = LoggerFactory.getLogger(ContentCommentServiceImpl::class.java)
    
    override fun createComment(
        contentId: UUID,
        userId: UUID,
        content: String,
        parentId: UUID?,
        metadata: Map<String, String>?
    ): ContentComment {
        // 检查内容是否存在
        val existingContent = contentRepository.findById(contentId)
            ?: throw IllegalArgumentException("内容不存在: $contentId")
        
        // 如果指定了父评论ID，检查父评论是否存在
        if (parentId != null) {
            val parentComment = contentCommentRepository.findCommentById(parentId)
                ?: throw IllegalArgumentException("父评论不存在: $parentId")
            
            // 检查父评论是否属于同一个内容
            if (parentComment.contentId != contentId) {
                throw IllegalArgumentException("父评论不属于指定的内容")
            }
            
            // 不允许嵌套回复（只支持一级回复）
            if (parentComment.parentId != null) {
                throw IllegalArgumentException("不支持嵌套回复")
            }
        }
        
        // 创建评论对象
        val comment = ContentComment(
            contentId = contentId,
            userId = userId,
            parentId = parentId,
            content = content.trim(),
            status = ContentComment.STATUS_PUBLISHED,
            metadata = metadata,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 验证评论
        val validationErrors = comment.validate()
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("评论验证失败: ${validationErrors.joinToString(", ")}")
        }
        
        logger.info("创建评论: contentId=$contentId, userId=$userId, parentId=$parentId")
        
        // 保存评论
        return contentCommentRepository.createComment(comment)
    }
    
    override fun updateComment(id: UUID, userId: UUID, content: String): ContentComment? {
        // 查找评论
        val comment = contentCommentRepository.findCommentById(id)
            ?: return null
        
        // 检查是否是评论的创建者
        if (comment.userId != userId) {
            throw IllegalStateException("您没有权限编辑此评论")
        }
        
        // 修改评论内容
        val updatedComment = comment.edit(content.trim())
        
        // 验证评论
        val validationErrors = updatedComment.validate()
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("评论验证失败: ${validationErrors.joinToString(", ")}")
        }
        
        logger.info("更新评论: id=$id, userId=$userId")
        
        // 保存更新后的评论
        return contentCommentRepository.updateComment(updatedComment)
    }
    
    override fun updateCommentStatus(id: UUID, status: String): ContentComment? {
        // 查找评论
        val comment = contentCommentRepository.findCommentById(id)
            ?: return null
        
        // 更新状态
        val updatedComment = comment.updateStatus(status)
        
        logger.info("更新评论状态: id=$id, status=$status")
        
        // 保存更新后的评论
        return contentCommentRepository.updateComment(updatedComment)
    }
    
    override fun deleteComment(id: UUID, userId: UUID, isAdmin: Boolean): Boolean {
        // 查找评论
        val comment = contentCommentRepository.findCommentById(id) ?: return false
        
        // 检查权限
        if (!isAdmin && comment.userId != userId) {
            throw IllegalStateException("您没有权限删除此评论")
        }
        
        logger.info("删除评论: id=$id, userId=$userId, isAdmin=$isAdmin")
        
        // 逻辑删除（标记为已删除）
        val updatedComment = comment.markAsDeleted()
        contentCommentRepository.updateComment(updatedComment)
        
        return true
    }
    
    override fun findCommentById(id: UUID): ContentComment? {
        return contentCommentRepository.findCommentById(id)
    }
    
    override fun findCommentsByContent(
        contentId: UUID,
        pageRequest: PageRequest,
        parentId: UUID?,
        status: String?
    ): PageResponse<ContentComment> {
        return contentCommentRepository.findCommentsByContent(
            contentId = contentId,
            pageRequest = pageRequest,
            parentId = parentId,
            status = status
        )
    }
    
    override fun findCommentsByUser(
        userId: UUID,
        pageRequest: PageRequest,
        status: String?
    ): PageResponse<ContentComment> {
        return contentCommentRepository.findCommentsByUser(
            userId = userId,
            pageRequest = pageRequest,
            status = status
        )
    }
    
    override fun findReplies(
        commentId: UUID,
        pageRequest: PageRequest,
        status: String?
    ): PageResponse<ContentComment> {
        return contentCommentRepository.findReplies(
            commentId = commentId,
            pageRequest = pageRequest,
            status = status
        )
    }
    
    override fun batchUpdateStatus(ids: List<UUID>, status: String): Int {
        logger.info("批量更新评论状态: ids=${ids.size}, status=$status")
        return contentCommentRepository.updateCommentsStatus(ids, status)
    }
    
    override fun getRecentComments(
        pageRequest: PageRequest,
        status: String?
    ): PageResponse<ContentComment> {
        return contentCommentRepository.getRecentComments(pageRequest, status)
    }
    
    override fun countCommentsByContent(contentId: UUID, status: String?): Long {
        return contentCommentRepository.countCommentsByContent(contentId, status)
    }
} 