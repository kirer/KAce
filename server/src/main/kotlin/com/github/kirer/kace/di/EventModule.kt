package com.github.kirer.kace.di

import com.github.kirer.kace.event.EventBus
import com.github.kirer.kace.event.EventStorage
import com.github.kirer.kace.event.InMemoryEventStorage
import org.koin.dsl.module

/**
 * 事件模块依赖注入配置
 */
val eventModule = module {
    // 事件总线
    single { EventBus() }
    
    // 事件存储
    single<EventStorage> { InMemoryEventStorage() }
} 