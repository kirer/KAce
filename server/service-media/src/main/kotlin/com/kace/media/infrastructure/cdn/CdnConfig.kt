package com.kace.media.infrastructure.cdn

import io.ktor.server.application.*

/**
 * CDN提供商枚举
 */
enum class CdnProvider {
    NONE,       // 不使用CDN
    CLOUDFRONT, // AWS CloudFront
    CLOUDFLARE, // Cloudflare
    CUSTOM      // 自定义CDN
}

/**
 * CDN配置类
 */
data class CdnConfig(
    val enabled: Boolean,
    val provider: CdnProvider,
    val domain: String,
    val secretKey: String?,
    val keyPairId: String?,
    val privateKeyPath: String?,
    val urlTtlSeconds: Int,
    val signedUrls: Boolean
)

/**
 * 从应用程序配置中解析CDN配置
 */
fun Application.cdnConfig(): CdnConfig {
    val config = environment.config.config("cdn")
    
    return CdnConfig(
        enabled = config.property("enabled").getString().toBoolean(),
        provider = CdnProvider.valueOf(config.property("provider").getString().uppercase()),
        domain = config.property("domain").getString(),
        secretKey = config.propertyOrNull("secretKey")?.getString(),
        keyPairId = config.propertyOrNull("keyPairId")?.getString(),
        privateKeyPath = config.propertyOrNull("privateKeyPath")?.getString(),
        urlTtlSeconds = config.propertyOrNull("urlTtlSeconds")?.getString()?.toInt() ?: 3600,
        signedUrls = config.propertyOrNull("signedUrls")?.getString()?.toBoolean() ?: false
    )
} 