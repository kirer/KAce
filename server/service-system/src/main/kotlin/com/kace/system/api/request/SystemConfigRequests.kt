package com.kace.system.api.request

import com.kace.system.domain.model.ConfigType
import kotlinx.serialization.Serializable

/**
 * 创建系统配置请求
 *
 * @property key 配置键
 * @property value 配置值
 * @property type 配置类型
 * @property description 配置描述
 * @property category 配置分类
 * @property editable 是否可编辑
 */
@Serializable
data class CreateSystemConfigRequest(
    val key: String,
    val value: String,
    val type: ConfigType,
    val description: String = "",
    val category: String = "DEFAULT",
    val editable: Boolean = true
)

/**
 * 更新系统配置请求
 *
 * @property value 配置值
 */
@Serializable
data class UpdateSystemConfigRequest(
    val value: String
)

/**
 * 批量更新系统配置请求
 *
 * @property configs 配置列表
 */
@Serializable
data class BatchUpdateSystemConfigsRequest(
    val configs: List<ConfigItem>
) {
    /**
     * 配置项
     *
     * @property key 配置键
     * @property value 配置值
     */
    @Serializable
    data class ConfigItem(
        val key: String,
        val value: String
    )
}