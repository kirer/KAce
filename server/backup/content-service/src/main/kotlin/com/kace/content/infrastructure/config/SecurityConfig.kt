package com.kace.content.infrastructure.config

import com.kace.common.security.jwt.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

/**
 * 配置安全
 */
fun Application.configureSecurity() {
    val jwtConfig by inject<JwtConfig>()
    
    authentication {
        jwt("jwt") {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.request.headers["Authorization"]?.let {
                    if (it.startsWith("Bearer ")) {
                        throw Exception("令牌无效或已过期")
                    }
                }
                throw Exception("缺少认证令牌")
            }
        }
    }
} 