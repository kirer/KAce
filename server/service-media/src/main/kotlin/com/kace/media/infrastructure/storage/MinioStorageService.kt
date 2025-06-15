package com.kace.media.infrastructure.storage

import com.kace.media.domain.service.StorageService
import io.ktor.server.config.*
import io.minio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * MinIO存储服务实现
 */
class MinioStorageService(private val config: ApplicationConfig) : StorageService {
    private val logger = LoggerFactory.getLogger(MinioStorageService::class.java)
    private val minioClient: MinioClient
    private val bucket: String
    private val publicBaseUrl: String
    
    init {
        val storageConfig = config.config("storage.minio")
        val endpoint = storageConfig.property("endpoint").getString()
        val accessKey = storageConfig.property("accessKey").getString()
        val secretKey = storageConfig.property("secretKey").getString()
        bucket = storageConfig.property("bucket").getString()
        publicBaseUrl = storageConfig.property("publicBaseUrl").getString()
        
        minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()
        
        // 确保存储桶存在
        try {
            val exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
                logger.info("Created bucket: $bucket")
            }
        } catch (e: Exception) {
            logger.error("Failed to initialize MinIO", e)
            throw e
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
        val objectName = generateObjectName(fileName, folder)
        
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(objectName)
                    .stream(inputStream, -1, 10485760) // 10MB
                    .contentType(contentType)
                    .build()
            )
            
            logger.info("File uploaded to MinIO: $objectName")
            objectName
        } catch (e: Exception) {
            logger.error("Failed to upload file to MinIO", e)
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
        val objectName = generateObjectName(fileName ?: file.name, folder)
        val fileContentType = contentType ?: "application/octet-stream"
        
        try {
            minioClient.uploadObject(
                UploadObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(objectName)
                    .filename(file.absolutePath)
                    .contentType(fileContentType)
                    .build()
            )
            
            logger.info("File uploaded to MinIO: $objectName")
            objectName
        } catch (e: Exception) {
            logger.error("Failed to upload file to MinIO", e)
            throw e
        }
    }
    
    /**
     * 获取文件
     */
    override suspend fun getFile(path: String): InputStream = withContext(Dispatchers.IO) {
        try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(path)
                    .build()
            )
        } catch (e: Exception) {
            logger.error("Failed to get file from MinIO: $path", e)
            throw FileNotFoundException("File not found: $path")
        }
    }
    
    /**
     * 删除文件
     */
    override suspend fun deleteFile(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(path)
                    .build()
            )
            
            logger.info("File deleted from MinIO: $path")
            true
        } catch (e: Exception) {
            logger.error("Failed to delete file from MinIO: $path", e)
            false
        }
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
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(path)
                    .build()
            )
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 生成对象名称
     */
    private fun generateObjectName(fileName: String, folder: String?): String {
        val dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val extension = fileName.substringAfterLast('.', "")
        val baseName = fileName.substringBeforeLast('.', fileName)
        val uniqueFileName = if (extension.isNotEmpty()) {
            "${baseName}_${UUID.randomUUID()}.$extension"
        } else {
            "${baseName}_${UUID.randomUUID()}"
        }
        
        return if (folder != null) {
            "$folder/$dateFolder/$uniqueFileName"
        } else {
            "$dateFolder/$uniqueFileName"
        }
    }
} 