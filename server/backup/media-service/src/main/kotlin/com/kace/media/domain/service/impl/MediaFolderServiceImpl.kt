package com.kace.media.domain.service.impl

import com.kace.media.domain.model.MediaFolder
import com.kace.media.domain.repository.MediaFolderRepository
import com.kace.media.domain.repository.MediaRepository
import com.kace.media.domain.service.MediaFolderService
import org.slf4j.LoggerFactory
import java.util.*

class MediaFolderServiceImpl(
    private val mediaFolderRepository: MediaFolderRepository,
    private val mediaRepository: MediaRepository
) : MediaFolderService {

    private val logger = LoggerFactory.getLogger(MediaFolderServiceImpl::class.java)

    override fun getFolderById(id: UUID): MediaFolder? {
        return mediaFolderRepository.findById(id)
    }

    override fun getAllFolders(page: Int, size: Int): List<MediaFolder> {
        return mediaFolderRepository.findAll(page, size)
    }

    override fun getSubFolders(parentId: UUID?, page: Int, size: Int): List<MediaFolder> {
        return mediaFolderRepository.findByParentId(parentId, page, size)
    }

    override fun searchFoldersByName(name: String, parentId: UUID?, page: Int, size: Int): List<MediaFolder> {
        return mediaFolderRepository.findByName(name, parentId, page, size)
    }

    override fun createFolder(name: String, description: String?, parentId: UUID?, userId: UUID): MediaFolder {
        logger.info("Creating folder: $name, parent: $parentId")
        
        // 如果指定了父文件夹，确认它存在
        if (parentId != null) {
            val parentFolder = mediaFolderRepository.findById(parentId)
                ?: throw IllegalArgumentException("Parent folder not found: $parentId")
        }
        
        val folder = MediaFolder(
            id = UUID.randomUUID(),
            name = name,
            description = description,
            parentId = parentId,
            createdBy = userId,
            createdAt = null,
            updatedAt = null
        )
        
        return mediaFolderRepository.save(folder)
    }

    override fun updateFolder(id: UUID, name: String?, description: String?, parentId: UUID?): MediaFolder? {
        val existingFolder = mediaFolderRepository.findById(id) ?: return null
        
        // 确保不会创建循环引用
        if (parentId != null && parentId != existingFolder.parentId) {
            validateNoCircularReference(id, parentId)
        }
        
        val updatedFolder = existingFolder.copy(
            name = name ?: existingFolder.name,
            description = description ?: existingFolder.description,
            parentId = parentId ?: existingFolder.parentId
        )
        
        return mediaFolderRepository.save(updatedFolder)
    }

    override fun deleteFolder(id: UUID, recursive: Boolean): Boolean {
        val folder = mediaFolderRepository.findById(id) ?: return false
        
        // 检查是否有子文件夹
        val hasSubFolders = mediaFolderRepository.hasChildren(id)
        
        // 检查文件夹是否包含媒体文件
        val mediaCount = mediaRepository.countByFolderId(id)
        
        if ((hasSubFolders || mediaCount > 0) && !recursive) {
            throw IllegalStateException("Folder is not empty. Use recursive delete to remove it and its contents.")
        }
        
        if (recursive) {
            // 递归删除所有子文件夹
            deleteSubFoldersRecursively(id)
            
            // 删除文件夹中的所有媒体文件
            // 注意：这里应该调用mediaService来正确删除媒体文件，但为了避免循环依赖，我们直接使用repository
            val mediaList = mediaRepository.findByFolderId(id, 1, Int.MAX_VALUE)
            mediaList.forEach { media ->
                media.id?.let { mediaId ->
                    mediaRepository.delete(mediaId)
                }
            }
        }
        
        return mediaFolderRepository.delete(id)
    }

    override fun countFolders(): Long {
        return mediaFolderRepository.count()
    }

    override fun countSubFolders(parentId: UUID?): Long {
        return mediaFolderRepository.countByParentId(parentId)
    }

    private fun deleteSubFoldersRecursively(parentId: UUID) {
        val subFolders = mediaFolderRepository.findByParentId(parentId, 1, Int.MAX_VALUE)
        
        subFolders.forEach { subFolder ->
            subFolder.id?.let { subFolderId ->
                // 先递归删除子文件夹的内容
                deleteSubFoldersRecursively(subFolderId)
                
                // 删除子文件夹中的所有媒体文件
                val mediaList = mediaRepository.findByFolderId(subFolderId, 1, Int.MAX_VALUE)
                mediaList.forEach { media ->
                    media.id?.let { mediaId ->
                        mediaRepository.delete(mediaId)
                    }
                }
                
                // 删除子文件夹
                mediaFolderRepository.delete(subFolderId)
            }
        }
    }

    private fun validateNoCircularReference(folderId: UUID, newParentId: UUID) {
        var currentParentId: UUID? = newParentId
        
        while (currentParentId != null) {
            if (currentParentId == folderId) {
                throw IllegalArgumentException("Circular reference detected. A folder cannot be a parent of itself.")
            }
            
            val parentFolder = mediaFolderRepository.findById(currentParentId)
            currentParentId = parentFolder?.parentId
        }
    }
} 