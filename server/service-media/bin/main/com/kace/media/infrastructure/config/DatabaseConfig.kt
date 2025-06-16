package com.kace.media.infrastructure.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

/**
 * 配置数据库连接和迁移
 */
fun Application.configureDatabases() {
    val config = environment.config.config("database")
    val driverClassName = config.property("driverClassName").getString()
    val jdbcUrl = config.property("jdbcUrl").getString()
    val username = config.property("username").getString()
    val password = config.property("password").getString()
    val maximumPoolSize = config.property("maximumPoolSize").getString().toInt()
    
    // 创建数据源
    val hikariConfig = HikariConfig().apply {
        this.driverClassName = driverClassName
        this.jdbcUrl = jdbcUrl
        this.username = username
        this.password = password
        this.maximumPoolSize = maximumPoolSize
        this.isAutoCommit = false
        this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        this.validate()
    }
    
    val dataSource = HikariDataSource(hikariConfig)
    
    // 运行数据库迁移
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("db/migration")
        .load()
    
    flyway.migrate()
    
    // 配置Exposed
    Database.connect(dataSource)
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    
    environment.monitor.subscribe(ApplicationStopping) {
        dataSource.close()
    }
} 