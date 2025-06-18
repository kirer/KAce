package com.github.kirer.kace.security

import org.slf4j.LoggerFactory
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 加密工具类
 * 用于敏感信息的加密和解密
 */
object EncryptionUtil {
    private val logger = LoggerFactory.getLogger(EncryptionUtil::class.java)
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_ALGORITHM = "AES"
    private const val GCM_TAG_LENGTH = 128
    private const val IV_LENGTH_BYTES = 12
    
    /**
     * 生成加密密钥
     * @param keySize 密钥大小，默认为256位
     * @return Base64编码的密钥字符串
     */
    fun generateKey(keySize: Int = 256): String {
        try {
            val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM)
            val secureRandom = SecureRandom()
            keyGenerator.init(keySize, secureRandom)
            val key = keyGenerator.generateKey()
            return Base64.getEncoder().encodeToString(key.encoded)
        } catch (e: Exception) {
            logger.error("生成加密密钥失败", e)
            throw RuntimeException("生成加密密钥失败", e)
        }
    }
    
    /**
     * 加密文本
     * @param plainText 明文
     * @param keyBase64 Base64编码的密钥
     * @return Base64编码的密文
     */
    fun encrypt(plainText: String, keyBase64: String): String {
        try {
            val key = decodeKey(keyBase64)
            
            // 生成随机IV
            val iv = ByteArray(IV_LENGTH_BYTES)
            SecureRandom().nextBytes(iv)
            
            // 初始化加密器
            val cipher = Cipher.getInstance(ALGORITHM)
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec)
            
            // 加密
            val encryptedBytes = cipher.doFinal(plainText.toByteArray())
            
            // 组合IV和密文
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            return Base64.getEncoder().encodeToString(combined)
        } catch (e: Exception) {
            logger.error("加密失败", e)
            throw RuntimeException("加密失败", e)
        }
    }
    
    /**
     * 解密文本
     * @param encryptedBase64 Base64编码的密文
     * @param keyBase64 Base64编码的密钥
     * @return 明文
     */
    fun decrypt(encryptedBase64: String, keyBase64: String): String {
        try {
            val key = decodeKey(keyBase64)
            
            // 解码密文
            val combined = Base64.getDecoder().decode(encryptedBase64)
            
            // 提取IV
            val iv = ByteArray(IV_LENGTH_BYTES)
            System.arraycopy(combined, 0, iv, 0, iv.size)
            
            // 提取加密数据
            val encryptedBytes = ByteArray(combined.size - iv.size)
            System.arraycopy(combined, iv.size, encryptedBytes, 0, encryptedBytes.size)
            
            // 初始化解密器
            val cipher = Cipher.getInstance(ALGORITHM)
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec)
            
            // 解密
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            
            return String(decryptedBytes)
        } catch (e: Exception) {
            logger.error("解密失败", e)
            throw RuntimeException("解密失败", e)
        }
    }
    
    /**
     * 从Base64字符串解码密钥
     */
    private fun decodeKey(keyBase64: String): SecretKey {
        val keyBytes = Base64.getDecoder().decode(keyBase64)
        return SecretKeySpec(keyBytes, KEY_ALGORITHM)
    }
    
    /**
     * 检查文本是否已加密
     * 简单检查是否是有效的Base64字符串，并且长度符合加密后的预期
     */
    fun isEncrypted(text: String): Boolean {
        return try {
            val decoded = Base64.getDecoder().decode(text)
            decoded.size > IV_LENGTH_BYTES
        } catch (e: Exception) {
            false
        }
    }
} 