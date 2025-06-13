package com.kace.analytics.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory

/**
 * 配置应用安全
 */
fun Application.configureSecurity() {
    val config = ConfigFactory.load()
    val jwtConfig = config.getConfig("security.jwt")
    
    val secret = jwtConfig.getString("secret")
    val issuer = jwtConfig.getString("issuer")
    val audience = jwtConfig.getString("audience")
    
    install(Authentication) {
        jwt {
            realm = jwtConfig.getString("realm")
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
} 