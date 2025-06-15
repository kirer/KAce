package com.kace.system.domain.service.impl

import com.kace.system.domain.model.ConfigType
import com.kace.system.domain.model.SystemConfig
import com.kace.system.domain.repository.SystemConfigRepository
import com.kace.system.domain.service.SystemConfigService
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory

/**
 * 系统配置服务实现
 * 
 * @property repository 系统配置仓库
 */
class SystemConfigServiceImpl(private val repository: SystemConfigRepository) : SystemConfigService {
    
    private val logger = LoggerFactory.getLogger(SystemConfigServiceImpl::class.java)
    
    override suspend fun getAllConfigs(): List<SystemConfig> {
        logger.debug("获取所有系统配置")
        return repository.findAll()
    }

    override suspend fun getConfigByKey(key: String): SystemConfig? {
        logger.debug("根据键获取系统配置: {}", key)
        return repository.findByKey(key)
    }

    override suspend fun getConfigsByCategory(category: String): List<SystemConfig> {
        logger.debug("根据分类获取系统配置: {}", category)
        return repository.findByCategory(category)
    }

    override suspend fun createConfig(config: SystemConfig): SystemConfig {
        logger.info("创建系统配置: {}", config.key)
        val now = Clock.System.now()
        
        // 验证配置键是否已存在
        val existingConfig = repository.findByKey(config.key)
        if (existingConfig != null) {
            logger.warn("配置键已存在: {}", config.key)
            throw IllegalArgumentException("配置键 '${config.key}' 已存在")
        }
        
        val newConfig = config.copy(
            createdAt = now,
            updatedAt = now
        )
        
        return repository.save(newConfig)
    }

    override suspend fun updateConfig(key: String, value: String): SystemConfig? {
        logger.info("更新系统配置: {}", key)
        val existingConfig = repository.findByKey(key)
        
        if (existingConfig == null) {
            logger.warn("配置键不存在: {}", key)
            return null
        }
        
        if (!existingConfig.editable) {
            logger.warn("配置不可编辑: {}", key)
            throw IllegalArgumentException("配置 '${key}' 不可编辑")
        }
        
        val updatedConfig = existingConfig.copy(
            value = value,
            updatedAt = Clock.System.now()
        )
        
        return repository.save(updatedConfig)
    }

    override suspend fun deleteConfig(key: String): Boolean {
        logger.info("删除系统配置: {}", key)
        val existingConfig = repository.findByKey(key)
        
        if (existingConfig == null) {
            logger.warn("配置键不存在: {}", key)
            return false
        }
        
        if (!existingConfig.editable) {
            logger.warn("配置不可删除: {}", key)
            throw IllegalArgumentException("配置 '${key}' 不可删除")
        }
        
        return repository.deleteByKey(key)
    }

    override suspend fun batchUpsertConfigs(configs: List<SystemConfig>): List<SystemConfig> {
        logger.info("批量更新系统配置, 数量: {}", configs.size)
        val now = Clock.System.now()
        
        val processedConfigs = configs.map { config ->
            val existingConfig = repository.findByKey(config.key)
            
            if (existingConfig != null && !existingConfig.editable) {
                logger.warn("配置不可编辑，跳过: {}", config.key)
                existingConfig
            } else if (existingConfig != null) {
                config.copy(
                    id = existingConfig.id,
                    createdAt = existingConfig.createdAt,
                    updatedAt = now
                )
            } else {
                config.copy(
                    createdAt = now,
                    updatedAt = now
                )
            }
        }
        
        return repository.saveAll(processedConfigs)
    }

    override suspend fun getConfigsByType(type: ConfigType): List<SystemConfig> {
        logger.debug("根据类型获取系统配置: {}", type)
        return repository.findByType(type)
    }

    override suspend fun getEditableConfigs(): List<SystemConfig> {
        logger.debug("获取可编辑的系统配置")
        return repository.findEditable()
    }
}