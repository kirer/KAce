package com.kace.system.domain.service

import com.kace.system.domain.model.SystemConfig
import com.kace.system.domain.model.ConfigType
import kotlinx.datetime.Instant

/**
 * 系统配置服务接口
 */
interface SystemConfigService {
    /**
     * 获取所有系统配置
     *
     * @return 系统配置列表
     */
    suspend fun getAllConfigs(): List<SystemConfig>
    
    /**
     * 根据配置键获取系统配置
     *
     * @param key 配置键
     * @return 系统配置，如果不存在则返回null
     */
    suspend fun getConfigByKey(key: String): SystemConfig?
    
    /**
     * 根据配置分类获取系统配置
     *
     * @param category 配置分类
     * @return 系统配置列表
     */
    suspend fun getConfigsByCategory(category: String): List<SystemConfig>
    
    /**
     * 创建系统配置
     *
     * @param config 系统配置
     * @return 创建的系统配置
     */
    suspend fun createConfig(config: SystemConfig): SystemConfig
    
    /**
     * 更新系统配置
     *
     * @param key 配置键
     * @param value 配置值
     * @return 更新的系统配置，如果配置不存在则返回null
     */
    suspend fun updateConfig(key: String, value: String): SystemConfig?
    
    /**
     * 删除系统配置
     *
     * @param key 配置键
     * @return 是否删除成功
     */
    suspend fun deleteConfig(key: String): Boolean
    
    /**
     * 批量创建或更新系统配置
     *
     * @param configs 系统配置列表
     * @return 创建或更新的系统配置列表
     */
    suspend fun batchUpsertConfigs(configs: List<SystemConfig>): List<SystemConfig>
    
    /**
     * 根据类型获取系统配置
     *
     * @param type 配置类型
     * @return 系统配置列表
     */
    suspend fun getConfigsByType(type: ConfigType): List<SystemConfig>
    
    /**
     * 获取可编辑的系统配置
     *
     * @return 可编辑的系统配置列表
     */
    suspend fun getEditableConfigs(): List<SystemConfig>
}