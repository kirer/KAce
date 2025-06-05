package com.github.kirer.kace.automation.model

/**
 * 表示不同的手势操作类型
 */
enum class GestureType {
    /**
     * 点击操作
     */
    CLICK,
    
    /**
     * 长按操作
     */
    LONG_CLICK,
    
    /**
     * 滑动操作
     */
    SWIPE,
    
    /**
     * 双指缩放操作
     */
    PINCH,
    
    /**
     * 双指旋转操作
     */
    ROTATE
} 