package com.kace.common.model.dto

import kotlinx.serialization.Serializable

/**
 * 通用响应数据传输对象
 */
@Serializable
data class ResponseDto<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null
) {
    companion object {
        /**
         * 创建成功响应
         */
        fun <T> success(data: T? = null, message: String? = null): ResponseDto<T> = ResponseDto(
            success = true,
            data = data,
            message = message
        )
        
        /**
         * 创建错误响应
         */
        fun <T> error(message: String? = null, errorCode: String? = null): ResponseDto<T> = ResponseDto(
            success = false,
            data = null,
            message = message,
            errorCode = errorCode
        )
    }
} 