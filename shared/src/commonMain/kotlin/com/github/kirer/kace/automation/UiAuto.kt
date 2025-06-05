package com.github.kirer.kace.automation

import com.github.kirer.kace.automation.model.*
import kotlin.jvm.JvmOverloads

/**
 * 统一的自动化控制器，提供跨平台一致的API
 * 根据当前运行平台，内部会使用对应平台的具体实现
 */
expect class UiAuto() : AutoController {
    /**
     * 创建一个新的选择器
     */
    override fun selector(): ElementSelector
    
    /**
     * 根据文本查找元素
     */
    override fun text(text: String): ElementInfo?
    
    /**
     * 根据ID查找元素
     */
    override fun id(id: String): ElementInfo?
    
    /**
     * 根据类名查找元素
     */
    override fun className(className: String): ElementInfo?
    
    /**
     * 根据选择器查找单个元素
     */
    override fun find(selector: ElementSelector): ElementInfo?
    
    /**
     * 根据选择器查找多个元素
     */
    override fun findAll(selector: ElementSelector): List<ElementInfo>
    
    /**
     * 获取当前屏幕上的所有元素
     */
    override fun getAllElements(): List<ElementInfo>
    
    /**
     * 点击指定元素
     */
    override fun click(element: ElementInfo): Boolean
    
    /**
     * 点击指定坐标
     */
    override fun click(point: Point): Boolean
    
    /**
     * 点击指定坐标，使用指定的点击持续时间
     */
    override fun click(point: Point, duration: Long): Boolean
    
    /**
     * 长按指定元素
     */
    override fun longClick(element: ElementInfo): Boolean
    
    /**
     * 长按指定坐标
     */
    override fun longClick(point: Point): Boolean
    
    /**
     * 长按指定坐标，使用指定的长按持续时间
     */
    override fun longClick(point: Point, duration: Long): Boolean
    
    /**
     * 从一个点滑动到另一个点
     */
    override fun swipe(from: Point, to: Point): Boolean
    
    /**
     * 从一个点滑动到另一个点，使用指定的滑动持续时间和步骤数
     */
    override fun swipe(from: Point, to: Point, duration: Long, steps: Int): Boolean
    
    /**
     * 在指定元素上输入文本
     */
    override fun input(element: ElementInfo, text: String): Boolean
    
    /**
     * 直接输入文本（在当前焦点的元素上）
     */
    override fun input(text: String): Boolean
    
    /**
     * 按下指定的按键
     */
    override fun pressKey(keyCode: Int): Boolean
    
    /**
     * 返回操作
     */
    override fun back(): Boolean
    
    /**
     * 主页操作
     */
    override fun home(): Boolean
    
    /**
     * 最近任务操作
     */
    override fun recents(): Boolean
    
    /**
     * 截取屏幕
     */
    override fun screenshot(): ByteArray
    
    /**
     * 执行手势操作
     */
    override fun gesture(gesture: Gesture): Boolean
    
    /**
     * 等待元素出现
     */
    override fun waitFor(selector: ElementSelector, timeout: Long, interval: Long): ElementInfo?
    
    /**
     * 等待元素消失
     */
    override fun waitForGone(selector: ElementSelector, timeout: Long, interval: Long): Boolean
    
    /**
     * 获取设备屏幕尺寸
     */
    override fun getScreenSize(): Rect
} 