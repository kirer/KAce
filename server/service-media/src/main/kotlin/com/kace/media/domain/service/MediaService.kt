package com.kace.media.domain.service

import com.kace.media.domain.model.Media
import com.kace.media.domain.model.MediaStatus
import com.kace.media.domain.model.MediaType
import java.io.File
import java.io.InputStream
import java.util.UUID

/**
 * 媒体服务接口
 */
interface MediaService {
    /**
     * 上传媒体文件
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param contentType 内容类型
     * @param description 描述（可选）
     * @param folderId 文件夹ID（可选）
     * @param tags 标签列表（可选）
     * @param createdBy 创建者ID
     * @return 创建的媒体对象
     */
    suspend fun uploadMedia(
        inputStream: InputStream,
        fileName: String,
        contentType: String,
        description: String? = null,
        folderId: UUID? = null,
        tags: List<String> = emptyList(),
        createdBy: UUID
    ): Media
    
    /**
     * 上传媒体文件
     * @param file 文件对象
     * @param fileName 文件名（可选，默认使用文件原名）
     * @param contentType 内容类型（可选，默认自动检测）
     * @param description 描述（可选）
     * @param folderId 文件夹ID（可选）
     * @param tags 标签列表（可选）
     * @param createdBy 创建者ID
     * @return 创建的媒体对象
     */
    suspend fun uploadMedia(
        file: File,
        fileName: String? = null,
        contentType: String? = null,
        description: String? = null,
        folderId: UUID? = null,
        tags: List<String> = emptyList(),
        createdBy: UUID
    ): Media
    
    /**
     * 获取媒体文件
     * @param id 媒体ID
     * @return 媒体文件输入流
     */
    suspend fun getMediaFile(id: UUID): InputStream
    
    /**
     * 获取媒体缩略图
     * @param id 媒体ID
     * @return 缩略图输入流
     */
    suspend fun getMediaThumbnail(id: UUID): InputStream?
    
    /**
     * 获取媒体详情
     * @param id 媒体ID
     * @return 媒体对象
     */
    suspend fun getMediaById(id: UUID): Media?
    
    /**
     * 更新媒体信息
     * @param id 媒体ID
     * @param name 新名称（可选）
     * @param description 新描述（可选）
     * @param folderId 新文件夹ID（可选）
     * @param tags 新标签列表（可选）
     * @return 更新后的媒体对象
     */
    suspend fun updateMedia(
        id: UUID,
        name: String? = null,
        description: String? = null,
        folderId: UUID? = null,
        tags: List<String>? = null
    ): Media
    
    /**
     * 删除媒体
     * @param id 媒体ID
     * @return 是否删除成功
     */
    suspend fun deleteMedia(id: UUID): Boolean
    
    /**
     * 获取所有媒体（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 媒体列表
     */
    suspend fun getAllMedia(page: Int, size: Int): List<Media>
    
    /**
     * 根据文件夹获取媒体（分页）
     * @param folderId 文件夹ID
     * @param page 页码
     * @param size 每页大小
     * @return 媒体列表
     */
    suspend fun getMediaByFolder(folderId: UUID?, page: Int, size: Int): List<Media>
    
    /**
     * 根据类型获取媒体（分页）
     * @param type 媒体类型
     * @param page 页码
     * @param size 每页大小
     * @return 媒体列表
     */
    suspend fun getMediaByType(type: MediaType, page: Int, size: Int): List<Media>
    
    /**
     * 根据标签获取媒体（分页）
     * @param tag 标签
     * @param page 页码
     * @param size 每页大小
     * @return 媒体列表
     */
    suspend fun getMediaByTag(tag: String, page: Int, size: Int): List<Media>
    
    /**
     * 搜索媒体（分页）
     * @param query 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 媒体列表
     */
    suspend fun searchMedia(query: String, page: Int, size: Int): List<Media>
    
    /**
     * 添加标签到媒体
     * @param mediaId 媒体ID
     * @param tag 标签
     * @return 是否添加成功
     */
    suspend fun addTagToMedia(mediaId: UUID, tag: String): Boolean
    
    /**
     * 从媒体中移除标签
     * @param mediaId 媒体ID
     * @param tag 标签
     * @return 是否移除成功
     */
    suspend fun removeTagFromMedia(mediaId: UUID, tag: String): Boolean
    
    /**
     * 获取媒体总数
     * @return 媒体总数
     */
    suspend fun countMedia(): Int
    
    /**
     * 获取特定类型的媒体数量
     * @param type 媒体类型
     * @return 媒体数量
     */
    suspend fun countMediaByType(type: MediaType): Int
    
    /**
     * 获取特定文件夹中的媒体数量
     * @param folderId 文件夹ID
     * @return 媒体数量
     */
    suspend fun countMediaByFolder(folderId: UUID?): Int
} 