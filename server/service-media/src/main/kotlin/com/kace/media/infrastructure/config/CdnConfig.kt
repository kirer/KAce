package com.kace.media.infrastructure.config

import com.kace.media.domain.service.CdnService
import com.kace.media.infrastructure.cdn.DefaultCdnService
import io.ktor.server.application.*
import org.slf4j.LoggerFactory

/**
 * 配置CDN服务
 */
fun Application.configureCdn(): CdnService {
    val logger = LoggerFactory.getLogger("CdnConfig")
    val cdnService = DefaultCdnService(this)
    
    if (cdnService.isConfigValid()) {
        val cdnDomain = cdnService.getCdnDomain()
        if (cdnDomain.isNotEmpty()) {
            logger.info("CDN服务已配置，域名: $cdnDomain")
        } else {
            logger.info("CDN服务未启用")
        }
    } else {
        logger.warn("CDN配置无效，请检查配置项")
    }
    
    return cdnService
} 