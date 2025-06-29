package com.kace.auth.infrastructure.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.jsonwebtoken.JwtException
import org.koin.ktor.ext.inject
import com.kace.common.security.jwt.JwtConfig

/**
 * 安全配置
 */
fun Application.configureSecurity() {
    val jwtConfig by inject<JwtConfig>()
    
    install(Authentication) {
        jwt("auth-jwt") {
            verifier {
                try {
                    jwtConfig.parseToken(it.payload)
                    true
                } catch (e: JwtException) {
                    false
                }
            }
            validate { credential ->
                if (jwtConfig.validateToken(credential.payload)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
