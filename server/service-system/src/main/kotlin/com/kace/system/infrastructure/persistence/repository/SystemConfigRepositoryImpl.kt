package com.kace.system.infrastructure.persistence.repository

import com.kace.system.domain.model.ConfigType
import com.kace.system.domain.model.SystemConfig
import com.kace.system.domain.repository.SystemConfigRepository
import com.kace.system.infrastructure.persistence.entity.SystemConfigs
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * 系统配置仓库实现
 */
class SystemConfigRepositoryImpl : SystemConfigRepository {

    override suspend fun findAll(): List<SystemConfig> = newSuspendedTransaction {
        SystemConfigs.selectAll()
            .map { SystemConfigs.toDomain(it) }
    }

    override suspend fun findById(id: Long): SystemConfig? = newSuspendedTransaction {
        SystemConfigs.select { SystemConfigs.id eq id }
            .singleOrNull()
            ?.let { SystemConfigs.toDomain(it) }
    }

    override suspend fun findByKey(key: String): SystemConfig? = newSuspendedTransaction {
        SystemConfigs.select { SystemConfigs.key eq key }
            .singleOrNull()
            ?.let { SystemConfigs.toDomain(it) }
    }

    override suspend fun findByCategory(category: String): List<SystemConfig> = newSuspendedTransaction {
        SystemConfigs.select { SystemConfigs.category eq category }
            .map { SystemConfigs.toDomain(it) }
    }

    override suspend fun findByType(type: ConfigType): List<SystemConfig> = newSuspendedTransaction {
        SystemConfigs.select { SystemConfigs.type eq type }
            .map { SystemConfigs.toDomain(it) }
    }

    override suspend fun findEditable(): List<SystemConfig> = newSuspendedTransaction {
        SystemConfigs.select { SystemConfigs.editable eq true }
            .map { SystemConfigs.toDomain(it) }
    }

    override suspend fun save(config: SystemConfig): SystemConfig = newSuspendedTransaction {
        val now = Clock.System.now()
        
        // 如果存在ID，则更新，否则插入新记录
        if (config.id != null) {
            SystemConfigs.update({ SystemConfigs.id eq config.id }) {
                it[key] = config.key
                it[value] = config.value
                it[type] = config.type
                it[description] = config.description
                it[category] = config.category
                it[editable] = config.editable
                it[updatedAt] = config.updatedAt?.toJavaInstant() ?: now.toJavaInstant()
            }
            
            findById(config.id)!!
        } else {
            val id = SystemConfigs.insert {
                it[key] = config.key
                it[value] = config.value
                it[type] = config.type
                it[description] = config.description
                it[category] = config.category
                it[editable] = config.editable
                it[createdAt] = config.createdAt?.toJavaInstant() ?: now.toJavaInstant()
                it[updatedAt] = config.updatedAt?.toJavaInstant() ?: now.toJavaInstant()
            } get SystemConfigs.id
            
            config.copy(id = id.value)
        }
    }

    override suspend fun saveAll(configs: List<SystemConfig>): List<SystemConfig> = newSuspendedTransaction {
        configs.map { save(it) }
    }

    override suspend fun deleteById(id: Long): Boolean = newSuspendedTransaction {
        SystemConfigs.deleteWhere { SystemConfigs.id eq id } > 0
    }

    override suspend fun deleteByKey(key: String): Boolean = newSuspendedTransaction {
        SystemConfigs.deleteWhere { SystemConfigs.key eq key } > 0
    }
}