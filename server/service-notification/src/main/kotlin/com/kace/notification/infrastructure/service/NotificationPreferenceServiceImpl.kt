package com.kace.notification.infrastructure.service

import com.kace.notification.domain.model.NotificationPreference
import com.kace.notification.domain.model.NotificationType
import com.kace.notification.domain.repository.NotificationPreferenceRepository
import com.kace.notification.domain.service.NotificationPreferenceService
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

/**
 * 通知偏好服务实现类
 */
class NotificationPreferenceServiceImpl(
    private val preferenceRepository: NotificationPreferenceRepository
) : NotificationPreferenceService {
    
    override fun createPreference(
        userId: UUID,
        eventType: String,
        channels: Set<NotificationType>,
        enabled: Boolean,
        quietHoursStart: Int?,
        quietHoursEnd: Int?
    ): NotificationPreference {
        // 检查是否已存在相同的偏好设置
        val existingPreference = preferenceRepository.findByUserIdAndEventType(userId, eventType)
        if (existingPreference != null) {
            // 更新现有偏好
            return updatePreference(
                existingPreference.id,
                channels,
                enabled,
                quietHoursStart,
                quietHoursEnd
            ) ?: existingPreference
        }
        
        val now = Instant.now()
        val preference = NotificationPreference(
            id = UUID.randomUUID(),
            userId = userId,
            eventType = eventType,
            channels = channels,
            enabled = enabled,
            quietHoursStart = quietHoursStart,
            quietHoursEnd = quietHoursEnd,
            createdAt = now,
            updatedAt = now
        )
        
        return preferenceRepository.create(preference)
    }
    
    override fun updatePreference(
        id: UUID,
        channels: Set<NotificationType>?,
        enabled: Boolean?,
        quietHoursStart: Int?,
        quietHoursEnd: Int?
    ): NotificationPreference? {
        val existingPreference = preferenceRepository.findById(id) ?: return null
        
        val updatedPreference = existingPreference.copy(
            channels = channels ?: existingPreference.channels,
            enabled = enabled ?: existingPreference.enabled,
            quietHoursStart = quietHoursStart ?: existingPreference.quietHoursStart,
            quietHoursEnd = quietHoursEnd ?: existingPreference.quietHoursEnd,
            updatedAt = Instant.now()
        )
        
        return preferenceRepository.update(updatedPreference)
    }
    
    override fun getPreference(id: UUID): NotificationPreference? {
        return preferenceRepository.findById(id)
    }
    
    override fun getUserPreferences(userId: UUID): List<NotificationPreference> {
        return preferenceRepository.findByUserId(userId)
    }
    
    override fun getUserPreferenceForEventType(userId: UUID, eventType: String): NotificationPreference? {
        return preferenceRepository.findByUserIdAndEventType(userId, eventType)
    }
    
    override fun enablePreference(id: UUID): Boolean {
        return preferenceRepository.enable(id)
    }
    
    override fun disablePreference(id: UUID): Boolean {
        return preferenceRepository.disable(id)
    }
    
    override fun enableAllUserPreferences(userId: UUID): Int {
        return preferenceRepository.enableAllForUser(userId)
    }
    
    override fun disableAllUserPreferences(userId: UUID): Int {
        return preferenceRepository.disableAllForUser(userId)
    }
    
    override fun deletePreference(id: UUID): Boolean {
        return preferenceRepository.deleteById(id)
    }
    
    override fun deleteAllUserPreferences(userId: UUID): Int {
        return preferenceRepository.deleteByUserId(userId)
    }
    
    override fun isEventTypeEnabled(userId: UUID, eventType: String): Boolean {
        return preferenceRepository.isEventTypeEnabled(userId, eventType)
    }
    
    override fun isQuietHoursActive(userId: UUID): Boolean {
        // 获取用户的所有偏好设置
        val preferences = preferenceRepository.findByUserId(userId)
        
        // 如果没有设置免打扰时段，则不在免打扰时段
        if (preferences.isEmpty() || preferences.all { it.quietHoursStart == null || it.quietHoursEnd == null }) {
            return false
        }
        
        // 获取当前小时
        val currentHour = LocalTime.now().hour
        
        // 检查是否有任何一个偏好设置的免打扰时段包含当前时间
        return preferences.any { preference ->
            val start = preference.quietHoursStart
            val end = preference.quietHoursEnd
            
            if (start != null && end != null) {
                if (start < end) {
                    // 正常时段，例如 22:00 - 06:00
                    currentHour in start until end
                } else {
                    // 跨越午夜的时段，例如 22:00 - 06:00
                    currentHour >= start || currentHour < end
                }
            } else {
                false
            }
        }
    }
    
    override fun getEnabledChannelsForEventType(userId: UUID, eventType: String): Set<NotificationType> {
        val preference = preferenceRepository.findByUserIdAndEventType(userId, eventType)
        
        return if (preference != null && preference.enabled) {
            preference.channels
        } else {
            // 如果没有特定的偏好设置或已禁用，则返回空集合
            emptySet()
        }
    }
} 