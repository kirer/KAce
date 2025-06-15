package com.kace.media.domain.service

import com.kace.media.domain.model.MediaProcessingTask
import com.kace.media.domain.model.MediaProcessingTaskStatus
import com.kace.media.domain.model.MediaProcessingTaskType
import java.io.InputStream
import java.util.UUID

/**
 * 媒体处理服务接口
 */
interface MediaProcessingService {
    /**
     * 创建处理任务
     * @param mediaId 媒体ID
     * @param type 任务类型
     * @param parameters 任务参数
     * @return 创建的任务对象
     */
    suspend fun createTask(
        mediaId: UUID,
        type: MediaProcessingTaskType,
        parameters: Map<String, Any>? = null
    ): MediaProcessingTask
    
    /**
     * 获取任务详情
     * @param id 任务ID
     * @return 任务对象
     */
    suspend fun getTaskById(id: UUID): MediaProcessingTask?
    
    /**
     * 获取媒体的所有任务
     * @param mediaId 媒体ID
     * @return 任务列表
     */
    suspend fun getTasksByMediaId(mediaId: UUID): List<MediaProcessingTask>
    
    /**
     * 更新任务状态
     * @param id 任务ID
     * @param status 新状态
     * @param errorMessage 错误信息（如果状态为FAILED）
     * @return 是否更新成功
     */
    suspend fun updateTaskStatus(
        id: UUID,
        status: MediaProcessingTaskStatus,
        errorMessage: String? = null
    ): Boolean
    
    /**
     * 更新任务结果
     * @param id 任务ID
     * @param result 任务结果
     * @return 是否更新成功
     */
    suspend fun updateTaskResult(id: UUID, result: Map<String, Any>): Boolean
    
    /**
     * 执行任务
     * @param taskId 任务ID
     * @return 是否执行成功
     */
    suspend fun executeTask(taskId: UUID): Boolean
    
    /**
     * 生成图片缩略图
     * @param mediaId 媒体ID
     * @param inputStream 图片输入流
     * @param width 缩略图宽度
     * @param height 缩略图高度
     * @return 缩略图路径
     */
    suspend fun generateImageThumbnail(
        mediaId: UUID,
        inputStream: InputStream,
        width: Int = 200,
        height: Int = 200
    ): String
    
    /**
     * 调整图片大小
     * @param mediaId 媒体ID
     * @param inputStream 图片输入流
     * @param width 目标宽度
     * @param height 目标高度
     * @param keepAspectRatio 是否保持宽高比
     * @return 调整后的图片路径
     */
    suspend fun resizeImage(
        mediaId: UUID,
        inputStream: InputStream,
        width: Int,
        height: Int,
        keepAspectRatio: Boolean = true
    ): String
    
    /**
     * 提取媒体元数据
     * @param mediaId 媒体ID
     * @param inputStream 媒体输入流
     * @param mimeType 媒体MIME类型
     * @return 元数据Map
     */
    suspend fun extractMetadata(
        mediaId: UUID,
        inputStream: InputStream,
        mimeType: String
    ): Map<String, Any>
    
    /**
     * 获取待处理任务列表
     * @param limit 限制数量
     * @return 任务列表
     */
    suspend fun getPendingTasks(limit: Int = 10): List<MediaProcessingTask>
    
    /**
     * 获取特定状态的任务数量
     * @param status 任务状态
     * @return 任务数量
     */
    suspend fun countTasksByStatus(status: MediaProcessingTaskStatus): Int
} 