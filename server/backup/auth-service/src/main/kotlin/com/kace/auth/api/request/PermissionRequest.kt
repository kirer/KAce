package com.kace.auth.api.request

import kotlinx.serialization.Serializable

/**
 * 创建权限请求
 */
@Serializable
data class CreatePermissionRequest(
    val name: String,
    val description: String? = null,
    val resource: String,
    val action: String
)

/**
 * 更新权限请求
 */
@Serializable
data class UpdatePermissionRequest(
    val name: String,
    val description: String? = null,
    val resource: String,
    val action: String
) 