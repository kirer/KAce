package com.kace.user.api.response

import com.kace.user.domain.model.PreferenceCategory
import com.kace.user.domain.model.UserPreference
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 用户偏好设置响应
 */
@Serializable
data class UserPreferenceResponse(
    val id: String?,
    val key: String,
    val value: String,
    val category: PreferenceCategory,
    val createdAt: Instant?,
    val updatedAt: Instant?
) {
    companion object {
        fun fromDomain(preference: UserPreference): UserPreferenceResponse {
            return UserPreferenceResponse(
                id = preference.id?.toString(),
                key = preference.key,
                value = preference.value,
                category = preference.category,
                createdAt = preference.createdAt,
                updatedAt = preference.updatedAt
            )
        }
    }
}

/**
 * 导出用户偏好设置响应
 */
@Serializable
data class ExportPreferencesResponse(
    val preferences: String
) 