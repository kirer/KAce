package com.kace.media.infrastructure.config

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kace.media.api.routes.*
import org.koin.ktor.ext.inject

/**
 * 配置序列化
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

/**
 * 配置安全
 */
fun Application.configureSecurity() {
    val config = environment.config.config("security.jwt")
    val secret = config.property("secret").getString()
    val issuer = config.property("issuer").getString()
    val audience = config.property("audience").getString()
    val realm = config.property("realm").getString()
    
    install(Authentication) {
        jwt("auth-jwt") {
            realm = realm
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(audience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
    
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
}

/**
 * 配置监控
 */
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "500: ${cause.message ?: "Internal Server Error"}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}

/**
 * 配置路由
 */
fun Application.configureRouting() {
    // 配置媒体基本功能路由
    configureMediaRoutes()
    configureMediaFolderRoutes()
    configureMediaProcessingRoutes()
    
    // 配置媒体权限和CDN路由
    configureMediaPermissionRoutes()
    configureCdnRoutes()
} 