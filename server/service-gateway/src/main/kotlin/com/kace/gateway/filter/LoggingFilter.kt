package com.kace.gateway.filter

import io.ktor.server.application.*
import io.ktor.server.request.*
import org.slf4j.LoggerFactory

/**
 * 日志过滤器
 * 用于记录请求和响应信息
 */
class LoggingFilter {
    // 日志记录器
    private val logger = LoggerFactory.getLogger(LoggingFilter::class.java)
    
    /**
     * 过滤请求
     * @param call ApplicationCall 应用调用
     * @return Boolean 是否继续处理
     */
    suspend fun filter(call: ApplicationCall): Boolean {
        // 记录请求信息
        val requestId = generateRequestId()
        val httpMethod = call.request.httpMethod.value
        val path = call.request.path()
        val userAgent = call.request.headers["User-Agent"]
        val remoteHost = call.request.origin.remoteHost
        
        logger.info("请求 [$requestId] - $httpMethod $path - 来自 $remoteHost - $userAgent")
        
        // 可以在这里添加请求处理时间统计
        
        return true
    }
    
    /**
     * 生成请求ID
     * @return String 请求ID
     */
    private fun generateRequestId(): String {
        return "req-${System.currentTimeMillis()}-${(0..9999).random()}"
    }
} 