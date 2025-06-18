package com.github.kirer.kace.util

import com.github.kirer.kace.config.EncryptedConfig
import com.github.kirer.kace.security.EncryptionUtil
import java.io.File
import java.util.*

/**
 * 配置加密工具
 * 用于加密配置文件中的敏感信息
 */
object ConfigEncryptionTool {
    private const val CONFIG_KEY_ENV = "KACE_CONFIG_ENCRYPTION_KEY"
    
    @JvmStatic
    fun main(args: Array<String>) {
        println("KAce 配置加密工具")
        println("=================")
        
        // 获取加密密钥
        val encryptionKey = getOrCreateEncryptionKey()
        
        // 命令行菜单
        var running = true
        while (running) {
            println("\n请选择操作:")
            println("1. 加密配置值")
            println("2. 解密配置值")
            println("3. 生成新的加密密钥")
            println("4. 退出")
            
            print("\n请输入选项 [1-4]: ")
            when (readLine()?.trim()) {
                "1" -> encryptValue(encryptionKey)
                "2" -> decryptValue(encryptionKey)
                "3" -> generateNewKey()
                "4" -> running = false
                else -> println("无效的选项，请重新输入")
            }
        }
        
        println("\n感谢使用 KAce 配置加密工具")
    }
    
    /**
     * 获取或创建加密密钥
     */
    private fun getOrCreateEncryptionKey(): String {
        // 首先尝试从环境变量获取
        var key = System.getenv(CONFIG_KEY_ENV)
        
        // 如果环境变量中没有，尝试从密钥文件获取
        if (key == null) {
            val keyFile = File("config-encryption.key")
            if (keyFile.exists()) {
                key = keyFile.readText().trim()
            }
        }
        
        // 如果仍然没有，生成新密钥
        if (key == null) {
            key = generateNewKey()
        }
        
        return key
    }
    
    /**
     * 生成新的加密密钥
     */
    private fun generateNewKey(): String {
        val key = EncryptionUtil.generateKey()
        println("\n已生成新的加密密钥: $key")
        println("请将此密钥保存在安全的地方，或设置为环境变量 $CONFIG_KEY_ENV")
        
        print("\n是否保存到本地文件? (y/n): ")
        if (readLine()?.trim()?.lowercase() == "y") {
            val keyFile = File("config-encryption.key")
            keyFile.writeText(key)
            println("密钥已保存到文件: ${keyFile.absolutePath}")
        }
        
        return key
    }
    
    /**
     * 加密配置值
     */
    private fun encryptValue(encryptionKey: String) {
        print("\n请输入要加密的配置值: ")
        val value = readLine()?.trim() ?: return
        
        if (value.isEmpty()) {
            println("值不能为空")
            return
        }
        
        try {
            val encrypted = EncryptedConfig.encryptValue(value, encryptionKey)
            println("\n加密结果:")
            println(encrypted)
            println("\n将此值放入配置文件中即可")
        } catch (e: Exception) {
            println("\n加密失败: ${e.message}")
        }
    }
    
    /**
     * 解密配置值
     */
    private fun decryptValue(encryptionKey: String) {
        print("\n请输入要解密的配置值 (以 ENC: 开头): ")
        val value = readLine()?.trim() ?: return
        
        if (!value.startsWith("ENC:")) {
            println("无效的加密值，必须以 ENC: 开头")
            return
        }
        
        try {
            val encryptedValue = value.substring(4) // 去除 "ENC:" 前缀
            val decrypted = EncryptionUtil.decrypt(encryptedValue, encryptionKey)
            println("\n解密结果:")
            println(decrypted)
        } catch (e: Exception) {
            println("\n解密失败: ${e.message}")
        }
    }
} 