package com.kace.user.api.request

import kotlinx.serialization.Serializable

/**
 * 创建角色请求
 */
@Serializable
data class CreateRoleRequest(
    val name: String,
    val description: String? = null,
    val permissions: List<String>? = null
)

/**
 * 更新角色请求
 */
@Serializable
data class UpdateRoleRequest(
    val name: String? = null,
    val description: String? = null,
    val permissions: List<String>? = null
) 