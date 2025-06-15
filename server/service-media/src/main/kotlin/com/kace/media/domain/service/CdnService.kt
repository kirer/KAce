package com.kace.media.domain.service

/**
 * CDN服务接口
 */
interface CdnService {
    /**
     * 生成CDN URL
     *
     * @param objectKey 对象键（通常是存储路径）
     * @param expirationSeconds URL有效期（秒），如果为null则使用配置默认值
     * @return CDN URL
     */
    fun generateUrl(objectKey: String, expirationSeconds: Int? = null): String
    
    /**
     * 生成CDN URL（不含签名）
     *
     * @param objectKey 对象键（通常是存储路径）
     * @return CDN URL（不含签名）
     */
    fun generatePublicUrl(objectKey: String): String
    
    /**
     * 无效化CDN缓存
     *
     * @param objectKeys 需要无效化的对象键列表
     * @return 无效化请求ID，如果不支持则返回null
     */
    suspend fun invalidate(objectKeys: List<String>): String?
    
    /**
     * 检查CDN配置是否有效
     *
     * @return 配置是否有效
     */
    fun isConfigValid(): Boolean
    
    /**
     * 获取CDN域名
     *
     * @return CDN域名
     */
    fun getCdnDomain(): String
} 