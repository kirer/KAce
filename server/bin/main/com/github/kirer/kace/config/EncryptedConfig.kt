package com.github.kirer.kace.config

import com.github.kirer.kace.security.EncryptionUtil
import com.typesafe.config.Config
import org.slf4j.LoggerFactory

/**
 * 加密配置工具类
 * 用于处理加密的配置项
 */
object EncryptedConfig {
    private val logger = LoggerFactory.getLogger(EncryptedConfig::class.java)
    private const val ENCRYPTED_PREFIX = "ENC:"
    
    /**
     * 获取可能加密的字符串配置
     * @param config 配置对象
     * @param path 配置路径
     * @param encryptionKey 加密密钥
     * @return 解密后的字符串
     */
    fun getString(config: Config, path: String, encryptionKey: String?): String {
        val value = config.getString(path)
        
        if (encryptionKey != null && value.startsWith(ENCRYPTED_PREFIX)) {
            return try {
                val encryptedValue = value.substring(ENCRYPTED_PREFIX.length)
                EncryptionUtil.decrypt(encryptedValue, encryptionKey)
            } catch (e: Exception) {
                logger.error("解密配置项失败: $path", e)
                throw RuntimeException("解密配置项失败: $path", e)
            }
        }
        
        return value
    }
    
    /**
     * 加密配置值
     * @param value 明文值
     * @param encryptionKey 加密密钥
     * @return 加密后的字符串，格式为 "ENC:加密内容"
     */
    fun encryptValue(value: String, encryptionKey: String): String {
        return try {
            val encryptedValue = EncryptionUtil.encrypt(value, encryptionKey)
            "$ENCRYPTED_PREFIX$encryptedValue"
        } catch (e: Exception) {
            logger.error("加密配置值失败", e)
            throw RuntimeException("加密配置值失败", e)
        }
    }
    
    /**
     * 检查值是否已加密
     * @param value 要检查的值
     * @return 是否已加密
     */
    fun isEncrypted(value: String): Boolean {
        return value.startsWith(ENCRYPTED_PREFIX)
    }
} 