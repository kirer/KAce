package com.kace.common.util

import java.security.MessageDigest
import java.util.*

/**
 * 字符串工具类
 */
object StringUtils {
    
    /**
     * 判断字符串是否为空
     */
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }
    
    /**
     * 判断字符串是否为空或空白
     */
    fun isBlank(str: String?): Boolean {
        return str == null || str.isBlank()
    }
    
    /**
     * 判断字符串是否不为空
     */
    fun isNotEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }
    
    /**
     * 判断字符串是否不为空或空白
     */
    fun isNotBlank(str: String?): Boolean {
        return !isBlank(str)
    }
    
    /**
     * 截取字符串
     */
    fun truncate(str: String?, maxLength: Int): String {
        if (str == null) return ""
        return if (str.length <= maxLength) str else str.substring(0, maxLength) + "..."
    }
    
    /**
     * 生成随机字符串
     */
    fun randomString(length: Int): String {
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { Random().nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
    
    /**
     * 生成UUID
     */
    fun uuid(): String {
        return UUID.randomUUID().toString()
    }
    
    /**
     * 计算MD5哈希
     */
    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 转换为驼峰命名
     */
    fun toCamelCase(str: String): String {
        return str.split('_').joinToString("") { 
            it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() }
        }.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }
    
    /**
     * 转换为帕斯卡命名
     */
    fun toPascalCase(str: String): String {
        return str.split('_').joinToString("") { 
            it.replaceFirstChar { char -> char.titlecase(Locale.getDefault()) }
        }
    }
    
    /**
     * 转换为下划线命名
     */
    fun toSnakeCase(str: String): String {
        return str.replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase(Locale.getDefault())
    }
} 