package com.kace.media.domain.repository

import com.kace.media.domain.model.MediaProcessingTask
import com.kace.media.domain.model.MediaProcessingTaskStatus
import com.kace.media.domain.model.MediaProcessingTaskType
import java.util.UUID

/**
 * 媒体处理任务仓库接口
 */
interface MediaProcessingTaskRepository {
    /**
     * 创建处理任务
     */
    suspend fun create(task: MediaProcessingTask): MediaProcessingTask
    
    /**
     * 根据ID查找处理任务
     */
    suspend fun findById(id: UUID): MediaProcessingTask?
    
    /**
     * 更新处理任务
     */
    suspend fun update(task: MediaProcessingTask): MediaProcessingTask
    
    /**
     * 删除处理任务
     */
    suspend fun delete(id: UUID): Boolean
    
    /**
     * 根据媒体ID查找处理任务
     */
    suspend fun findByMediaId(mediaId: UUID): List<MediaProcessingTask>
    
    /**
     * 根据状态查找处理任务（分页）
     */
    suspend fun findByStatus(status: MediaProcessingTaskStatus, page: Int, size: Int): List<MediaProcessingTask>
    
    /**
     * 根据类型查找处理任务（分页）
     */
    suspend fun findByType(type: MediaProcessingTaskType, page: Int, size: Int): List<MediaProcessingTask>
    
    /**
     * 查找所有处理任务（分页）
     */
    suspend fun findAll(page: Int, size: Int): List<MediaProcessingTask>
    
    /**
     * 更新任务状态
     */
    suspend fun updateStatus(id: UUID, status: MediaProcessingTaskStatus, errorMessage: String? = null): Boolean
    
    /**
     * 更新任务结果
     */
    suspend fun updateResult(id: UUID, result: Map<String, Any>): Boolean
    
    /**
     * 计算处理任务总数
     */
    suspend fun count(): Int
    
    /**
     * 计算特定状态的处理任务数量
     */
    suspend fun countByStatus(status: MediaProcessingTaskStatus): Int
    
    /**
     * 计算特定类型的处理任务数量
     */
    suspend fun countByType(type: MediaProcessingTaskType): Int
} 