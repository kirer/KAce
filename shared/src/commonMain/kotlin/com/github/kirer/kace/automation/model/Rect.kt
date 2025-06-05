package com.github.kirer.kace.automation.model

/**
 * 表示屏幕上的一个矩形区域
 */
data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    /**
     * 矩形的宽度
     */
    val width: Int
        get() = right - left
    
    /**
     * 矩形的高度
     */
    val height: Int
        get() = bottom - top
    
    /**
     * 矩形的中心点
     */
    val center: Point
        get() = Point(left + width / 2, top + height / 2)
    
    /**
     * 检查一个点是否在矩形内
     */
    fun contains(point: Point): Boolean {
        return point.x >= left && point.x <= right && point.y >= top && point.y <= bottom
    }
    
    /**
     * 检查一个矩形是否完全包含在此矩形内
     */
    fun contains(rect: Rect): Boolean {
        return left <= rect.left && top <= rect.top && right >= rect.right && bottom >= rect.bottom
    }
    
    /**
     * 检查一个矩形是否与此矩形相交
     */
    fun intersects(rect: Rect): Boolean {
        return !(left > rect.right || right < rect.left || top > rect.bottom || bottom < rect.top)
    }
    
    companion object {
        val EMPTY = Rect(0, 0, 0, 0)
    }
} 