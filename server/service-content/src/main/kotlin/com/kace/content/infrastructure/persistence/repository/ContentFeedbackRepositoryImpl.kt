package com.kace.content.infrastructure.persistence.repository

import com.kace.common.api.PageRequest
import com.kace.common.api.PageResponse
import com.kace.content.domain.model.ContentFeedback
import com.kace.content.domain.repository.ContentFeedbackRepository
import com.kace.content.infrastructure.persistence.entity.ContentEntity
import com.kace.content.infrastructure.persistence.entity.ContentFeedbackEntity
import com.kace.content.infrastructure.persistence.entity.ContentFeedbacks
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

/**
 * 内容反馈仓库实现类
 */
class ContentFeedbackRepositoryImpl(private val database: Database) : ContentFeedbackRepository {
    
    override fun createFeedback(feedback: ContentFeedback): ContentFeedback = transaction(database) {
        val contentEntity = ContentEntity.findById(feedback.contentId)
            ?: throw IllegalArgumentException("内容不存在: ${feedback.contentId}")
        
        val existingFeedback = ContentFeedbackEntity.find {
            (ContentFeedbacks.contentId eq contentEntity.id) and
                    (ContentFeedbacks.userId eq feedback.userId) and
                    (ContentFeedbacks.type eq feedback.type)
        }.firstOrNull()
        
        if (existingFeedback != null) {
            existingFeedback.value = feedback.value
            existingFeedback.metadata = feedback.metadata
            existingFeedback.updatedAt = LocalDateTime.now()
            existingFeedback.toDomain()
        } else {
            ContentFeedbackEntity.new {
                this.contentId = contentEntity.id
                this.userId = feedback.userId
                this.type = feedback.type
                this.value = feedback.value
                this.metadata = feedback.metadata
                this.createdAt = feedback.createdAt
                this.updatedAt = feedback.updatedAt
            }.toDomain()
        }
    }
    
    override fun updateFeedback(feedback: ContentFeedback): ContentFeedback? = transaction(database) {
        val feedbackEntity = ContentFeedbackEntity.findById(feedback.id) ?: return@transaction null
        
        feedbackEntity.value = feedback.value
        feedbackEntity.metadata = feedback.metadata
        feedbackEntity.updatedAt = LocalDateTime.now()
        
        feedbackEntity.toDomain()
    }
    
    override fun findFeedbackById(id: UUID): ContentFeedback? = transaction(database) {
        ContentFeedbackEntity.findById(id)?.toDomain()
    }
    
    override fun deleteFeedback(id: UUID): Boolean = transaction(database) {
        val feedbackEntity = ContentFeedbackEntity.findById(id) ?: return@transaction false
        feedbackEntity.delete()
        true
    }
    
    override fun findUserFeedbackForContent(contentId: UUID, userId: UUID, type: String): ContentFeedback? = transaction(database) {
        val contentEntity = ContentEntity.findById(contentId) ?: return@transaction null
        
        ContentFeedbackEntity.find {
            (ContentFeedbacks.contentId eq contentEntity.id) and
                    (ContentFeedbacks.userId eq userId) and
                    (ContentFeedbacks.type eq type)
        }.firstOrNull()?.toDomain()
    }
    
    override fun findFeedbacksByContent(contentId: UUID, pageRequest: PageRequest, type: String?): PageResponse<ContentFeedback> = transaction(database) {
        val contentEntity = ContentEntity.findById(contentId) ?: return@transaction PageResponse.empty(pageRequest)
        
        var query = ContentFeedbackEntity.find { ContentFeedbacks.contentId eq contentEntity.id }
        
        if (type != null) {
            query = query.andWhere { ContentFeedbacks.type eq type }
        }
        
        val total = query.count()
        val items = query
            .orderBy(ContentFeedbacks.createdAt to SortOrder.DESC)
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
    
    override fun findFeedbacksByUser(userId: UUID, pageRequest: PageRequest, type: String?): PageResponse<ContentFeedback> = transaction(database) {
        var query = ContentFeedbackEntity.find { ContentFeedbacks.userId eq userId }
        
        if (type != null) {
            query = query.andWhere { ContentFeedbacks.type eq type }
        }
        
        val total = query.count()
        val items = query
            .orderBy(ContentFeedbacks.createdAt to SortOrder.DESC)
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
    
    override fun calculateContentFeedbackStats(contentId: UUID): Map<String, Any> = transaction(database) {
        val contentEntity = ContentEntity.findById(contentId) ?: return@transaction emptyMap<String, Any>()
        
        val allFeedbacks = ContentFeedbackEntity.find {
            ContentFeedbacks.contentId eq contentEntity.id
        }.map { it.toDomain() }
        
        val stats = mutableMapOf<String, Any>()
        
        // 点赞数量
        val likes = allFeedbacks.filter { it.type == ContentFeedback.TYPE_LIKE && it.value > 0 }.count()
        stats["likes"] = likes
        
        // 评分统计
        val ratings = allFeedbacks.filter { it.type == ContentFeedback.TYPE_RATING }
        if (ratings.isNotEmpty()) {
            val avgRating = ratings.map { it.value }.average()
            val ratingCount = ratings.size
            stats["rating_average"] = (avgRating * 10).roundToInt() / 10.0
            stats["rating_count"] = ratingCount
            
            // 评分分布
            val ratingDistribution = (1..5).associateWith { rating ->
                ratings.count { it.value == rating }
            }
            stats["rating_distribution"] = ratingDistribution
        }
        
        // 有用性统计
        val helpfulFeedbacks = allFeedbacks.filter { it.type == ContentFeedback.TYPE_HELPFUL }
        if (helpfulFeedbacks.isNotEmpty()) {
            val helpfulCount = helpfulFeedbacks.count { it.value > 0 }
            val notHelpfulCount = helpfulFeedbacks.count { it.value < 0 }
            stats["helpful_count"] = helpfulCount
            stats["not_helpful_count"] = notHelpfulCount
        }
        
        // 反应统计
        val reactions = allFeedbacks.filter { it.type == ContentFeedback.TYPE_REACTION }
        if (reactions.isNotEmpty()) {
            val reactionCounts = reactions.groupBy { it.value }
                .mapValues { it.value.size }
            stats["reactions"] = reactionCounts
        }
        
        stats
    }
    
    override fun calculateBatchContentFeedbackStats(contentIds: List<UUID>): Map<UUID, Map<String, Any>> = transaction(database) {
        val result = mutableMapOf<UUID, Map<String, Any>>()
        
        contentIds.forEach { contentId ->
            val stats = calculateContentFeedbackStats(contentId)
            if (stats.isNotEmpty()) {
                result[contentId] = stats
            }
        }
        
        result
    }
} 