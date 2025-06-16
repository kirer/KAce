package com.kace.common.security.crypto

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * 密码编码器
 */
object PasswordEncoder {
    private const val DEFAULT_COST = 12
    
    /**
     * 对密码进行哈希处理
     */
    fun encode(password: String, cost: Int = DEFAULT_COST): String {
        return BCrypt.withDefaults().hashToString(cost, password.toCharArray())
    }
    
    /**
     * 验证密码
     */
    fun verify(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }
} 