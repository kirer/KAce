package com.kace.system.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * 系统配置领域模型
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
data class SystemConfig(
    val id: Long? = null,
    val key: String,
    val value: String,
    val type: ConfigType,
    val description: String = "",
    val category: String = "DEFAULT",
    val editable: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

/**
 * 配置类型
 */
@Serializable
enum class ConfigType {
    STRING,
    NUMBER,
    BOOLEAN,
    JSON,
    DATE,
    EMAIL,
    URL
}
