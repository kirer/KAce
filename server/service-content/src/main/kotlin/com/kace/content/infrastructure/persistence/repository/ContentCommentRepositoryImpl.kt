package com.kace.content.infrastructure.persistence.repository

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentComment
import com.kace.content.domain.repository.ContentCommentRepository
import com.kace.content.infrastructure.persistence.entity.ContentCommentEntity
import com.kace.content.infrastructure.persistence.entity.ContentComments
import com.kace.content.infrastructure.persistence.entity.ContentEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

/**
 * 内容评论仓库实现类
 */
class ContentCommentRepositoryImpl(private val database: Database) : ContentCommentRepository {
    
    override fun createComment(comment: ContentComment): ContentComment = transaction(database) {
        val contentEntity = ContentEntity.findById(comment.contentId)
            ?: throw IllegalArgumentException("内容不存在: ${comment.contentId}")
        
        val commentEntity = ContentCommentEntity.new {
            this.contentId = contentEntity.id
            this.userId = comment.userId
            this.parentId = comment.parentId
            this.content = comment.content
            this.status = comment.status
            this.metadata = comment.metadata
            this.createdAt = comment.createdAt
            this.updatedAt = comment.updatedAt
        }
        
        commentEntity.toDomain()
    }
    
    override fun updateComment(comment: ContentComment): ContentComment? = transaction(database) {
        val commentEntity = ContentCommentEntity.findById(comment.id) ?: return@transaction null
        
        commentEntity.content = comment.content
        commentEntity.status = comment.status
        commentEntity.metadata = comment.metadata
        commentEntity.updatedAt = LocalDateTime.now()
        
        commentEntity.toDomain()
    }
    
    override fun findCommentById(id: UUID): ContentComment? = transaction(database) {
        ContentCommentEntity.findById(id)?.toDomain()
    }
    
    override fun deleteComment(id: UUID): Boolean = transaction(database) {
        ContentCommentEntity.findById(id)?.let {
            it.delete()
            true
        } ?: false
    }
    
    override fun findCommentsByContent(
        contentId: UUID,
        pageRequest: PageRequest,
        parentId: UUID?,
        status: String?
    ): PageResponse<ContentComment> = transaction(database) {
        // 构建查询条件
        var query = ContentCommentEntity.find { ContentComments.contentId eq contentId }
        
        if (parentId != null) {
            query = query.andWhere { ContentComments.parentId eq parentId }
        } else {
            query = query.andWhere { ContentComments.parentId.isNull() }
        }
        
        if (status != null) {
            query = query.andWhere { ContentComments.status eq status }
        }
        
        // 计算总数
        val total = query.count()
        
        // 分页查询
        val items = query
            .orderBy(ContentComments.createdAt to SortOrder.DESC)
            .limit(pageRequest.size, pageRequest.page * pageRequest.size)
            .map { it.toDomain() }
        
        PageResponse(
            items = items,
            page = pageRequest.page,
            size = pageRequest.size,
            total = total,
            totalPages = (total + pageRequest.size - 1) / pageRequest.size
        )
    }
    
    override fun findCommentsByUser(
        userId: UUID,
        pageRequest: PageRequest,
        status: String?
    ): PageResponse<ContentComment> = transaction(database) {
        // 构建查询条件
        var query = ContentCommentEntity.find { ContentComments.userId eq userId }
        
        if (status != null) {
            query = query.andWhere { ContentComments.status eq status }
        }
        
        // 计算总数
        val total = query.count()
        
        // 分页查询
        val items = query
            .orderBy(ContentComments.createdAt to SortOrder.DESC)
            .limit(pageRequest.size, pageRequest.page * pageRequest.size)
            .map { it.toDomain() }
        
        PageResponse(
            items = items,
            page = pageRequest.page,
            size = pageRequest.size,
            total = total,
            totalPages = (total + pageRequest.size - 1) / pageRequest.size
        )
    }
    
    override fun findReplies(
        commentId: UUID,
        pageRequest: PageRequest,
        status: String?
    ): PageResponse<ContentComment> = transaction(database) {
        // 构建查询条件
        var query = ContentCommentEntity.find { ContentComments.parentId eq commentId }
        
        if (status != null) {
            query = query.andWhere { ContentComments.status eq status }
        }
        
        // 计算总数
        val total = query.count()
        
        // 分页查询
        val items = query
            .orderBy(ContentComments.createdAt to SortOrder.ASC) // 回复按时间正序排列
            .limit(pageRequest.size, pageRequest.page * pageRequest.size)
            .map { it.toDomain() }
        
        PageResponse(
            items = items,
            page = pageRequest.page,
            size = pageRequest.size,
            total = total,
            totalPages = (total + pageRequest.size - 1) / pageRequest.size
        )
    }
    
    override fun countCommentsByContent(contentId: UUID, status: String?): Long = transaction(database) {
        var query = ContentCommentEntity.find { ContentComments.contentId eq contentId }
        
        if (status != null) {
            query = query.andWhere { ContentComments.status eq status }
        }
        
        query.count()
    }
    
    override fun countCommentsByUser(userId: UUID, status: String?): Long = transaction(database) {
        var query = ContentCommentEntity.find { ContentComments.userId eq userId }
        
        if (status != null) {
            query = query.andWhere { ContentComments.status eq status }
        }
        
        query.count()
    }
    
    override fun updateCommentsStatus(ids: List<UUID>, status: String): Int = transaction(database) {
        val updatedCount = ContentCommentEntity
            .find { ContentComments.id inList ids }
            .count { entity ->
                entity.status = status
                entity.updatedAt = LocalDateTime.now()
                true
            }
        
        updatedCount.toInt()
    }
    
    override fun getRecentComments(pageRequest: PageRequest, status: String?): PageResponse<ContentComment> = transaction(database) {
        // 构建查询条件
        var query = ContentCommentEntity.all()
        
        if (status != null) {
            query = query.andWhere { ContentComments.status eq status }
        }
        
        // 计算总数
        val total = query.count()
        
        // 分页查询
        val items = query
            .orderBy(ContentComments.createdAt to SortOrder.DESC)
            .limit(pageRequest.size, pageRequest.page * pageRequest.size)
            .map { it.toDomain() }
        
        PageResponse(
            items = items,
            page = pageRequest.page,
            size = pageRequest.size,
            total = total,
            totalPages = (total + pageRequest.size - 1) / pageRequest.size
        )
    }
} 