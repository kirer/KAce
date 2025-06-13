package com.kace.user.infrastructure.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/**
 * 配置数据库
 */
fun Application.configureDatabase() {
    val logger = LoggerFactory.getLogger("DatabaseConfig")
    
    // 创建数据源
    val dataSource = createDataSource(environment)
    
    // 初始化数据库
    Database.connect(dataSource)
    
    // 执行数据库迁移
    migrateDatabase(dataSource)
    
    logger.info("数据库配置完成")
}

/**
 * 创建数据源
 */
private fun createDataSource(environment: ApplicationEnvironment): DataSource {
    val config = HikariConfig().apply {
        driverClassName = environment.config.property("database.driverClassName").getString()
        jdbcUrl = environment.config.property("database.jdbcUrl").getString()
        username = environment.config.property("database.username").getString()
        password = environment.config.property("database.password").getString()
        maximumPoolSize = environment.config.property("database.maximumPoolSize").getString().toInt()
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    
    return HikariDataSource(config)
}

/**
 * 执行数据库迁移
 */
private fun migrateDatabase(dataSource: DataSource) {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
    
    flyway.migrate()
} 