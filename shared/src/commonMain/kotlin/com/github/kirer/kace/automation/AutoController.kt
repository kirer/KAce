package com.github.kirer.kace.automation

import com.github.kirer.kace.automation.model.*

/**
 * 自动化控制器接口，定义所有平台通用的自动化操作
 */
interface AutoController {

    /**
     * 创建一个元素选择器
     */
    fun selector(): ElementSelector
    
    /**
     * 根据文本查找元素
     */
    fun text(text: String): ElementInfo?
    
    /**
     * 根据ID查找元素
     */
    fun id(id: String): ElementInfo?
    
    /**
     * 根据类名查找元素
     */
    fun className(className: String): ElementInfo?
    
    /**
     * 根据选择器查找单个元素
     */
    fun find(selector: ElementSelector): ElementInfo?
    
    /**
     * 根据选择器查找多个元素
     */
    fun findAll(selector: ElementSelector): List<ElementInfo>
    
    /**
     * 获取当前屏幕上的所有元素
     */
    fun getAllElements(): List<ElementInfo>
    
    /**
     * 点击指定元素
     */
    fun click(element: ElementInfo): Boolean
    
    /**
     * 点击指定坐标
     */
    fun click(point: Point): Boolean
    
    /**
     * 点击指定坐标，使用指定的点击持续时间
     */
    fun click(point: Point, duration: Long): Boolean
    
    /**
     * 长按指定元素
     */
    fun longClick(element: ElementInfo): Boolean
    
    /**
     * 长按指定坐标
     */
    fun longClick(point: Point): Boolean
    
    /**
     * 长按指定坐标，使用指定的长按持续时间
     */
    fun longClick(point: Point, duration: Long): Boolean
    
    /**
     * 从一个点滑动到另一个点
     */
    fun swipe(from: Point, to: Point): Boolean
    
    /**
     * 从一个点滑动到另一个点，使用指定的滑动持续时间和步骤数
     */
    fun swipe(from: Point, to: Point, duration: Long, steps: Int = 10): Boolean
    
    /**
     * 在指定元素上输入文本
     */
    fun input(element: ElementInfo, text: String): Boolean
    
    /**
     * 直接输入文本（在当前焦点的元素上）
     */
    fun input(text: String): Boolean
    
    /**
     * 按下指定的按键
     */
    fun pressKey(keyCode: Int): Boolean
    
    /**
     * 返回操作
     */
    fun back(): Boolean
    
    /**
     * 主页操作
     */
    fun home(): Boolean
    
    /**
     * 最近任务操作
     */
    fun recents(): Boolean
    
    /**
     * 截取屏幕
     */
    fun screenshot(): ByteArray
    
    /**
     * 执行手势操作
     */
    fun gesture(gesture: Gesture): Boolean
    
    /**
     * 等待元素出现
     */
    fun waitFor(selector: ElementSelector, timeout: Long = 10000, interval: Long = 500): ElementInfo?
    
    /**
     * 等待元素消失
     */
    fun waitForGone(selector: ElementSelector, timeout: Long = 10000, interval: Long = 500): Boolean
    
    /**
     * 获取设备屏幕尺寸
     */
    fun getScreenSize(): Rect
} 