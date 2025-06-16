package com.kace.user.infrastructure.security

import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component

/**
 * 密码编码器接口
 */
interface PasswordEncoder {
    /**
     * 对密码进行编码
     *
     * @param rawPassword 原始密码
     * @return 编码后的密码
     */
    fun encode(rawPassword: String): String
    
    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 编码后的密码
     * @return 如果密码匹配返回true，否则返回false
     */
    fun matches(rawPassword: String, encodedPassword: String): Boolean
}

/**
 * BCrypt密码编码器实现
 */
@Component
class BCryptPasswordEncoder : PasswordEncoder {
    
    /**
     * 使用BCrypt对密码进行编码
     */
    override fun encode(rawPassword: String): String {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt())
    }
    
    /**
     * 使用BCrypt验证密码
     */
    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, encodedPassword)
    }
} 