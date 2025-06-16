package com.kace.media.domain.repository

import com.kace.media.domain.model.MediaFolder
import java.util.UUID

/**
 * 媒体文件夹仓库接口
 */
interface MediaFolderRepository {
    /**
     * 创建文件夹
     */
    suspend fun create(folder: MediaFolder): MediaFolder
    
    /**
     * 根据ID查找文件夹
     */
    suspend fun findById(id: UUID): MediaFolder?
    
    /**
     * 更新文件夹
     */
    suspend fun update(folder: MediaFolder): MediaFolder
    
    /**
     * 删除文件夹
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 查找所有根文件夹（分页）
     */
    suspend fun findRootFolders(page: Int, size: Int): List<MediaFolder>
    
    /**
     * 查找特定父文件夹下的子文件夹（分页）
     */
    suspend fun findByParentId(parentId: UUID, page: Int, size: Int): List<MediaFolder>
    
    /**
     * 查找文件夹路径（从根到当前文件夹的路径）
     */
    suspend fun findPath(id: UUID): List<MediaFolder>
    
    /**
     * 计算文件夹总数
     */
    suspend fun count(): Int
    
    /**
     * 计算特定父文件夹下的子文件夹数量
     */
    suspend fun countByParentId(parentId: UUID?): Int
    
    /**
     * 检查文件夹是否为空（不包含子文件夹和媒体文件）
     */
    suspend fun isEmpty(id: UUID): Boolean
    
    /**
     * 根据路径查找文件夹
     */
    suspend fun findByPath(path: String): MediaFolder?
} 