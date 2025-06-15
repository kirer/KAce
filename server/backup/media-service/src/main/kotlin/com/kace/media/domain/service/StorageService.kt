package com.kace.media.domain.service

import java.io.File
import java.io.InputStream
import java.util.UUID

/**
 * 存储服务接口
 */
interface StorageService {
    /**
     * 上传文件
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param contentType 内容类型
     * @param folder 存储文件夹（可选）
     * @return 文件的存储路径
     */
    suspend fun uploadFile(
        inputStream: InputStream,
        fileName: String,
        contentType: String,
        folder: String? = null
    ): String
    
    /**
     * 上传文件
     * @param file 文件对象
     * @param fileName 文件名（可选，默认使用文件原名）
     * @param contentType 内容类型（可选，默认自动检测）
     * @param folder 存储文件夹（可选）
     * @return 文件的存储路径
     */
    suspend fun uploadFile(
        file: File,
        fileName: String? = null,
        contentType: String? = null,
        folder: String? = null
    ): String
    
    /**
     * 获取文件
     * @param path 文件路径
     * @return 文件输入流
     */
    suspend fun getFile(path: String): InputStream
    
    /**
     * 删除文件
     * @param path 文件路径
     * @return 是否删除成功
     */
    suspend fun deleteFile(path: String): Boolean
    
    /**
     * 生成文件的访问URL
     * @param path 文件路径
     * @param expiryTimeSeconds URL有效期（秒），默认为1小时
     * @return 访问URL
     */
    suspend fun generateUrl(path: String, expiryTimeSeconds: Int = 3600): String
    
    /**
     * 生成永久访问URL
     * @param path 文件路径
     * @return 永久访问URL
     */
    suspend fun generatePermanentUrl(path: String): String
    
    /**
     * 检查文件是否存在
     * @param path 文件路径
     * @return 文件是否存在
     */
    suspend fun fileExists(path: String): Boolean
    
    /**
     * 获取文件大小
     * @param path 文件路径
     * @return 文件大小（字节）
     */
    suspend fun getFileSize(path: String): Long
    
    /**
     * 创建文件夹
     * @param folderPath 文件夹路径
     * @return 是否创建成功
     */
    suspend fun createFolder(folderPath: String): Boolean
    
    /**
     * 列出文件夹内容
     * @param folderPath 文件夹路径
     * @return 文件路径列表
     */
    suspend fun listFolder(folderPath: String): List<String>
    
    /**
     * 生成唯一的文件名
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    fun generateUniqueFileName(originalFileName: String): String {
        val extension = originalFileName.substringAfterLast(".", "")
        val baseName = if (extension.isNotEmpty()) {
            originalFileName.substringBeforeLast(".")
        } else {
            originalFileName
        }
        
        val uniqueId = UUID.randomUUID().toString()
        return if (extension.isNotEmpty()) {
            "$baseName-$uniqueId.$extension"
        } else {
            "$baseName-$uniqueId"
        }
    }
} 