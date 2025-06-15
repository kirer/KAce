package com.kace.media.infrastructure.storage

import com.kace.media.domain.service.StorageService
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * 本地文件系统存储服务实现
 */
class LocalStorageService(private val config: ApplicationConfig) : StorageService {
    private val logger = LoggerFactory.getLogger(LocalStorageService::class.java)
    private val baseDirectory: String
    private val publicBaseUrl: String
    
    init {
        val storageConfig = config.config("storage.local")
        baseDirectory = storageConfig.property("directory").getString()
        publicBaseUrl = storageConfig.property("publicBaseUrl").getString()
        
        // 确保基础目录存在
        val baseDir = File(baseDirectory)
        if (!baseDir.exists()) {
            baseDir.mkdirs()
            logger.info("Created base directory: ${baseDir.absolutePath}")
        }
    }
    
    /**
     * 上传文件（从输入流）
     */
    override suspend fun uploadFile(
        inputStream: InputStream,
        fileName: String,
        contentType: String,
        folder: String?
    ): String = withContext(Dispatchers.IO) {
        val targetFolder = getTargetFolder(folder)
        val targetFile = createTargetFile(targetFolder, fileName)
        
        try {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            logger.info("File uploaded to: ${targetFile.absolutePath}")
            
            // 返回相对于基础目录的路径
            val relativePath = targetFile.absolutePath.substring(File(baseDirectory).absolutePath.length + 1)
            relativePath
        } catch (e: Exception) {
            logger.error("Failed to upload file", e)
            throw e
        }
    }
    
    /**
     * 上传文件（从文件对象）
     */
    override suspend fun uploadFile(
        file: File,
        fileName: String?,
        contentType: String?,
        folder: String?
    ): String = withContext(Dispatchers.IO) {
        val targetFolder = getTargetFolder(folder)
        val targetFile = createTargetFile(targetFolder, fileName ?: file.name)
        
        try {
            Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            logger.info("File uploaded to: ${targetFile.absolutePath}")
            
            // 返回相对于基础目录的路径
            val relativePath = targetFile.absolutePath.substring(File(baseDirectory).absolutePath.length + 1)
            relativePath
        } catch (e: Exception) {
            logger.error("Failed to upload file", e)
            throw e
        }
    }
    
    /**
     * 获取文件
     */
    override suspend fun getFile(path: String): InputStream = withContext(Dispatchers.IO) {
        val file = File(baseDirectory, path)
        if (!file.exists()) {
            logger.error("File not found: $path")
            throw FileNotFoundException("File not found: $path")
        }
        
        file.inputStream()
    }
    
    /**
     * 删除文件
     */
    override suspend fun deleteFile(path: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(baseDirectory, path)
        if (!file.exists()) {
            logger.warn("File not found for deletion: $path")
            return@withContext false
        }
        
        val deleted = file.delete()
        if (deleted) {
            logger.info("File deleted: $path")
        } else {
            logger.error("Failed to delete file: $path")
        }
        
        deleted
    }
    
    /**
     * 生成文件URL
     */
    override suspend fun getFileUrl(path: String): String {
        return "$publicBaseUrl/$path"
    }
    
    /**
     * 检查文件是否存在
     */
    override suspend fun fileExists(path: String): Boolean = withContext(Dispatchers.IO) {
        File(baseDirectory, path).exists()
    }
    
    /**
     * 获取目标文件夹，如果不存在则创建
     */
    private fun getTargetFolder(folder: String?): File {
        val dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val folderPath = if (folder != null) {
            Paths.get(baseDirectory, folder, dateFolder).toString()
        } else {
            Paths.get(baseDirectory, dateFolder).toString()
        }
        
        val targetFolder = File(folderPath)
        if (!targetFolder.exists()) {
            targetFolder.mkdirs()
        }
        
        return targetFolder
    }
    
    /**
     * 创建目标文件，确保文件名唯一
     */
    private fun createTargetFile(targetFolder: File, fileName: String): File {
        val extension = fileName.substringAfterLast('.', "")
        val baseName = fileName.substringBeforeLast('.', fileName)
        val uniqueFileName = if (extension.isNotEmpty()) {
            "${baseName}_${UUID.randomUUID()}.$extension"
        } else {
            "${baseName}_${UUID.randomUUID()}"
        }
        
        return File(targetFolder, uniqueFileName)
    }
} 