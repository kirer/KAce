package com.kace.user.infrastructure.config

import com.kace.user.domain.repository.OrganizationRepository
import com.kace.user.domain.repository.PermissionRepository
import com.kace.user.domain.repository.RoleRepository
import com.kace.user.domain.repository.UserProfileRepository
import com.kace.user.domain.repository.UserRepository
import com.kace.user.domain.repository.UserCredentialRepository
import com.kace.user.domain.repository.UserPreferenceRepository
import com.kace.user.domain.service.OrganizationService
import com.kace.user.domain.service.PermissionService
import com.kace.user.domain.service.RoleService
import com.kace.user.domain.service.UserService
import com.kace.user.domain.service.AuthenticationService
import com.kace.user.domain.service.AuthorizationService
import com.kace.user.domain.service.UserPreferenceService
import com.kace.user.domain.service.impl.OrganizationServiceImpl
import com.kace.user.domain.service.impl.PermissionServiceImpl
import com.kace.user.domain.service.impl.RoleServiceImpl
import com.kace.user.domain.service.impl.UserServiceImpl
import com.kace.user.domain.service.impl.AuthenticationServiceImpl
import com.kace.user.domain.service.impl.AuthorizationServiceImpl
import com.kace.user.domain.service.impl.UserPreferenceServiceImpl
import com.kace.user.infrastructure.persistence.repository.OrganizationRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.PermissionRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.RoleRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.UserProfileRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.UserRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.JpaUserCredentialRepository
import com.kace.user.infrastructure.persistence.repository.JpaUserPreferenceRepository
import com.kace.user.infrastructure.persistence.repository.UserCredentialRepositoryImpl
import com.kace.user.infrastructure.persistence.repository.UserPreferenceRepositoryImpl
import com.kace.common.security.jwt.JwtConfig
import com.kace.user.infrastructure.security.BCryptPasswordEncoder
import com.kace.user.infrastructure.security.JwtTokenProvider
import com.kace.user.infrastructure.security.PasswordEncoder
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * 配置Koin依赖注入
 */
fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            repositoryModule,
            serviceModule,
            securityModule
        )
    }
}

/**
 * 仓库模块
 */
val repositoryModule = module {
    // JPA仓库
    single<JpaUserCredentialRepository>()
    single<JpaUserPreferenceRepository>()
    
    // 领域仓库实现
    single<UserCredentialRepository> { UserCredentialRepositoryImpl(get(), get()) }
    single<UserPreferenceRepository> { UserPreferenceRepositoryImpl(get()) }
    
    // 其他已有的仓库...
    single<UserRepository> { UserRepositoryImpl() }
    single<UserProfileRepository> { UserProfileRepositoryImpl() }
    single<RoleRepository> { RoleRepositoryImpl() }
    single<PermissionRepository> { PermissionRepositoryImpl() }
    single<OrganizationRepository> { OrganizationRepositoryImpl() }
}

/**
 * 服务模块
 */
val serviceModule = module {
    // 认证和授权服务
    single<AuthenticationService> { AuthenticationServiceImpl(get(), get(), get(), get()) }
    single<AuthorizationService> { AuthorizationServiceImpl(get(), get(), get()) }
    
    // 用户偏好设置服务
    single<UserPreferenceService> { UserPreferenceServiceImpl(get()) }
    
    // 其他已有的服务...
    single<UserService> { UserServiceImpl(get(), get()) }
    single<RoleService> { RoleServiceImpl(get(), get()) }
    single<PermissionService> { PermissionServiceImpl(get()) }
    single<OrganizationService> { OrganizationServiceImpl(get(), get()) }
}

/**
 * 安全模块
 */
val securityModule = module {
    // 密码编码器
    single<PasswordEncoder> { BCryptPasswordEncoder() }
    
    // JWT令牌提供者
    single { JwtTokenProvider(
        secretString = environment.config.property("jwt.secret").getString(),
        tokenValidityInSeconds = environment.config.property("jwt.token-validity-in-seconds").getString().toLong(),
        refreshTokenValidityInSeconds = environment.config.property("jwt.refresh-token-validity-in-seconds").getString().toLong()
    ) }
} 