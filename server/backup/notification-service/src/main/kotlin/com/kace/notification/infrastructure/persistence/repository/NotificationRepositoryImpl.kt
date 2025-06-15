package com.kace.notification.infrastructure.persistence.repository

import com.kace.notification.domain.model.Notification
import com.kace.notification.domain.model.NotificationStatus
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.repository.NotificationRepository
import com.kace.notification.infrastructure.persistence.entity.NotificationEntities
import com.kace.notification.infrastructure.persistence.entity.NotificationEntity
import com.kace.notification.infrastructure.persistence.entity.toEntity
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

/**
 * 通知仓库实现类
 */
class NotificationRepositoryImpl : NotificationRepository {
    
    override fun create(notification: Notification): Notification = transaction {
        notification.toEntity().toDomain()
    }
    
    override fun update(notification: Notification): Notification = transaction {
        val entity = NotificationEntity[notification.id]
        entity.type = notification.type
        entity.recipientId = notification.recipientId
        entity.title = notification.title
        entity.content = notification.content
        entity.metadata = notification.metadata as? Map<String, Any> ?: emptyMap()
        entity.status = notification.status
        entity.priority = notification.priority
        entity.sentAt = notification.sentAt
        entity.deliveredAt = notification.deliveredAt
        entity.readAt = notification.readAt
        entity.updatedAt = notification.updatedAt
        entity.toDomain()
    }
    
    override fun findById(id: UUID): Notification? = transaction {
        NotificationEntity.findById(id)?.toDomain()
    }
    
    override fun findByRecipientId(recipientId: UUID, limit: Int, offset: Int): List<Notification> = transaction {
        NotificationEntity.find { NotificationEntities.recipientId eq recipientId }
            .orderBy(NotificationEntities.createdAt to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toDomain() }
    }
    
    override fun findByStatus(status: NotificationStatus, limit: Int, offset: Int): List<Notification> = transaction {
        NotificationEntity.find { NotificationEntities.status eq status }
            .orderBy(NotificationEntities.createdAt to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toDomain() }
    }
    
    override fun findByType(type: NotificationType, limit: Int, offset: Int): List<Notification> = transaction {
        NotificationEntity.find { NotificationEntities.type eq type }
            .orderBy(NotificationEntities.createdAt to SortOrder.DESC)
            .limit(limit, offset.toLong())
            .map { it.toDomain() }
    }
    
    override fun findByRecipientIdAndStatus(recipientId: UUID, status: NotificationStatus, limit: Int, offset: Int): List<Notification> = transaction {
        NotificationEntity.find { 
            (NotificationEntities.recipientId eq recipientId) and 
            (NotificationEntities.status eq status) 
        }
        .orderBy(NotificationEntities.createdAt to SortOrder.DESC)
        .limit(limit, offset.toLong())
        .map { it.toDomain() }
    }
    
    override fun findByRecipientIdAndType(recipientId: UUID, type: NotificationType, limit: Int, offset: Int): List<Notification> = transaction {
        NotificationEntity.find { 
            (NotificationEntities.recipientId eq recipientId) and 
            (NotificationEntities.type eq type) 
        }
        .orderBy(NotificationEntities.createdAt to SortOrder.DESC)
        .limit(limit, offset.toLong())
        .map { it.toDomain() }
    }
    
    override fun findByTimeRange(startTime: Instant, endTime: Instant, limit: Int, offset: Int): List<Notification> = transaction {
        NotificationEntity.find { 
            (NotificationEntities.createdAt greaterEq startTime) and 
            (NotificationEntities.createdAt lessEq endTime) 
        }
        .orderBy(NotificationEntities.createdAt to SortOrder.DESC)
        .limit(limit, offset.toLong())
        .map { it.toDomain() }
    }
    
    override fun updateStatus(id: UUID, status: NotificationStatus, timestamp: Instant): Boolean = transaction {
        val entity = NotificationEntity.findById(id) ?: return@transaction false
        entity.status = status
        entity.updatedAt = timestamp
        
        // 根据状态更新相应的时间戳
        when (status) {
            NotificationStatus.SENT -> entity.sentAt = timestamp
            NotificationStatus.DELIVERED -> entity.deliveredAt = timestamp
            NotificationStatus.READ -> entity.readAt = timestamp
            else -> {} // 其他状态不需要更新特定时间戳
        }
        
        true
    }
    
    override fun markAsRead(id: UUID): Boolean = transaction {
        val entity = NotificationEntity.findById(id) ?: return@transaction false
        val now = Instant.now()
        entity.status = NotificationStatus.READ
        entity.readAt = now
        entity.updatedAt = now
        true
    }
    
    override fun markAsRead(ids: List<UUID>): Int = transaction {
        var count = 0
        val now = Instant.now()
        
        ids.forEach { id ->
            val entity = NotificationEntity.findById(id) ?: return@forEach
            entity.status = NotificationStatus.READ
            entity.readAt = now
            entity.updatedAt = now
            count++
        }
        
        count
    }
    
    override fun markAllAsRead(recipientId: UUID): Int = transaction {
        var count = 0
        val now = Instant.now()
        
        NotificationEntity.find { 
            (NotificationEntities.recipientId eq recipientId) and 
            (NotificationEntities.status neq NotificationStatus.READ) 
        }.forEach { entity ->
            entity.status = NotificationStatus.READ
            entity.readAt = now
            entity.updatedAt = now
            count++
        }
        
        count
    }
    
    override fun deleteById(id: UUID): Boolean = transaction {
        NotificationEntity.findById(id)?.delete() != null
    }
    
    override fun deleteByRecipientId(recipientId: UUID): Int = transaction {
        var count = 0
        
        NotificationEntity.find { NotificationEntities.recipientId eq recipientId }
            .forEach {
                it.delete()
                count++
            }
        
        count
    }
    
    override fun deleteOlderThan(timestamp: Instant): Int = transaction {
        var count = 0
        
        NotificationEntity.find { NotificationEntities.createdAt less timestamp }
            .forEach {
                it.delete()
                count++
            }
        
        count
    }
    
    override fun countUnreadByRecipientId(recipientId: UUID): Long = transaction {
        NotificationEntity.find { 
            (NotificationEntities.recipientId eq recipientId) and 
            (NotificationEntities.status neq NotificationStatus.READ) 
        }.count()
    }
} 