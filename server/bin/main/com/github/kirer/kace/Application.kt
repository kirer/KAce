package com.github.kirer.kace

import com.github.kirer.kace.config.AppConfig
import com.github.kirer.kace.core.installFeatures
import com.github.kirer.kace.plugin.PluginManager
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.logger.slf4jLogger

/**
 * 应用程序入口点
 */
fun main() {
    // 加载配置
    val appConfig = AppConfig.load()
    
    // 启动依赖注入
    startKoin {
        slf4jLogger()
        modules(
            module {
                single { appConfig }
                // 其他依赖注入配置将在这里添加
            }
        )
    }
    
    // 启动服务器
    embeddedServer(
        Netty, 
        port = appConfig.server.port, 
        host = appConfig.server.host
    ) {
        // 安装核心功能
        installFeatures()
        
        // 初始化插件管理器
        val pluginManager = PluginManager()
        pluginManager.initialize()
        
        // 其他初始化代码将在这里添加
    }.start(wait = true)
}