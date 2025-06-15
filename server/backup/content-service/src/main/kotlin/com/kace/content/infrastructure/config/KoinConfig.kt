package com.kace.content.infrastructure.config

import com.kace.content.domain.repository.*
import com.kace.content.domain.service.*
import com.kace.content.domain.service.impl.*
import com.kace.content.infrastructure.persistence.repository.*
import com.kace.content.infrastructure.search.ElasticsearchContentSearchClient
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import javax.sql.DataSource

/**
 * 应用配置
 */
fun Application.configureKoin() {
    val database = configureDatabase()
    org.koin.ktor.plugin.Koin {
        modules(contentServiceModule(database))
    }
}

/**
 * 配置数据库
 */
private fun Application.configureDatabase(): Database {
    val dataSource = hikariDataSource()
    return Database.connect(dataSource)
}

/**
 * 配置数据源
 */
private fun Application.hikariDataSource(): DataSource {
    val config = environment.config.config("database")
    val hikariConfig = HikariConfig().apply {
        driverClassName = config.property("driver").getString()
        jdbcUrl = config.property("url").getString()
        username = config.property("user").getString()
        password = config.property("password").getString()
        maximumPoolSize = config.property("pool_size").getString().toInt()
    }
    return HikariDataSource(hikariConfig)
}

/**
 * 依赖注入配置
 */
fun contentServiceModule(database: Database) = module {
    // 仓库
    single<ContentTypeRepository> { ContentTypeRepositoryImpl(database) }
    single<ContentRepository> { ContentRepositoryImpl(database) }
    single<ContentVersionRepository> { ContentVersionRepositoryImpl(database) }
    single<CategoryRepository> { CategoryRepositoryImpl(database) }
    single<TagRepository> { TagRepositoryImpl(database) }
    single<ContentRelationRepository> { ContentRelationRepositoryImpl(database) }
    single<ContentPermissionRepository> { ContentPermissionRepositoryImpl(database) }

    // 搜索客户端
    single { ElasticsearchContentSearchClient() }
    
    // 服务
    single<ContentTypeService> { ContentTypeServiceImpl(get()) }
    single<ContentService> { ContentServiceImpl(get(), get()) }
    single<ContentVersionService> { ContentVersionServiceImpl(get()) }
    single<CategoryService> { CategoryServiceImpl(get()) }
    single<TagService> { TagServiceImpl(get()) }
    single<ContentSearchService> { ContentSearchServiceImpl(get()) }
    single<ContentRelationService> { ContentRelationServiceImpl(get()) }
    single<ContentPermissionService> { ContentPermissionServiceImpl(get()) }
} 