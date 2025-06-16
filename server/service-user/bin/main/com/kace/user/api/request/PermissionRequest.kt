package com.kace.user.api.request

/**
 * 权限创建请求
 */
data class PermissionCreateRequest(
    val name: String,
    val code: String,
    val description: String? = null,
    val category: String
)

/**
 * 权限更新请求
 */
data class PermissionUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val category: String? = null
) 