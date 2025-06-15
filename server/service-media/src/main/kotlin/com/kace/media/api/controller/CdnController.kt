package com.kace.media.api.controller

import com.kace.common.exception.BadRequestException
import com.kace.common.response.ApiResponse
import com.kace.media.domain.service.CdnService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

/**
 * CDN控制器
 */
class CdnController(private val cdnService: CdnService) {
    
    /**
     * 生成CDN URL
     */
    suspend fun generateUrl(call: ApplicationCall) {
        val objectKey = call.parameters["objectKey"] ?: throw BadRequestException("对象键不能为空")
        val expirationSeconds = call.request.queryParameters["expirySeconds"]?.toIntOrNull()
        
        val url = cdnService.generateUrl(objectKey, expirationSeconds)
        call.respond(HttpStatusCode.OK, ApiResponse.success(UrlResponse(url)))
    }
    
    /**
     * 生成CDN公共URL（不含签名）
     */
    suspend fun generatePublicUrl(call: ApplicationCall) {
        val objectKey = call.parameters["objectKey"] ?: throw BadRequestException("对象键不能为空")
        
        val url = cdnService.generatePublicUrl(objectKey)
        call.respond(HttpStatusCode.OK, ApiResponse.success(UrlResponse(url)))
    }
    
    /**
     * 无效化CDN缓存
     */
    suspend fun invalidateCache(call: ApplicationCall) {
        val request = call.receive<InvalidateCacheRequest>()
        
        if (request.objectKeys.isEmpty()) {
            throw BadRequestException("至少需要提供一个对象键")
        }
        
        val invalidationId = cdnService.invalidate(request.objectKeys)
        call.respond(HttpStatusCode.OK, ApiResponse.success(InvalidationResponse(invalidationId)))
    }
    
    /**
     * 获取CDN配置信息
     */
    suspend fun getCdnInfo(call: ApplicationCall) {
        val isValid = cdnService.isConfigValid()
        val domain = cdnService.getCdnDomain()
        
        call.respond(HttpStatusCode.OK, ApiResponse.success(CdnInfoResponse(
            isEnabled = domain.isNotBlank(),
            isConfigValid = isValid,
            cdnDomain = domain
        )))
    }
}

/**
 * URL响应
 */
@Serializable
data class UrlResponse(val url: String)

/**
 * 无效化CDN缓存请求
 */
@Serializable
data class InvalidateCacheRequest(val objectKeys: List<String>)

/**
 * 无效化响应
 */
@Serializable
data class InvalidationResponse(val invalidationId: String?)

/**
 * CDN配置信息响应
 */
@Serializable
data class CdnInfoResponse(
    val isEnabled: Boolean,
    val isConfigValid: Boolean,
    val cdnDomain: String
) 