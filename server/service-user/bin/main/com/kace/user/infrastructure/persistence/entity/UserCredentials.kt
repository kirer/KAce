package com.kace.user.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

/**
 * 用户凭证实体类
 */
@Entity
@Table(name = "user_credentials")
class UserCredentials(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: UUID,
    
    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,
    
    @Column(name = "last_login")
    val lastLogin: Instant? = null,
    
    @Column(name = "password_reset_token")
    val passwordResetToken: String? = null,
    
    @Column(name = "password_reset_expires")
    val passwordResetExpires: Instant? = null,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant
) 