package com.kace.user.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 组织响应
 */
@Serializable
data class OrganizationResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val status: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 组织成员响应
 */
@Serializable
data class OrganizationMemberResponse(
    val id: String,
    val organizationId: String,
    val userId: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String? = null,
    val role: String,
    val createdAt: Instant,
    val updatedAt: Instant
) 