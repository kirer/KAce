package com.github.kirer.kace.automation.model

/**
 * 表示屏幕上的一个点
 *
 * @property x X坐标
 * @property y Y坐标
 */
data class Point(
    val x: Int,
    val y: Int
) {
    companion object {
        val ZERO = Point(0, 0)
    }
    
    /**
     * 计算与另一个点的距离
     */
    fun distanceTo(other: Point): Double {
        val dx = x - other.x
        val dy = y - other.y
        return kotlin.math.sqrt((dx * dx + dy * dy).toDouble())
    }
    
    /**
     * 创建一个新点，相对于当前点偏移指定的距离
     */
    fun offset(dx: Int, dy: Int): Point {
        return Point(x + dx, y + dy)
    }
} 