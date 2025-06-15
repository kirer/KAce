package com.kace.media.api.routes

import com.kace.media.api.controller.CdnController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * 配置CDN路由
 */
fun Application.configureCdnRoutes() {
    val cdnController: CdnController by inject()
    
    routing {
        route("/api/v1/cdn") {
            // 获取CDN配置信息
            get {
                cdnController.getCdnInfo(call)
            }
            
            // 生成签名URL
            get("/url/{objectKey...}") {
                cdnController.generateUrl(call)
            }
            
            // 生成公共URL
            get("/public-url/{objectKey...}") {
                cdnController.generatePublicUrl(call)
            }
            
            // 无效化缓存
            post("/invalidate") {
                cdnController.invalidateCache(call)
            }
        }
    }
} 