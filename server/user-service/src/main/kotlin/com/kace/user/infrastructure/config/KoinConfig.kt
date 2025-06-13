package com.kace.user.infrastructure.config

import com.kace.user.domain.repository.PermissionRepository
import com.kace.user.domain.repository.RoleRepository
import com.kace.user.domain.repository.UserRepository
import com.kace.user.domain.service.PermissionService
import com.kace.user.domain.service.RoleService
import com.kace.user.domain.service.UserService
import com.kace.user.domain.service.impl.PermissionServiceImpl
import com.kace.user.domain.service.impl.RoleServiceImpl
import com.kace.user.domain.service.impl.UserServiceImpl
import com.kace.user.infrastructure.persistence.repository.PermissionRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.RoleRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.UserRepositoryImpl
import com.kace.common.security.jwt.JwtConfig
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * 配置依赖注入
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(userModule)
    }
}

/**
 * 用户服务模块
 */
val userModule = module {
    // 配置
    single { 
        JwtConfig(
            secret = environment.config.property("security.jwt.secret").getString(),
            issuer = environment.config.property("security.jwt.issuer").getString(),
            audience = environment.config.property("security.jwt.audience").getString(),
            realm = environment.config.property("security.jwt.realm").getString(),
            expirationInMinutes = environment.config.property("security.jwt.expirationInMinutes").getString().toLong()
        )
    }
    
    // 仓库
    single<UserRepository> { UserRepositoryImpl() }
    single<RoleRepository> { RoleRepositoryImpl() }
    single<PermissionRepository> { PermissionRepositoryImpl() }
    
    // 服务
    single<UserService> { UserServiceImpl(get()) }
    single<RoleService> { RoleServiceImpl(get()) }
    single<PermissionService> { PermissionServiceImpl(get()) }
} 