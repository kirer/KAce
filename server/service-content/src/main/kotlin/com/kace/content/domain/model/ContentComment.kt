package com.kace.content.domain.model

import java.time.LocalDateTime
import java.util.*

/**
 * 内容评论领域模型
 */
data class ContentComment(
    val id: UUID = UUID.randomUUID(),
    val contentId: UUID,
    val userId: UUID,
    val parentId: UUID? = null,
    val content: String,
    val status: String = "PUBLISHED",
    val metadata: Map<String, String>? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_PUBLISHED = "PUBLISHED"
        const val STATUS_REJECTED = "REJECTED"
        const val STATUS_HIDDEN = "HIDDEN"
        const val STATUS_DELETED = "DELETED"
    }
    
    /**
     * 验证评论内容
     */
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (content.isBlank()) {
            errors.add("评论内容不能为空")
        }
        
        if (content.length > 5000) {
            errors.add("评论内容不能超过5000个字符")
        }
        
        return errors
    }
    
    /**
     * 更新评论状态
     */
    fun updateStatus(newStatus: String): ContentComment {
        return copy(
            status = newStatus,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 编辑评论内容
     */
    fun edit(newContent: String): ContentComment {
        return copy(
            content = newContent,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 添加元数据
     */
    fun addMetadata(key: String, value: String): ContentComment {
        val updatedMetadata = metadata?.toMutableMap() ?: mutableMapOf()
        updatedMetadata[key] = value
        
        return copy(
            metadata = updatedMetadata,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 删除评论（标记为删除状态）
     */
    fun markAsDeleted(): ContentComment {
        return updateStatus(STATUS_DELETED)
    }
    
    /**
     * 隐藏评论
     */
    fun hide(): ContentComment {
        return updateStatus(STATUS_HIDDEN)
    }
    
    /**
     * 发布评论
     */
    fun publish(): ContentComment {
        return updateStatus(STATUS_PUBLISHED)
    }
} 