package com.kace.media.infrastructure.config

import io.ktor.server.application.*
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import java.io.File

/**
 * 配置存储服务
 */
fun Application.configureStorage() {
    val config = environment.config.config("storage")
    val storageType = config.property("type").getString()
    
    when (storageType) {
        "minio" -> configureMinioStorage(config.config("minio"))
        "local" -> configureLocalStorage(config.config("local"))
    }
}

/**
 * 配置MinIO存储
 */
private fun Application.configureMinioStorage(config: ApplicationConfig) {
    val endpoint = config.property("endpoint").getString()
    val accessKey = config.property("accessKey").getString()
    val secretKey = config.property("secretKey").getString()
    val bucket = config.property("bucket").getString()
    val region = config.propertyOrNull("region")?.getString() ?: ""
    
    // 创建MinIO客户端
    val minioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build()
    
    // 确保存储桶存在
    val bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())
    if (!bucketExists) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).region(region).build())
        log.info("Created MinIO bucket: $bucket")
    } else {
        log.info("Using existing MinIO bucket: $bucket")
    }
}

/**
 * 配置本地存储
 */
private fun Application.configureLocalStorage(config: ApplicationConfig) {
    val directory = config.property("directory").getString()
    val uploadDir = File(directory)
    
    if (!uploadDir.exists()) {
        uploadDir.mkdirs()
        log.info("Created local storage directory: ${uploadDir.absolutePath}")
    } else {
        log.info("Using existing local storage directory: ${uploadDir.absolutePath}")
    }
} 