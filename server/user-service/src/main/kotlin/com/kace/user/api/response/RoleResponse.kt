package com.kace.user.api.response

import com.kace.user.domain.model.Role

/**
 * 角色响应
 */
data class RoleResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val permissions: List<String> = emptyList(),
    val isSystem: Boolean = false,
    val organizationId: String? = null,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        /**
         * 从领域模型转换为响应模型
         */
        fun fromModel(role: Role): RoleResponse {
            return RoleResponse(
                id = role.id,
                name = role.name,
                description = role.description,
                permissions = role.permissions,
                isSystem = role.isSystem,
                organizationId = role.organizationId,
                createdAt = role.createdAt.toString(),
                updatedAt = role.updatedAt.toString()
            )
        }
    }
}

/**
 * 用户ID响应
 */
data class UserIdResponse(
    val userId: String
) 