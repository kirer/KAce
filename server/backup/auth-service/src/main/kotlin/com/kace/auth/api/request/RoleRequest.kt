package com.kace.auth.api.request

import kotlinx.serialization.Serializable

/**
 * 创建角色请求
 */
@Serializable
data class CreateRoleRequest(
    val name: String,
    val description: String? = null,
    val permissionIds: List<String>? = null
)

/**
 * 更新角色请求
 */
@Serializable
data class UpdateRoleRequest(
    val name: String,
    val description: String? = null,
    val permissionIds: List<String>? = null
) 