package com.kace.user.api.request

import kotlinx.serialization.Serializable

/**
 * 创建组织请求
 */
@Serializable
data class CreateOrganizationRequest(
    val name: String,
    val description: String? = null,
    val parentId: String? = null
)

/**
 * 更新组织请求
 */
@Serializable
data class UpdateOrganizationRequest(
    val name: String? = null,
    val description: String? = null,
    val parentId: String? = null
) 