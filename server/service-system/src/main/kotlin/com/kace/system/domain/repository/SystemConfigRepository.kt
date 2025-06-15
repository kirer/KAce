package com.kace.system.domain.repository

import com.kace.system.domain.model.SystemConfig
import com.kace.system.domain.model.ConfigType

/**
 * 系统配置仓库接口
 */
interface SystemConfigRepository {
    /**
     * 查找所有系统配置
     *
     * @return 系统配置列表
     */
    suspend fun findAll(): List<SystemConfig>
    
    /**
     * 根据ID查找系统配置
     *
     * @param id 配置ID
     * @return 系统配置，不存在则返回null
     */
    suspend fun findById(id: Long): SystemConfig?
    
    /**
     * 根据键查找系统配置
     *
     * @param key 配置键
     * @return 系统配置，不存在则返回null
     */
    suspend fun findByKey(key: String): SystemConfig?
    
    /**
     * 根据分类查找系统配置
     *
     * @param category 配置分类
     * @return 系统配置列表
     */
    suspend fun findByCategory(category: String): List<SystemConfig>
    
    /**
     * 根据类型查找系统配置
     *
     * @param type 配置类型
     * @return 系统配置列表
     */
    suspend fun findByType(type: ConfigType): List<SystemConfig>
    
    /**
     * 查找可编辑的系统配置
     *
     * @return 可编辑的系统配置列表
     */
    suspend fun findEditable(): List<SystemConfig>
    
    /**
     * 保存系统配置
     *
     * @param config 系统配置
     * @return 保存的系统配置
     */
    suspend fun save(config: SystemConfig): SystemConfig
    
    /**
     * 批量保存系统配置
     *
     * @param configs 系统配置列表
     * @return 保存的系统配置列表
     */
    suspend fun saveAll(configs: List<SystemConfig>): List<SystemConfig>
    
    /**
     * 删除系统配置
     *
     * @param id 配置ID
     * @return 是否删除成功
     */
    suspend fun deleteById(id: Long): Boolean
    
    /**
     * 根据键删除系统配置
     *
     * @param key 配置键
     * @return 是否删除成功
     */
    suspend fun deleteByKey(key: String): Boolean
}