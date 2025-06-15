package com.kace.user.api.request

/**
 * 角色创建请求
 */
data class RoleCreateRequest(
    val name: String,
    val description: String? = null,
    val permissions: List<String> = emptyList(),
    val organizationId: String? = null
)

/**
 * 角色更新请求
 */
data class RoleUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val permissions: List<String>? = null
)

/**
 * 角色分配请求
 */
data class AssignRoleRequest(
    val userId: String
) 