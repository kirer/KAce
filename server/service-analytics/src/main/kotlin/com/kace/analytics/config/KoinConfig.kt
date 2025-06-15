package com.kace.analytics.config

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import com.kace.analytics.domain.service.*
import com.kace.analytics.domain.repository.*
import com.kace.analytics.infrastructure.persistence.repository.*
import com.kace.analytics.infrastructure.collector.*
import com.kace.analytics.infrastructure.processor.*
import com.kace.analytics.infrastructure.reporter.*

/**
 * 配置Koin依赖注入
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(analyticsModule)
    }
}

/**
 * 分析服务的Koin模块
 */
val analyticsModule = module {
    // 仓库
    single<EventRepository> { EventRepositoryImpl() }
    single<MetricRepository> { MetricRepositoryImpl() }
    single<ReportRepository> { ReportRepositoryImpl() }
    
    // 服务
    single { EventService(get()) }
    single { MetricService(get()) }
    single { ReportService(get()) }
    
    // 数据收集器
    single { EventCollector(get()) }
    single { PageViewCollector(get()) }
    single { UserActivityCollector(get()) }
    
    // 数据处理器
    single { EventProcessor(get()) }
    single { AggregationProcessor(get()) }
    
    // 报表生成器
    single { DashboardReporter(get()) }
    single { ExportReporter(get()) }
} 