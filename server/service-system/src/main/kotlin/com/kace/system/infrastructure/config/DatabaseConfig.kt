package com.kace.system.infrastructure.config

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.config4k.extract
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/**
 * 数据库配置类
 */
object DatabaseConfig {
    private val logger = LoggerFactory.getLogger(DatabaseConfig::class.java)
    private val config = ConfigFactory.load()
    
    /**
     * 初始化数据库连接
     */
    fun init() {
        logger.info("初始化系统服务数据库...")
        
        val dataSource = createDataSource()
        runMigrations(dataSource)
        connectExposedToDataSource(dataSource)
        
        logger.info("系统服务数据库初始化完成")
    }
    
    /**
     * 创建数据源
     */
    private fun createDataSource(): DataSource {
        val dbConfig = config.extract<DatabaseSettings>("database")
        logger.info("配置数据库连接: ${dbConfig.jdbcUrl}")

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbConfig.jdbcUrl
            username = dbConfig.username
            password = dbConfig.password
            driverClassName = dbConfig.driverClassName
            maximumPoolSize = dbConfig.poolSize
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(hikariConfig)
    }
    
    /**
     * 运行数据库迁移
     */
    private fun runMigrations(dataSource: DataSource) {
        logger.info("执行数据库迁移")
        
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            
        flyway.migrate()
    }
    
    /**
     * 将Exposed连接到数据源
     */
    private fun connectExposedToDataSource(dataSource: DataSource) {
        logger.info("连接Exposed到数据源")
        Database.connect(dataSource)
    }
}

/**
 * 数据库设置数据类
 */
data class DatabaseSettings(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val driverClassName: String,
    val poolSize: Int
)