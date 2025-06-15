package com.kace.notification.infrastructure.config

import com.kace.notification.domain.repository.NotificationChannelRepository
import com.kace.notification.domain.repository.NotificationPreferenceRepository
import com.kace.notification.domain.repository.NotificationRepository
import com.kace.notification.domain.repository.NotificationTemplateRepository
import com.kace.notification.domain.service.NotificationChannelService
import com.kace.notification.domain.service.NotificationPreferenceService
import com.kace.notification.domain.service.NotificationService
import com.kace.notification.domain.service.NotificationTemplateService
import com.kace.notification.infrastructure.messaging.NotificationSender
import com.kace.notification.infrastructure.messaging.RabbitMQClient
import com.kace.notification.infrastructure.messaging.RabbitMQClientImpl
import com.kace.notification.infrastructure.messaging.RabbitMQNotificationSender
import com.kace.notification.infrastructure.persistence.repository.NotificationChannelRepositoryImpl
import com.kace.notification.infrastructure.persistence.repository.NotificationPreferenceRepositoryImpl
import com.kace.notification.infrastructure.persistence.repository.NotificationRepositoryImpl
import com.kace.notification.infrastructure.persistence.repository.NotificationTemplateRepositoryImpl
import com.kace.notification.infrastructure.service.NotificationChannelServiceImpl
import com.kace.notification.infrastructure.service.NotificationPreferenceServiceImpl
import com.kace.notification.infrastructure.service.NotificationServiceImpl
import com.kace.notification.infrastructure.service.NotificationTemplateServiceImpl
import com.kace.notification.infrastructure.template.FreemarkerTemplateEngine
import com.kace.notification.infrastructure.template.TemplateEngine
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * Koin依赖注入配置
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(notificationModule)
    }
}

/**
 * 通知服务模块
 */
val notificationModule = module {
    // 配置
    single {
        val config = environment?.config
        val host = config?.property("rabbitmq.host")?.getString() ?: "localhost"
        val port = config?.property("rabbitmq.port")?.getString()?.toInt() ?: 5672
        val username = config?.property("rabbitmq.username")?.getString() ?: "guest"
        val password = config?.property("rabbitmq.password")?.getString() ?: "guest"
        
        RabbitMQClientImpl(host, port, username, password)
    }
    
    // 仓库
    single<NotificationRepository> { NotificationRepositoryImpl() }
    single<NotificationTemplateRepository> { NotificationTemplateRepositoryImpl() }
    single<NotificationPreferenceRepository> { NotificationPreferenceRepositoryImpl() }
    single<NotificationChannelRepository> { NotificationChannelRepositoryImpl() }
    
    // 模板引擎
    single<TemplateEngine> { FreemarkerTemplateEngine() }
    
    // 消息发送
    single<NotificationSender> { RabbitMQNotificationSender(get(), get()) }
    
    // 服务
    single<NotificationTemplateService> { NotificationTemplateServiceImpl(get(), get()) }
    single<NotificationPreferenceService> { NotificationPreferenceServiceImpl(get()) }
    single<NotificationChannelService> { NotificationChannelServiceImpl(get()) }
    single<NotificationService> { NotificationServiceImpl(get(), get(), get(), get()) }
} 