package com.kace.notification.infrastructure.persistence.repository

import com.kace.notification.domain.model.NotificationPreference
import com.kace.notification.domain.repository.NotificationPreferenceRepository
import com.kace.notification.infrastructure.persistence.entity.NotificationPreferenceEntities
import com.kace.notification.infrastructure.persistence.entity.NotificationPreferenceEntity
import com.kace.notification.infrastructure.persistence.entity.toEntity
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * 通知偏好仓库实现类
 */
class NotificationPreferenceRepositoryImpl : NotificationPreferenceRepository {
    
    override fun create(preference: NotificationPreference): NotificationPreference = transaction {
        preference.toEntity().toDomain()
    }
    
    override fun update(preference: NotificationPreference): NotificationPreference = transaction {
        val entity = NotificationPreferenceEntity[preference.id]
        entity.userId = preference.userId
        entity.eventType = preference.eventType
        entity.channels = preference.channels.map { it.name }
        entity.enabled = preference.enabled
        entity.quietHoursStart = preference.quietHoursStart
        entity.quietHoursEnd = preference.quietHoursEnd
        entity.updatedAt = preference.updatedAt
        entity.toDomain()
    }
    
    override fun findById(id: UUID): NotificationPreference? = transaction {
        NotificationPreferenceEntity.findById(id)?.toDomain()
    }
    
    override fun findByUserId(userId: UUID): List<NotificationPreference> = transaction {
        NotificationPreferenceEntity.find { NotificationPreferenceEntities.userId eq userId }
            .map { it.toDomain() }
    }
    
    override fun findByUserIdAndEventType(userId: UUID, eventType: String): NotificationPreference? = transaction {
        NotificationPreferenceEntity.find { 
            (NotificationPreferenceEntities.userId eq userId) and 
            (NotificationPreferenceEntities.eventType eq eventType) 
        }.firstOrNull()?.toDomain()
    }
    
    override fun enable(id: UUID): Boolean = transaction {
        val entity = NotificationPreferenceEntity.findById(id) ?: return@transaction false
        entity.enabled = true
        true
    }
    
    override fun disable(id: UUID): Boolean = transaction {
        val entity = NotificationPreferenceEntity.findById(id) ?: return@transaction false
        entity.enabled = false
        true
    }
    
    override fun enableAllForUser(userId: UUID): Int = transaction {
        var count = 0
        
        NotificationPreferenceEntity.find { NotificationPreferenceEntities.userId eq userId }
            .forEach {
                if (!it.enabled) {
                    it.enabled = true
                    count++
                }
            }
        
        count
    }
    
    override fun disableAllForUser(userId: UUID): Int = transaction {
        var count = 0
        
        NotificationPreferenceEntity.find { NotificationPreferenceEntities.userId eq userId }
            .forEach {
                if (it.enabled) {
                    it.enabled = false
                    count++
                }
            }
        
        count
    }
    
    override fun deleteById(id: UUID): Boolean = transaction {
        NotificationPreferenceEntity.findById(id)?.delete() != null
    }
    
    override fun deleteByUserId(userId: UUID): Int = transaction {
        var count = 0
        
        NotificationPreferenceEntity.find { NotificationPreferenceEntities.userId eq userId }
            .forEach {
                it.delete()
                count++
            }
        
        count
    }
    
    override fun isEventTypeEnabled(userId: UUID, eventType: String): Boolean = transaction {
        val preference = NotificationPreferenceEntity.find { 
            (NotificationPreferenceEntities.userId eq userId) and 
            (NotificationPreferenceEntities.eventType eq eventType) 
        }.firstOrNull()
        
        preference?.enabled ?: false
    }
} 