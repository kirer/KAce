package com.kace.media.infrastructure.persistence.repository

import com.kace.media.domain.model.MediaProcessingTask
import com.kace.media.domain.model.TaskStatus
import com.kace.media.domain.repository.MediaProcessingTaskRepository
import com.kace.media.infrastructure.persistence.entity.MediaProcessingTaskEntity
import com.kace.media.infrastructure.persistence.entity.MediaProcessingTasks
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class MediaProcessingTaskRepositoryImpl : MediaProcessingTaskRepository {

    override fun findById(id: UUID): MediaProcessingTask? = transaction {
        MediaProcessingTaskEntity.findById(id)?.toMediaProcessingTask()
    }

    override fun findByMediaId(mediaId: UUID): List<MediaProcessingTask> = transaction {
        MediaProcessingTaskEntity.find { MediaProcessingTasks.mediaId eq mediaId }
            .map { it.toMediaProcessingTask() }
    }

    override fun findByStatus(status: TaskStatus, page: Int, size: Int): List<MediaProcessingTask> = transaction {
        MediaProcessingTaskEntity.find { MediaProcessingTasks.status eq status }
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toMediaProcessingTask() }
    }

    override fun findPendingTasks(limit: Int): List<MediaProcessingTask> = transaction {
        MediaProcessingTaskEntity.find { MediaProcessingTasks.status eq TaskStatus.PENDING }
            .limit(limit)
            .map { it.toMediaProcessingTask() }
    }

    override fun save(task: MediaProcessingTask): MediaProcessingTask = transaction {
        val existingTask = task.id?.let { MediaProcessingTaskEntity.findById(it) }

        val taskEntity = if (existingTask != null) {
            existingTask.apply {
                this.mediaId = task.mediaId
                this.taskType = task.taskType
                this.parameters = task.parameters
                this.status = task.status
                this.result = task.result
                this.errorMessage = task.errorMessage
                this.updatedAt = Instant.now()
                if (task.startedAt != null) this.startedAt = task.startedAt
                if (task.completedAt != null) this.completedAt = task.completedAt
            }
        } else {
            MediaProcessingTaskEntity.new(task.id ?: UUID.randomUUID()) {
                this.mediaId = task.mediaId
                this.taskType = task.taskType
                this.parameters = task.parameters
                this.status = task.status ?: TaskStatus.PENDING
                this.result = task.result
                this.errorMessage = task.errorMessage
                this.createdAt = Instant.now()
                this.updatedAt = Instant.now()
                this.startedAt = task.startedAt
                this.completedAt = task.completedAt
            }
        }

        taskEntity.toMediaProcessingTask()
    }

    override fun updateStatus(id: UUID, status: TaskStatus, result: Map<String, Any>?, errorMessage: String?): Boolean = transaction {
        val task = MediaProcessingTaskEntity.findById(id)
        if (task != null) {
            task.status = status
            task.result = result
            task.errorMessage = errorMessage
            task.updatedAt = Instant.now()
            
            if (status == TaskStatus.PROCESSING && task.startedAt == null) {
                task.startedAt = Instant.now()
            } else if ((status == TaskStatus.COMPLETED || status == TaskStatus.FAILED) && task.completedAt == null) {
                task.completedAt = Instant.now()
            }
            
            true
        } else {
            false
        }
    }

    override fun delete(id: UUID): Boolean = transaction {
        val task = MediaProcessingTaskEntity.findById(id)
        task?.delete()
        task != null
    }

    override fun deleteByMediaId(mediaId: UUID): Int = transaction {
        val tasks = MediaProcessingTaskEntity.find { MediaProcessingTasks.mediaId eq mediaId }
        val count = tasks.count().toInt()
        tasks.forEach { it.delete() }
        count
    }

    override fun countByStatus(status: TaskStatus): Long = transaction {
        MediaProcessingTaskEntity.find { MediaProcessingTasks.status eq status }.count()
    }

    private fun MediaProcessingTaskEntity.toMediaProcessingTask(): MediaProcessingTask {
        return MediaProcessingTask(
            id = this.id.value,
            mediaId = this.mediaId,
            taskType = this.taskType,
            parameters = this.parameters,
            status = this.status,
            result = this.result,
            errorMessage = this.errorMessage,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            startedAt = this.startedAt,
            completedAt = this.completedAt
        )
    }
} 