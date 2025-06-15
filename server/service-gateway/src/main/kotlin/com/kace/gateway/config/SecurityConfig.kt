package com.kace.gateway.config

import com.kace.common.security.jwt.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.jsonwebtoken.Jwts
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

/**
 * 配置安全
 */
fun Application.configureSecurity() {
    val logger = LoggerFactory.getLogger("SecurityConfig")
    
    // 配置CORS
    configureCors()
    
    // 配置JWT认证
    configureJwt()
    
    logger.info("安全配置已加载")
}

/**
 * 配置CORS
 */
private fun Application.configureCors() {
    install(CORS) {
        // 允许的主机
        val allowedHosts = environment.config.property("cors.hosts").getList()
        allowedHosts.forEach { host ->
            if (host == "*") {
                anyHost()
            } else {
                host(host)
            }
        }
        
        // 允许的方法
        val allowedMethods = environment.config.property("cors.methods").getList()
            .map { HttpMethod.parse(it) }
        methods.addAll(allowedMethods)
        
        // 允许的头
        val allowedHeaders = environment.config.property("cors.headers").getList()
        allowHeaders(allowedHeaders)
        
        // 允许凭证
        allowCredentials = environment.config.property("cors.allowCredentials").getString().toBoolean()
        
        // 最大缓存时间
        maxAgeInSeconds = environment.config.property("cors.maxAgeInSeconds").getString().toLong()
    }
}

/**
 * 配置JWT认证
 */
private fun Application.configureJwt() {
    val jwtConfig by inject<JwtConfig>()
    val logger = LoggerFactory.getLogger("JwtConfig")
    
    install(Authentication) {
        jwt("auth-jwt") {
            realm = environment.config.property("security.jwt.realm").getString()
            
            verifier {
                try {
                    Jwts.parser()
                        .verifyWith(jwtConfig.getSecretKey())
                        .requireIssuer(jwtConfig.getIssuer())
                        .requireAudience(jwtConfig.getAudience())
                        .build()
                } catch (e: Exception) {
                    logger.error("JWT验证器创建失败: ${e.message}", e)
                    throw e
                }
            }
            
            validate { credential ->
                try {
                    if (credential.payload.audience.contains(jwtConfig.getAudience())) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    logger.error("JWT验证失败: ${e.message}", e)
                    null
                }
            }
            
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token无效或已过期"))
            }
        }
    }
} 