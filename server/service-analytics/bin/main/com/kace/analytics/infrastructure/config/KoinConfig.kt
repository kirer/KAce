package com.kace.analytics.infrastructure.config

import com.kace.analytics.api.controller.EventController
import com.kace.analytics.api.controller.MetricController
import com.kace.analytics.api.controller.ReportController
import com.kace.analytics.api.controller.ReportExportController
import com.kace.analytics.domain.repository.EventRepository
import com.kace.analytics.domain.repository.MetricRepository
import com.kace.analytics.domain.repository.ReportRepository
import com.kace.analytics.domain.service.EventService
import com.kace.analytics.domain.service.MetricService
import com.kace.analytics.domain.service.ReportService
import com.kace.analytics.infrastructure.persistence.repository.EventRepositoryImpl
import com.kace.analytics.infrastructure.persistence.repository.MetricRepositoryImpl
import com.kace.analytics.infrastructure.persistence.repository.ReportRepositoryImpl
import com.kace.analytics.infrastructure.service.EventServiceImpl
import com.kace.analytics.infrastructure.service.MetricServiceImpl
import com.kace.analytics.infrastructure.service.ReportServiceImpl
import org.koin.dsl.module

/**
 * Koin依赖注入配置
 */
val analyticsModule = module {
    // 仓库
    single<EventRepository> { EventRepositoryImpl() }
    single<MetricRepository> { MetricRepositoryImpl() }
    single<ReportRepository> { ReportRepositoryImpl() }
    
    // 服务
    single<EventService> { EventServiceImpl(get()) }
    single<MetricService> { MetricServiceImpl(get()) }
    single<ReportService> { ReportServiceImpl(get(), get(), get()) }
    
    // 控制器
    single { EventController(get()) }
    single { MetricController(get()) }
    single { ReportController(get()) }
    single { ReportExportController(get()) }
} 