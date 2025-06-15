package com.kace.content.domain.model

import java.time.LocalDateTime
import java.util.*

/**
 * 内容反馈领域模型
 */
data class ContentFeedback(
    val id: UUID = UUID.randomUUID(),
    val contentId: UUID,
    val userId: UUID,
    val type: String,
    val value: Int,
    val metadata: Map<String, String>? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val TYPE_LIKE = "LIKE"
        const val TYPE_RATING = "RATING"
        const val TYPE_HELPFUL = "HELPFUL"
        const val TYPE_REACTION = "REACTION"
    }
    
    /**
     * 验证反馈
     */
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (type !in listOf(TYPE_LIKE, TYPE_RATING, TYPE_HELPFUL, TYPE_REACTION)) {
            errors.add("反馈类型无效")
        }
        
        when (type) {
            TYPE_LIKE -> {
                if (value !in 0..1) {
                    errors.add("点赞值必须是0或1")
                }
            }
            TYPE_RATING -> {
                if (value !in 1..5) {
                    errors.add("评分值必须是1到5之间")
                }
            }
            TYPE_HELPFUL -> {
                if (value !in -1..1) {
                    errors.add("有用值必须是-1、0或1")
                }
            }
            TYPE_REACTION -> {
                if (value < 0) {
                    errors.add("反应值必须是非负整数")
                }
            }
        }
        
        return errors
    }
    
    /**
     * 更新反馈值
     */
    fun updateValue(newValue: Int): ContentFeedback {
        return copy(
            value = newValue,
            updatedAt = LocalDateTime.now()
        )
    }
    
    /**
     * 添加元数据
     */
    fun addMetadata(key: String, value: String): ContentFeedback {
        val updatedMetadata = metadata?.toMutableMap() ?: mutableMapOf()
        updatedMetadata[key] = value
        
        return copy(
            metadata = updatedMetadata,
            updatedAt = LocalDateTime.now()
        )
    }
} 