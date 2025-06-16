package com.kace.media.domain.service

import com.kace.media.domain.model.MediaFolder
import java.util.UUID

/**
 * 媒体文件夹服务接口
 */
interface MediaFolderService {
    /**
     * 创建文件夹
     * @param name 文件夹名称
     * @param description 描述（可选）
     * @param parentId 父文件夹ID（可选）
     * @param createdBy 创建者ID
     * @return 创建的文件夹对象
     */
    suspend fun createFolder(
        name: String,
        description: String? = null,
        parentId: UUID? = null,
        createdBy: UUID
    ): MediaFolder
    
    /**
     * 获取文件夹详情
     * @param id 文件夹ID
     * @return 文件夹对象
     */
    suspend fun getFolderById(id: UUID): MediaFolder?
    
    /**
     * 更新文件夹
     * @param id 文件夹ID
     * @param name 新名称（可选）
     * @param description 新描述（可选）
     * @return 更新后的文件夹对象
     */
    suspend fun updateFolder(
        id: UUID,
        name: String? = null,
        description: String? = null
    ): MediaFolder
    
    /**
     * 删除文件夹
     * @param id 文件夹ID
     * @param recursive 是否递归删除子文件夹和媒体（默认为false）
     * @return 是否删除成功
     */
    suspend fun deleteFolder(id: UUID, recursive: Boolean = false): Boolean
    
    /**
     * 获取根文件夹列表（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 文件夹列表
     */
    suspend fun getRootFolders(page: Int, size: Int): List<MediaFolder>
    
    /**
     * 获取子文件夹列表（分页）
     * @param parentId 父文件夹ID
     * @param page 页码
     * @param size 每页大小
     * @return 文件夹列表
     */
    suspend fun getSubFolders(parentId: UUID, page: Int, size: Int): List<MediaFolder>
    
    /**
     * 获取文件夹路径（从根到当前文件夹的路径）
     * @param id 文件夹ID
     * @return 文件夹路径列表
     */
    suspend fun getFolderPath(id: UUID): List<MediaFolder>
    
    /**
     * 检查文件夹是否为空
     * @param id 文件夹ID
     * @return 是否为空
     */
    suspend fun isFolderEmpty(id: UUID): Boolean
    
    /**
     * 移动文件夹
     * @param id 文件夹ID
     * @param newParentId 新的父文件夹ID（null表示移动到根目录）
     * @return 更新后的文件夹对象
     */
    suspend fun moveFolder(id: UUID, newParentId: UUID?): MediaFolder
    
    /**
     * 获取文件夹总数
     * @return 文件夹总数
     */
    suspend fun countFolders(): Int
    
    /**
     * 获取特定父文件夹下的子文件夹数量
     * @param parentId 父文件夹ID
     * @return 子文件夹数量
     */
    suspend fun countSubFolders(parentId: UUID?): Int
} 