package com.kace.gateway.filter

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.future.await
import org.koin.java.KoinJavaComponent.getKoin
import org.slf4j.LoggerFactory
import java.time.Duration

/**
 * 限流过滤器
 * 使用Redis实现简单的令牌桶限流算法
 */
class RateLimitFilter {
    // 日志记录器
    private val logger = LoggerFactory.getLogger(RateLimitFilter::class.java)
    
    // Redis客户端
    private val redisCommands by lazy { getKoin().get<RedisAsyncCommands<String, String>>() }
    
    // 限流配置
    private val defaultLimit = 100 // 默认每分钟请求限制
    private val windowSeconds = 60 // 时间窗口（秒）
    
    /**
     * 过滤请求
     * @param call ApplicationCall 应用调用
     * @return Boolean 是否继续处理
     */
    suspend fun filter(call: ApplicationCall): Boolean {
        try {
            // 获取客户端IP
            val clientIp = call.request.origin.remoteHost
            
            // 获取请求路径
            val path = call.request.path()
            
            // 对公开API不进行限流
            if (isPublicPath(path)) {
                return true
            }
            
            // 构建限流键
            val rateLimitKey = "ratelimit:$clientIp:${path.split("/").take(3).joinToString(":")}"
            
            // 检查并增加计数
            val count = checkAndIncrement(rateLimitKey)
            
            // 如果超过限制，则拒绝请求
            if (count > getPathLimit(path)) {
                logger.warn("请求限流: $clientIp - $path - 计数: $count")
                call.respond(
                    HttpStatusCode.TooManyRequests,
                    mapOf("error" to "请求过于频繁，请稍后再试")
                )
                return false
            }
            
            // 添加限流头
            call.response.headers.append("X-RateLimit-Limit", getPathLimit(path).toString())
            call.response.headers.append("X-RateLimit-Remaining", (getPathLimit(path) - count).toString())
            
            return true
        } catch (e: Exception) {
            // 如果限流检查失败，记录错误但允许请求通过
            logger.error("限流检查失败: ${e.message}", e)
            return true
        }
    }
    
    /**
     * 检查并增加计数
     * @param key String 限流键
     * @return Long 当前计数
     */
    private suspend fun checkAndIncrement(key: String): Long {
        // 获取当前计数
        val count = redisCommands.incr(key).await()
        
        // 如果是新键，设置过期时间
        if (count == 1L) {
            redisCommands.expire(key, windowSeconds.toLong()).await()
        }
        
        return count
    }
    
    /**
     * 获取路径的限流值
     * @param path String 请求路径
     * @return Int 限流值
     */
    private fun getPathLimit(path: String): Int {
        // 可以根据不同的路径设置不同的限流值
        return when {
            path.startsWith("/api/auth") -> 30
            path.startsWith("/api/media") -> 50
            else -> defaultLimit
        }
    }
    
    /**
     * 检查是否是公开路径
     * @param path String 请求路径
     * @return Boolean 是否是公开路径
     */
    private fun isPublicPath(path: String): Boolean {
        return path == "/health" || path == "/metrics"
    }
} 