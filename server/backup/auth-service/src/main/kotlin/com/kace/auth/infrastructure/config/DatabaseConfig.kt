package com.kace.auth.infrastructure.config

import com.kace.common.config.DatabaseConfig
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
    
    // 配置数据库连接池
    val hikariConfig = HikariConfig().apply {
        driverClassName = config.property("driverClassName").getString()
        jdbcUrl = config.property("jdbcUrl").getString()
        username = config.property("username").getString()
        password = config.property("password").getString()
        maximumPoolSize = config.property("maximumPoolSize").getString().toInt()
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    
    // 创建数据源
    val dataSource = HikariDataSource(hikariConfig)
    
    // 运行Flyway数据库迁移
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
    
    flyway.migrate()
    
    // 配置Exposed
    Database.connect(dataSource)
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    
    // 注册关闭钩子
    environment.monitor.subscribe(ApplicationStopped) {
        dataSource.close()
    }
}
