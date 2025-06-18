package com.github.kirer.kace.di

import com.github.kirer.kace.log.InMemoryLogStorage
import com.github.kirer.kace.log.LogService
import com.github.kirer.kace.log.LogStorage
import org.koin.dsl.module

/**
 * 日志模块依赖注入配置
 */
val logModule = module {
    // 单例日志存储
    single<LogStorage> { InMemoryLogStorage() }
    
    // 日志服务
    single { LogService() }
} 