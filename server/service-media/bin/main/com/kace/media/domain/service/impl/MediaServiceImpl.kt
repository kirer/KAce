package com.kace.media.domain.service.impl

import com.kace.media.domain.model.Media
import com.kace.media.domain.model.MediaType
import com.kace.media.domain.repository.MediaRepository
import com.kace.media.domain.service.MediaProcessingService
import com.kace.media.domain.service.MediaService
import com.kace.media.domain.service.StorageService
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.*

class MediaServiceImpl(
    private val mediaRepository: MediaRepository,
    private val storageService: StorageService,
    private val mediaProcessingService: MediaProcessingService
) : MediaService {

    private val logger = LoggerFactory.getLogger(MediaServiceImpl::class.java)

    override fun getMediaById(id: UUID): Media? {
        return mediaRepository.findById(id)
    }

    override fun getAllMedia(page: Int, size: Int): List<Media> {
        return mediaRepository.findAll(page, size)
    }

    override fun getMediaByFolder(folderId: UUID?, page: Int, size: Int): List<Media> {
        return mediaRepository.findByFolderId(folderId, page, size)
    }

    override fun searchMediaByName(name: String, page: Int, size: Int): List<Media> {
        return mediaRepository.findByName(name, page, size)
    }

    override fun searchMediaByTag(tag: String, page: Int, size: Int): List<Media> {
        return mediaRepository.findByTag(tag, page, size)
    }

    override fun uploadMedia(
        fileName: String,
        contentType: String,
        fileSize: Long,
        inputStream: InputStream,
        folderId: UUID?,
        description: String?,
        userId: UUID
    ): Media {
        logger.info("Uploading media: $fileName, size: $fileSize, type: $contentType, folder: $folderId")
        
        // 确定媒体类型
        val mediaType = determineMediaType(contentType)
        
        // 上传文件到存储服务
        val storageResult = storageService.storeFile(fileName, contentType, inputStream)
        
        // 创建媒体记录
        val media = Media(
            id = UUID.randomUUID(),
            name = fileName,
            description = description,
            type = mediaType,
            mimeType = contentType,
            size = fileSize,
            path = storageResult.path,
            url = storageResult.url,
            metadata = mapOf("originalName" to fileName),
            folderId = folderId,
            createdBy = userId,
            createdAt = null,
            updatedAt = null
        )
        
        // 保存媒体记录
        val savedMedia = mediaRepository.save(media)
        
        // 根据媒体类型创建处理任务
        if (mediaType == MediaType.IMAGE) {
            mediaProcessingService.createImageProcessingTasks(savedMedia.id!!)
        } else if (mediaType == MediaType.VIDEO) {
            mediaProcessingService.createVideoProcessingTasks(savedMedia.id!!)
        }
        
        return savedMedia
    }

    override fun updateMedia(id: UUID, name: String?, description: String?, folderId: UUID?): Media? {
        val existingMedia = mediaRepository.findById(id) ?: return null
        
        val updatedMedia = existingMedia.copy(
            name = name ?: existingMedia.name,
            description = description ?: existingMedia.description,
            folderId = folderId ?: existingMedia.folderId
        )
        
        return mediaRepository.save(updatedMedia)
    }

    override fun deleteMedia(id: UUID): Boolean {
        val media = mediaRepository.findById(id) ?: return false
        
        // 删除存储的文件
        val deleted = storageService.deleteFile(media.path)
        if (!deleted) {
            logger.warn("Failed to delete file from storage: ${media.path}")
        }
        
        // 删除相关的处理任务
        mediaProcessingService.deleteTasksByMediaId(id)
        
        // 删除媒体记录
        return mediaRepository.delete(id)
    }

    override fun addTagToMedia(mediaId: UUID, tagId: UUID): Boolean {
        return mediaRepository.addTag(mediaId, tagId)
    }

    override fun removeTagFromMedia(mediaId: UUID, tagId: UUID): Boolean {
        return mediaRepository.removeTag(mediaId, tagId)
    }

    override fun countMedia(): Long {
        return mediaRepository.count()
    }

    override fun countMediaByFolder(folderId: UUID?): Long {
        return mediaRepository.countByFolderId(folderId)
    }

    private fun determineMediaType(contentType: String): MediaType {
        return when {
            contentType.startsWith("image/") -> MediaType.IMAGE
            contentType.startsWith("video/") -> MediaType.VIDEO
            contentType.startsWith("audio/") -> MediaType.AUDIO
            contentType.startsWith("application/pdf") -> MediaType.DOCUMENT
            contentType.startsWith("application/msword") || 
            contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml") ||
            contentType.startsWith("application/vnd.ms-excel") ||
            contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml") ||
            contentType.startsWith("application/vnd.ms-powerpoint") ||
            contentType.startsWith("application/vnd.openxmlformats-officedocument.presentationml") ||
            contentType.startsWith("text/") -> MediaType.DOCUMENT
            else -> MediaType.OTHER
        }
    }
} 