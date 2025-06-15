package com.kace.gateway.filter

import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*

/**
 * 认证过滤器
 * 用于处理请求的认证信息
 */
class AuthFilter {
    /**
     * 过滤请求
     * @param call ApplicationCall 应用调用
     * @return Boolean 是否继续处理
     */
    suspend fun filter(call: ApplicationCall): Boolean {
        // 获取请求路径
        val path = call.request.path()
        
        // 公开路径不需要认证
        if (isPublicPath(path)) {
            return true
        }
        
        // 获取JWT主体
        val principal = call.principal<JWTPrincipal>()
        if (principal == null) {
            // 如果需要认证但没有JWT主体，则拒绝请求
            // 注意：实际上这不应该发生，因为Ktor的认证插件会拦截未认证的请求
            return false
        }
        
        // 可以在这里添加额外的权限检查
        // 例如检查用户角色、权限等
        
        return true
    }
    
    /**
     * 检查是否是公开路径
     * @param path String 请求路径
     * @return Boolean 是否是公开路径
     */
    private fun isPublicPath(path: String): Boolean {
        val publicPaths = listOf(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/health",
            "/metrics"
        )
        
        return publicPaths.any { path.startsWith(it) }
    }
} 