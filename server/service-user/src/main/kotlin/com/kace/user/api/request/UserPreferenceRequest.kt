package com.kace.user.api.request

import com.kace.user.domain.model.PreferenceCategory
import kotlinx.serialization.Serializable

/**
 * 设置用户偏好请求
 */
@Serializable
data class SetPreferenceRequest(
    val key: String,
    val value: String,
    val category: PreferenceCategory = PreferenceCategory.CUSTOM
)

/**
 * 批量设置用户偏好请求
 */
@Serializable
data class SetPreferencesRequest(
    val preferences: List<SetPreferenceRequest>
)

/**
 * 导入用户偏好设置请求
 */
@Serializable
data class ImportPreferencesRequest(
    val preferences: String,
    val overwrite: Boolean = true
) 