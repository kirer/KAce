package com.kace.notification.infrastructure.persistence.repository

import com.kace.notification.domain.model.NotificationChannel
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.repository.NotificationChannelRepository
import com.kace.notification.infrastructure.persistence.entity.NotificationChannelEntities
import com.kace.notification.infrastructure.persistence.entity.NotificationChannelEntity
import com.kace.notification.infrastructure.persistence.entity.toEntity
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * 通知渠道仓库实现类
 */
class NotificationChannelRepositoryImpl : NotificationChannelRepository {
    
    override fun create(channel: NotificationChannel): NotificationChannel = transaction {
        // 如果设置为默认渠道，先将同类型的其他渠道设为非默认
        if (channel.isDefault) {
            NotificationChannelEntity.find { 
                (NotificationChannelEntities.type eq channel.type) and
                (NotificationChannelEntities.isDefault eq true)
            }.forEach { it.isDefault = false }
        }
        
        channel.toEntity().toDomain()
    }
    
    override fun update(channel: NotificationChannel): NotificationChannel = transaction {
        val entity = NotificationChannelEntity[channel.id]
        
        // 如果设置为默认渠道，先将同类型的其他渠道设为非默认
        if (channel.isDefault && !entity.isDefault) {
            NotificationChannelEntity.find { 
                (NotificationChannelEntities.type eq channel.type) and
                (NotificationChannelEntities.isDefault eq true) and
                (NotificationChannelEntities.id neq channel.id)
            }.forEach { it.isDefault = false }
        }
        
        entity.name = channel.name
        entity.type = channel.type
        entity.provider = channel.provider
        entity.config = channel.config
        entity.isDefault = channel.isDefault
        entity.isActive = channel.isActive
        entity.updatedAt = channel.updatedAt
        
        entity.toDomain()
    }
    
    override fun findById(id: UUID): NotificationChannel? = transaction {
        NotificationChannelEntity.findById(id)?.toDomain()
    }
    
    override fun findByName(name: String): NotificationChannel? = transaction {
        NotificationChannelEntity.find { NotificationChannelEntities.name eq name }
            .firstOrNull()?.toDomain()
    }
    
    override fun findByType(type: NotificationType): List<NotificationChannel> = transaction {
        NotificationChannelEntity.find { NotificationChannelEntities.type eq type }
            .orderBy(NotificationChannelEntities.isDefault to SortOrder.DESC, NotificationChannelEntities.name to SortOrder.ASC)
            .map { it.toDomain() }
    }
    
    override fun findDefaultByType(type: NotificationType): NotificationChannel? = transaction {
        NotificationChannelEntity.find { 
            (NotificationChannelEntities.type eq type) and 
            (NotificationChannelEntities.isDefault eq true) and
            (NotificationChannelEntities.isActive eq true)
        }.firstOrNull()?.toDomain()
    }
    
    override fun findAllActive(): List<NotificationChannel> = transaction {
        NotificationChannelEntity.find { NotificationChannelEntities.isActive eq true }
            .orderBy(NotificationChannelEntities.type to SortOrder.ASC, NotificationChannelEntities.name to SortOrder.ASC)
            .map { it.toDomain() }
    }
    
    override fun findAll(): List<NotificationChannel> = transaction {
        NotificationChannelEntity.all()
            .orderBy(NotificationChannelEntities.type to SortOrder.ASC, NotificationChannelEntities.name to SortOrder.ASC)
            .map { it.toDomain() }
    }
    
    override fun setAsDefault(id: UUID): Boolean = transaction {
        val entity = NotificationChannelEntity.findById(id) ?: return@transaction false
        
        // 将同类型的其他渠道设为非默认
        NotificationChannelEntity.find { 
            (NotificationChannelEntities.type eq entity.type) and
            (NotificationChannelEntities.isDefault eq true) and
            (NotificationChannelEntities.id neq id)
        }.forEach { it.isDefault = false }
        
        entity.isDefault = true
        true
    }
    
    override fun activate(id: UUID): Boolean = transaction {
        val entity = NotificationChannelEntity.findById(id) ?: return@transaction false
        entity.isActive = true
        true
    }
    
    override fun deactivate(id: UUID): Boolean = transaction {
        val entity = NotificationChannelEntity.findById(id) ?: return@transaction false
        entity.isActive = false
        
        // 如果是默认渠道，需要重新指定同类型的另一个活跃渠道为默认
        if (entity.isDefault) {
            val newDefault = NotificationChannelEntity.find { 
                (NotificationChannelEntities.type eq entity.type) and
                (NotificationChannelEntities.isActive eq true) and
                (NotificationChannelEntities.id neq id)
            }.firstOrNull()
            
            newDefault?.isDefault = true
            entity.isDefault = false
        }
        
        true
    }
    
    override fun deleteById(id: UUID): Boolean = transaction {
        val entity = NotificationChannelEntity.findById(id) ?: return@transaction false
        
        // 如果是默认渠道，需要重新指定同类型的另一个活跃渠道为默认
        if (entity.isDefault) {
            val newDefault = NotificationChannelEntity.find { 
                (NotificationChannelEntities.type eq entity.type) and
                (NotificationChannelEntities.isActive eq true) and
                (NotificationChannelEntities.id neq id)
            }.firstOrNull()
            
            newDefault?.isDefault = true
        }
        
        entity.delete()
        true
    }
} 