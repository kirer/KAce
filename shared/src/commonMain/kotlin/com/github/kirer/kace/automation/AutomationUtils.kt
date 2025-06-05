package com.github.kirer.kace.automation

import com.github.kirer.kace.automation.model.ElementInfo
import com.github.kirer.kace.automation.model.Point
import com.github.kirer.kace.automation.model.Rect

/**
 * 自动化相关的实用工具类
 */
object AutomationUtils {
    /**
     * 默认等待元素超时时间
     */
    const val DEFAULT_TIMEOUT = 10000L
    
    /**
     * 默认等待检查间隔
     */
    const val DEFAULT_INTERVAL = 500L
    
    /**
     * 默认点击持续时间
     */
    const val DEFAULT_CLICK_DURATION = 100L
    
    /**
     * 默认长按持续时间
     */
    const val DEFAULT_LONG_CLICK_DURATION = 1000L
    
    /**
     * 默认滑动持续时间
     */
    const val DEFAULT_SWIPE_DURATION = 300L
    
    /**
     * 默认滑动步骤数
     */
    const val DEFAULT_SWIPE_STEPS = 10
    
    /**
     * 检查两个点是否足够接近
     */
    fun isPointsClose(p1: Point, p2: Point, threshold: Int = 5): Boolean {
        return p1.distanceTo(p2) <= threshold
    }
    
    /**
     * 获取向上滑动的起点和终点
     */
    fun getSwipeUpPoints(bounds: Rect): Pair<Point, Point> {
        val startX = bounds.center.x
        val startY = bounds.bottom - bounds.height / 4
        val endY = bounds.top + bounds.height / 4
        return Point(startX, startY) to Point(startX, endY)
    }
    
    /**
     * 获取向下滑动的起点和终点
     */
    fun getSwipeDownPoints(bounds: Rect): Pair<Point, Point> {
        val (end, start) = getSwipeUpPoints(bounds)
        return start to end
    }
    
    /**
     * 获取向左滑动的起点和终点
     */
    fun getSwipeLeftPoints(bounds: Rect): Pair<Point, Point> {
        val startY = bounds.center.y
        val startX = bounds.right - bounds.width / 4
        val endX = bounds.left + bounds.width / 4
        return Point(startX, startY) to Point(endX, startY)
    }
    
    /**
     * 获取向右滑动的起点和终点
     */
    fun getSwipeRightPoints(bounds: Rect): Pair<Point, Point> {
        val (end, start) = getSwipeLeftPoints(bounds)
        return start to end
    }
    
    /**
     * 在文本中查找匹配文本的元素
     */
    fun findElementWithText(elements: List<ElementInfo>, text: String, exact: Boolean = false): ElementInfo? {
        return elements.find { 
            if (exact) it.text == text || it.desc == text
            else it.text.contains(text, true) || it.desc.contains(text, true)
        }
    }
    
    /**
     * 计算列表中心点的滑动距离
     */
    fun calculateScrollDistance(bounds: Rect, direction: Int): Pair<Point, Point> {
        return when (direction) {
            0 -> getSwipeUpPoints(bounds)    // 向上滑动
            1 -> getSwipeDownPoints(bounds)  // 向下滑动
            2 -> getSwipeLeftPoints(bounds)  // 向左滑动
            3 -> getSwipeRightPoints(bounds) // 向右滑动
            else -> getSwipeUpPoints(bounds) // 默认向上滑动
        }
    }
    
    /**
     * 生成随机点击点，在元素边界内的随机位置
     */
    fun randomPointInBounds(bounds: Rect): Point {
        val randomX = bounds.left + (kotlin.random.Random.nextDouble() * bounds.width).toInt()
        val randomY = bounds.top + (kotlin.random.Random.nextDouble() * bounds.height).toInt()
        return Point(randomX, randomY)
    }
} 