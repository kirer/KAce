package com.kace.system.api.response

import com.kace.system.domain.model.ConfigType
import com.kace.system.domain.model.SystemConfig
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 系统配置响应
 *
 * @property id 配置ID
 * @property key 配置键
 * @property value 配置值
 * @property type 配置类型
 * @property description 配置描述
 * @property category 配置分类
 * @property editable 是否可编辑
 * @property createdAt 创建时间
 * @property updatedAt 更新时间
 */
@Serializable
data class SystemConfigResponse(
    val id: Long,
    val key: String,
    val value: String,
    val type: ConfigType,
    val description: String,
    val category: String,
    val editable: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 系统配置分组响应
 *
 * @property category 分类名称
 * @property configs 该分类的配置列表
 */
@Serializable
data class SystemConfigCategoryResponse(
    val category: String,
    val configs: List<SystemConfigResponse>
)

/**
 * 将领域模型转换为响应模型的扩展函数
 */
fun SystemConfig.toResponse(): SystemConfigResponse {
    return SystemConfigResponse(
        id = id ?: 0,
        key = key,
        value = value,
        type = type,
        description = description,
        category = category,
        editable = editable,
        createdAt = createdAt ?: Instant.DISTANT_PAST,
        updatedAt = updatedAt ?: Instant.DISTANT_PAST
    )
}

/**
 * 将配置列表按分类分组的扩展函数
 */
fun List<SystemConfig>.groupByCategory(): List<SystemConfigCategoryResponse> {
    return this.groupBy { it.category }
        .map { (category, configs) ->
            SystemConfigCategoryResponse(
                category = category,
                configs = configs.map { it.toResponse() }
            )
        }
}