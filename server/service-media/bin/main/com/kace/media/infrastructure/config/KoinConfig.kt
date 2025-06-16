package com.kace.media.infrastructure.config

import com.kace.media.api.controller.CdnController
import com.kace.media.api.controller.MediaPermissionController
import com.kace.media.domain.repository.MediaFolderRepository
import com.kace.media.domain.repository.MediaPermissionRepository
import com.kace.media.domain.repository.MediaProcessingTaskRepository
import com.kace.media.domain.repository.MediaRepository
import com.kace.media.domain.service.*
import com.kace.media.domain.service.impl.MediaFolderServiceImpl
import com.kace.media.domain.service.impl.MediaPermissionServiceImpl
import com.kace.media.domain.service.impl.MediaProcessingServiceImpl
import com.kace.media.domain.service.impl.MediaServiceImpl
import com.kace.media.infrastructure.cdn.DefaultCdnService
import com.kace.media.infrastructure.persistence.repository.MediaFolderRepositoryImpl
import com.kace.media.infrastructure.persistence.repository.MediaPermissionRepositoryImpl
import com.kace.media.infrastructure.persistence.repository.MediaProcessingTaskRepositoryImpl
import com.kace.media.infrastructure.persistence.repository.MediaRepositoryImpl
import com.kace.media.infrastructure.storage.LocalStorageService
import com.kace.media.infrastructure.storage.MinioStorageService
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * 配置Koin依赖注入
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(mediaModule)
    }
}

/**
 * 媒体服务模块
 */
val mediaModule = module {
    // 仓库
    single<MediaRepository> { MediaRepositoryImpl() }
    single<MediaFolderRepository> { MediaFolderRepositoryImpl() }
    single<MediaProcessingTaskRepository> { MediaProcessingTaskRepositoryImpl() }
    single<MediaPermissionRepository> { MediaPermissionRepositoryImpl() }
    
    // CDN服务
    single<CdnService> { DefaultCdnService(environment) }
    
    // 存储服务
    single<StorageService> { 
        val config = environment.config.config("storage")
        val storageType = config.property("type").getString()
        
        when (storageType) {
            "minio" -> MinioStorageService(environment.config)
            else -> LocalStorageService(environment.config)
        }
    }
    
    // 业务服务
    single<MediaService> { MediaServiceImpl(get(), get(), get()) }
    single<MediaFolderService> { MediaFolderServiceImpl(get(), get()) }
    single<MediaProcessingService> { MediaProcessingServiceImpl(get(), get(), get()) }
    single<MediaPermissionService> { MediaPermissionServiceImpl(get(), get()) }
    
    // 控制器
    single { MediaPermissionController(get()) }
    single { CdnController(get()) }
} 