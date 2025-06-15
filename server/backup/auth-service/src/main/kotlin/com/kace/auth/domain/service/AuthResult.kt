package com.kace.auth.domain.service

import com.kace.auth.domain.model.User

/**
 * 认证结果
 */
data class AuthResult(
    val user: User,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
) 