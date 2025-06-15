package com.kace.user.api.response

import com.kace.user.domain.model.Permission

/**
 * 权限响应
 */
data class PermissionResponse(
    val id: String,
    val name: String,
    val code: String,
    val description: String? = null,
    val category: String,
    val isSystem: Boolean = true,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        /**
         * 从领域模型转换为响应模型
         */
        fun fromModel(permission: Permission): PermissionResponse {
            return PermissionResponse(
                id = permission.id,
                name = permission.name,
                code = permission.code,
                description = permission.description,
                category = permission.category,
                isSystem = permission.isSystem,
                createdAt = permission.createdAt.toString(),
                updatedAt = permission.updatedAt.toString()
            )
        }
    }
} 