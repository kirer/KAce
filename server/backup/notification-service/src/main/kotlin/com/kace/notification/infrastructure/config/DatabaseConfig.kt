package com.kace.notification.infrastructure.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import javax.sql.DataSource

/**
 * 数据库配置
 */
fun Application.configureDatabases() {
    val config = environment.config
    
    val driverClassName = config.property("database.driverClassName").getString()
    val jdbcUrl = config.property("database.jdbcUrl").getString()
    val username = config.property("database.username").getString()
    val password = config.property("database.password").getString()
    val maximumPoolSize = config.property("database.maximumPoolSize").getString().toInt()
    
    // 创建数据源
    val dataSource = createHikariDataSource(
        url = jdbcUrl,
        driver = driverClassName,
        username = username,
        password = password,
        maximumPoolSize = maximumPoolSize
    )
    
    // 运行数据库迁移
    runFlyway(dataSource)
    
    // 配置Exposed
    Database.connect(dataSource)
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_COMMITTED
    
    log.info("数据库连接已配置: $jdbcUrl")
}

/**
 * 创建HikariCP数据源
 */
private fun createHikariDataSource(
    url: String,
    driver: String,
    username: String,
    password: String,
    maximumPoolSize: Int
): DataSource {
    val config = HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        this.username = username
        this.password = password
        this.maximumPoolSize = maximumPoolSize
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_READ_COMMITTED"
        validate()
    }
    
    return HikariDataSource(config)
}

/**
 * 运行Flyway数据库迁移
 */
private fun runFlyway(dataSource: DataSource) {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("db/migration")
        .load()
    
    flyway.migrate()
} 