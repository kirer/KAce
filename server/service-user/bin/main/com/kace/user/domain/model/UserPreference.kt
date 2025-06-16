package com.kace.user.domain.model

import java.util.UUID
import kotlinx.datetime.Instant

/**
 * 用户偏好设置类别
 */
enum class PreferenceCategory {
    UI,         // 用户界面偏好
    NOTIFICATION, // 通知偏好
    PRIVACY,    // 隐私偏好
    SECURITY,   // 安全偏好
    SYSTEM,     // 系统偏好
    CUSTOM      // 自定义偏好
}

/**
 * 用户偏好设置领域模型
 *
 * @property id 偏好设置ID
 * @property userId 用户ID
 * @property key 偏好设置键
 * @property value 偏好设置值
 * @property category 偏好设置类别
 * @property createdAt 创建时间
 * @property updatedAt 更新时间
 */
data class UserPreference(
    val id: UUID? = null,
    val userId: UUID,
    val key: String,
    val value: String,
    val category: PreferenceCategory = PreferenceCategory.CUSTOM,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
) 