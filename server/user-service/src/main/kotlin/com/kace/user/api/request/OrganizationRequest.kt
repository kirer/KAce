package com.kace.user.api.request

import kotlinx.serialization.Serializable

/**
 * 创建组织请求
 */
@Serializable
data class CreateOrganizationRequest(
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null
)

/**
 * 更新组织请求
 */
@Serializable
data class UpdateOrganizationRequest(
    val name: String? = null,
    val description: String? = null,
    val logoUrl: String? = null
)

/**
 * 更新组织状态请求
 */
@Serializable
data class UpdateOrganizationStatusRequest(
    val status: String
)

/**
 * 添加组织成员请求
 */
@Serializable
data class AddOrganizationMemberRequest(
    val userId: String,
    val role: String
)

/**
 * 更新组织成员角色请求
 */
@Serializable
data class UpdateOrganizationMemberRoleRequest(
    val role: String
) 