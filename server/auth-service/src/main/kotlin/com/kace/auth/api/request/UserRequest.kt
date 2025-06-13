package com.kace.auth.api.request

import kotlinx.serialization.Serializable

/**
 * 创建用户请求
 */
@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val roleIds: List<String>? = null
)

/**
 * 更新用户请求
 */
@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val active: Boolean? = null,
    val verified: Boolean? = null,
    val roleIds: List<String>? = null
) 