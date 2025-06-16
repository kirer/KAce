package com.kace.user.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID
import com.kace.user.domain.model.PreferenceCategory

/**
 * 用户偏好设置实体类
 */
@Entity
@Table(name = "user_preferences")
class UserPreferences(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    
    @Column(name = "preference_key", nullable = false, length = 100)
    val key: String,
    
    @Column(name = "preference_value", nullable = false, columnDefinition = "TEXT")
    val value: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    val category: PreferenceCategory,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant
) 