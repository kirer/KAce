package com.kace.media.domain.repository

import com.kace.media.domain.model.Media
import com.kace.media.domain.model.MediaType
import java.util.UUID

/**
 * 媒体仓库接口
 */
interface MediaRepository {
    /**
     * 创建媒体
     */
    suspend fun create(media: Media): Media
    
    /**
     * 根据ID查找媒体
     */
    suspend fun findById(id: UUID): Media?
    
    /**
     * 更新媒体
     */
    suspend fun update(media: Media): Media
    
    /**
     * 删除媒体
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 查找所有媒体（分页）
     */
    suspend fun findAll(page: Int, size: Int): List<Media>
    
    /**
     * 根据文件夹ID查找媒体（分页）
     */
    suspend fun findByFolderId(folderId: UUID?, page: Int, size: Int): List<Media>
    
    /**
     * 根据类型查找媒体（分页）
     */
    suspend fun findByType(type: MediaType, page: Int, size: Int): List<Media>
    
    /**
     * 根据标签查找媒体（分页）
     */
    suspend fun findByTag(tag: String, page: Int, size: Int): List<Media>
    
    /**
     * 搜索媒体（分页）
     */
    suspend fun search(query: String, page: Int, size: Int): List<Media>
    
    /**
     * 计算媒体总数
     */
    suspend fun count(): Int
    
    /**
     * 计算特定文件夹中的媒体数量
     */
    suspend fun countByFolderId(folderId: UUID?): Int
    
    /**
     * 计算特定类型的媒体数量
     */
    suspend fun countByType(type: MediaType): Int
    
    /**
     * 添加标签到媒体
     */
    suspend fun addTag(mediaId: UUID, tag: String): Boolean
    
    /**
     * 从媒体中移除标签
     */
    suspend fun removeTag(mediaId: UUID, tag: String): Boolean
    
    /**
     * 更新媒体状态
     */
    suspend fun updateStatus(id: UUID, status: com.kace.media.domain.model.MediaStatus): Boolean
    
    /**
     * 更新媒体元数据
     */
    suspend fun updateMetadata(id: UUID, metadata: Map<String, Any>): Boolean
} 