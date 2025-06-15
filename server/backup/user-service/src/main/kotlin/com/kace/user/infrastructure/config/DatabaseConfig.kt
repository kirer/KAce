package com.kace.user.infrastructure.config

import com.kace.user.infrastructure.persistence.entity.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/**
 * 数据库配置
 */
fun Application.configureDatabase() {
    val logger = LoggerFactory.getLogger("DatabaseConfig")
    
    // 获取数据库配置
    val dbConfig = environment.config.config("database")
    val jdbcUrl = dbConfig.property("jdbcUrl").getString()
    val username = dbConfig.property("username").getString()
    val password = dbConfig.property("password").getString()
    val driverClassName = dbConfig.property("driverClassName").getString()
    val maximumPoolSize = dbConfig.property("maximumPoolSize").getString().toInt()
    
    // 创建数据源
    val dataSource = createDataSource(
        url = jdbcUrl,
        driver = driverClassName,
        username = username,
        password = password,
        maximumPoolSize = maximumPoolSize
    )
    
    // 运行数据库迁移
    runDatabaseMigration(dataSource)
    
    // 连接数据库
    Database.connect(dataSource)
    
    // 创建表（如果不使用Flyway）
    // createTables()
    
    logger.info("数据库配置完成")
}

/**
 * 创建数据源
 */
private fun createDataSource(
    url: String,
    driver: String,
    username: String,
    password: String,
    maximumPoolSize: Int
): DataSource {
    val config = HikariConfig().apply {
        jdbcUrl = url
        driverClassName = driver
        this.username = username
        this.password = password
        this.maximumPoolSize = maximumPoolSize
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    
    return HikariDataSource(config)
}

/**
 * 运行数据库迁移
 */
private fun runDatabaseMigration(dataSource: DataSource) {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
    
    flyway.migrate()
}

/**
 * 创建表（如果不使用Flyway）
 */
private fun createTables() {
    transaction {
        SchemaUtils.create(
            Users,
            UserProfiles,
            Roles,
            Permissions,
            UserRoles,
            RolePermissions,
            Organizations,
            UserOrganizations
        )
    }
} 