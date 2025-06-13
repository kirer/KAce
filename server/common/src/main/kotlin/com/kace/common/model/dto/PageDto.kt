package com.kace.common.model.dto

import kotlinx.serialization.Serializable

/**
 * 分页请求参数
 */
@Serializable
data class PageRequest(
    val page: Int = 1,
    val size: Int = 20,
    val sort: String? = null,
    val direction: String? = null
)

/**
 * 分页响应数据
 */
@Serializable
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
) {
    companion object {
        /**
         * 创建空的分页响应
         */
        fun <T> empty(): PageResponse<T> = PageResponse(
            content = emptyList(),
            page = 0,
            size = 0,
            totalElements = 0,
            totalPages = 0,
            first = true,
            last = true,
            empty = true
        )
        
        /**
         * 从内容列表和分页信息创建分页响应
         */
        fun <T> of(
            content: List<T>,
            page: Int,
            size: Int,
            totalElements: Long
        ): PageResponse<T> {
            val totalPages = if (size > 0) Math.ceil(totalElements.toDouble() / size).toInt() else 0
            val isFirst = page <= 1
            val isLast = page >= totalPages
            val isEmpty = content.isEmpty()
            
            return PageResponse(
                content = content,
                page = page,
                size = size,
                totalElements = totalElements,
                totalPages = totalPages,
                first = isFirst,
                last = isLast,
                empty = isEmpty
            )
        }
    }
} 