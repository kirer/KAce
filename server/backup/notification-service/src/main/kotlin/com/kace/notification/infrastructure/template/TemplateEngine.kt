package com.kace.notification.infrastructure.template

/**
 * 模板引擎接口
 */
interface TemplateEngine {
    /**
     * 渲染模板
     * 
     * @param template 模板内容
     * @param variables 模板变量
     * @return 渲染后的内容
     */
    fun render(template: String, variables: Map<String, Any>): String
} 