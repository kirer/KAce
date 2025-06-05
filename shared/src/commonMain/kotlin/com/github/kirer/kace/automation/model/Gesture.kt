package com.github.kirer.kace.automation.model

/**
 * 表示一个手势操作
 */
sealed class Gesture(val type: GestureType) {
    
    /**
     * 点击手势
     */
    data class Click(val point: Point, val duration: Long = 100) : Gesture(GestureType.CLICK)
    
    /**
     * 长按手势
     */
    data class LongClick(val point: Point, val duration: Long = 1000) : Gesture(GestureType.LONG_CLICK)
    
    /**
     * 滑动手势
     */
    data class Swipe(
        val start: Point, 
        val end: Point, 
        val duration: Long = 300,
        val steps: Int = 10
    ) : Gesture(GestureType.SWIPE)
    
    /**
     * 缩放手势
     */
    data class Pinch(
        val center: Point,
        val startRadius: Float,
        val endRadius: Float,
        val duration: Long = 300
    ) : Gesture(GestureType.PINCH)
    
    /**
     * 旋转手势
     */
    data class Rotate(
        val center: Point,
        val startAngle: Float,
        val endAngle: Float,
        val radius: Float,
        val duration: Long = 300
    ) : Gesture(GestureType.ROTATE)
} 