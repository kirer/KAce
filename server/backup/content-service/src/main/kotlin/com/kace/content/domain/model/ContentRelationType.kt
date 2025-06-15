package com.kace.content.domain.model

/**
 * 内容关联类型
 */
enum class ContentRelationType {
    /**
     * 引用关系
     */
    REFERENCE,
    
    /**
     * 父子关系
     */
    PARENT_CHILD,
    
    /**
     * 相关内容
     */
    RELATED,
    
    /**
     * 翻译关系
     */
    TRANSLATION,
    
    /**
     * 版本关系
     */
    VERSION,
    
    /**
     * 自定义关系
     */
    CUSTOM
} 