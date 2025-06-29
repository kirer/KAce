package com.kace.auth.infrastructure.config

import com.kace.auth.api.controller.AuthController
import com.kace.auth.api.controller.PermissionController
import com.kace.auth.api.controller.RoleController
import com.kace.auth.api.controller.UserController
import com.kace.auth.domain.repository.PermissionRepository
import com.kace.auth.domain.repository.RoleRepository
import com.kace.auth.domain.repository.UserRepository
import com.kace.auth.domain.service.AuthService
import com.kace.auth.domain.service.PermissionService
import com.kace.auth.domain.service.RoleService
import com.kace.auth.domain.service.UserService
import com.kace.auth.infrastructure.persistence.repository.PermissionRepositoryImpl
import com.kace.auth.infrastructure.persistence.repository.RoleRepositoryImpl
import com.kace.auth.infrastructure.persistence.repository.UserRepositoryImpl
import com.kace.auth.infrastructure.security.AuthServiceImpl
import com.kace.auth.infrastructure.security.RedisTokenStore
import com.kace.auth.infrastructure.security.TokenStore
import com.kace.auth.infrastructure.service.PermissionServiceImpl
import com.kace.auth.infrastructure.service.RoleServiceImpl
import com.kace.auth.infrastructure.service.UserServiceImpl
import com.kace.common.security.jwt.JwtConfig
import io.ktor.server.application.*
import io.lettuce.core.RedisClient
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * 配置依赖注入
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(authModule)
    }
}

/**
 * 认证服务模块
 */
val authModule = module {
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
    
    // Redis客户端
    single { 
        val redisHost = environment.config.property("redis.host").getString()
        val redisPort = environment.config.property("redis.port").getString().toInt()
        RedisClient.create("redis://$redisHost:$redisPort").connect().async()
    }
    
    // 令牌存储
    single<TokenStore> { RedisTokenStore(get()) }
    
    // 仓库
    single<UserRepository> { UserRepositoryImpl() }
    single<RoleRepository> { RoleRepositoryImpl() }
    single<PermissionRepository> { PermissionRepositoryImpl() }
    
    // 服务
    single<AuthService> { AuthServiceImpl(get(), get(), get()) }
    single<UserService> { UserServiceImpl(get(), get()) }
    single<RoleService> { RoleServiceImpl(get(), get()) }
    single<PermissionService> { PermissionServiceImpl(get()) }
    
    // 控制器
    single { AuthController() }
    single { UserController() }
    single { RoleController() }
    single { PermissionController() }
}
