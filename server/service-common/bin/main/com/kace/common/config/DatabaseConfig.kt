package com.kace.common.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

/**
 * 数据库配置工具类
 */
object DatabaseConfig {
    
    /**
     * 配置并返回数据源
     */
    fun createDataSource(
        driverClassName: String,
        jdbcUrl: String,
        username: String,
        password: String,
        maximumPoolSize: Int
    ): DataSource {
        val config = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            this.maximumPoolSize = maximumPoolSize
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            this.validate()
        }
        return HikariDataSource(config)
    }
    
    /**
     * 配置并连接数据库
     */
    fun connect(dataSource: DataSource): Database {
        return Database.connect(dataSource)
    }
} 