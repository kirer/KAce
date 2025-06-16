package com.kace.analytics.config

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

/**
 * 配置数据库连接
 */
fun Application.configureDatabases() {
    val config = ConfigFactory.load()
    val dbConfig = config.getConfig("database")
    
    val dataSource = createHikariDataSource(
        url = dbConfig.getString("jdbcUrl"),
        driver = dbConfig.getString("driverClassName"),
        username = dbConfig.getString("username"),
        password = dbConfig.getString("password"),
        poolSize = dbConfig.getInt("maximumPoolSize")
    )
    
    // 初始化Flyway数据库迁移
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("db/migration")
        .load()
    flyway.migrate()
    
    // 连接到Exposed ORM
    Database.connect(dataSource)
}

/**
 * 创建HikariCP数据源
 */
private fun createHikariDataSource(
    url: String,
    driver: String,
    username: String,
    password: String,
    poolSize: Int
): DataSource {
    val config = HikariConfig().apply {
        jdbcUrl = url
        driverClassName = driver
        this.username = username
        this.password = password
        maximumPoolSize = poolSize
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    
    return HikariDataSource(config)
} 