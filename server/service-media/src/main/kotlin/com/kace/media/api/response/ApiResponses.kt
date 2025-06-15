package com.kace.media.api.response

import kotlinx.serialization.Serializable

/**
 * API响应基类
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorResponse? = null
)

/**
 * 错误响应
 */
@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)

/**
 * 分页响应
 */
@Serializable
data class PagedResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val total: Int,
    val totalPages: Int
)

/**
 * 成功响应
 */
@Serializable
data class SuccessResponse(
    val message: String
)

/**
 * 创建成功响应
 */
fun <T> success(data: T): ApiResponse<T> {
    return ApiResponse(success = true, data = data)
}

/**
 * 创建成功消息响应
 */
fun successMessage(message: String): ApiResponse<SuccessResponse> {
    return ApiResponse(success = true, data = SuccessResponse(message))
}

/**
 * 创建错误响应
 */
fun error(code: String, message: String, details: Map<String, String>? = null): ApiResponse<Nothing> {
    return ApiResponse(success = false, error = ErrorResponse(code, message, details))
}

/**
 * 创建分页响应
 */
fun <T> paged(items: List<T>, page: Int, size: Int, total: Int): ApiResponse<PagedResponse<T>> {
    val totalPages = if (size > 0) (total + size - 1) / size else 0
    return success(PagedResponse(items, page, size, total, totalPages))
} 