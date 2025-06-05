package com.github.kirer.kace.automation

import com.github.kirer.kace.automation.model.*
import java.awt.Robot
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * 桌面平台的自动化控制器实现
 * 使用Java AWT Robot实现UI自动化
 */
actual class UiAuto : AutoController {
    private val robot = Robot()
    
    init {
        // 设置自动延迟，使操作更稳定
        robot.setAutoDelay(20)
        robot.setAutoWaitForIdle(true)
    }
    
    /**
     * 创建一个新的选择器
     */
    actual override fun selector(): ElementSelector {
        return ElementSelector()
    }
    
    /**
     * 根据文本查找元素
     * 注意：桌面平台上需要使用图像识别或UI Automation API来实现
     */
    actual override fun text(text: String): ElementInfo? {
        // 桌面平台实现需要特定的UI自动化库
        // 这里提供一个简单的占位实现
        return null
    }
    
    /**
     * 根据ID查找元素
     */
    actual override fun id(id: String): ElementInfo? {
        // 桌面平台实现需要特定的UI自动化库
        return null
    }
    
    /**
     * 根据类名查找元素
     */
    actual override fun className(className: String): ElementInfo? {
        // 桌面平台实现需要特定的UI自动化库
        return null
    }
    
    /**
     * 根据选择器查找单个元素
     */
    actual override fun find(selector: ElementSelector): ElementInfo? {
        // 桌面平台实现需要特定的UI自动化库
        return null
    }
    
    /**
     * 根据选择器查找多个元素
     */
    actual override fun findAll(selector: ElementSelector): List<ElementInfo> {
        // 桌面平台实现需要特定的UI自动化库
        return emptyList()
    }
    
    /**
     * 获取当前屏幕上的所有元素
     */
    actual override fun getAllElements(): List<ElementInfo> {
        // 桌面平台实现需要特定的UI自动化库
        return emptyList()
    }
    
    /**
     * 点击指定元素
     */
    actual override fun click(element: ElementInfo): Boolean {
        val center = element.center ?: return false
        return click(center)
    }
    
    /**
     * 点击指定坐标
     */
    actual override fun click(point: Point): Boolean {
        return click(point, AutomationUtils.DEFAULT_CLICK_DURATION)
    }
    
    /**
     * 点击指定坐标，使用指定的点击持续时间
     */
    actual override fun click(point: Point, duration: Long): Boolean {
        robot.mouseMove(point.x, point.y)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        
        if (duration > 0) {
            try {
                Thread.sleep(duration)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        return true
    }
    
    /**
     * 长按指定元素
     */
    actual override fun longClick(element: ElementInfo): Boolean {
        val center = element.center ?: return false
        return longClick(center)
    }
    
    /**
     * 长按指定坐标
     */
    actual override fun longClick(point: Point): Boolean {
        return longClick(point, AutomationUtils.DEFAULT_LONG_CLICK_DURATION)
    }
    
    /**
     * 长按指定坐标，使用指定的长按持续时间
     */
    actual override fun longClick(point: Point, duration: Long): Boolean {
        robot.mouseMove(point.x, point.y)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        
        try {
            Thread.sleep(duration)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        return true
    }
    
    /**
     * 从一个点滑动到另一个点
     */
    actual override fun swipe(from: Point, to: Point): Boolean {
        return swipe(from, to, AutomationUtils.DEFAULT_SWIPE_DURATION, AutomationUtils.DEFAULT_SWIPE_STEPS)
    }
    
    /**
     * 从一个点滑动到另一个点，使用指定的滑动持续时间和步骤数
     */
    actual override fun swipe(from: Point, to: Point, duration: Long, steps: Int): Boolean {
        robot.mouseMove(from.x, from.y)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        
        val sleepTime = duration / steps
        val dx = (to.x - from.x) / steps.toDouble()
        val dy = (to.y - from.y) / steps.toDouble()
        
        for (i in 1 until steps) {
            val x = from.x + (dx * i).toInt()
            val y = from.y + (dy * i).toInt()
            
            robot.mouseMove(x, y)
            
            try {
                Thread.sleep(sleepTime)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
        
        robot.mouseMove(to.x, to.y)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        return true
    }
    
    /**
     * 在指定元素上输入文本
     */
    actual override fun input(element: ElementInfo, text: String): Boolean {
        val center = element.center ?: return false
        click(center)
        return input(text)
    }
    
    /**
     * 直接输入文本（在当前焦点的元素上）
     */
    actual override fun input(text: String): Boolean {
        for (c in text) {
            try {
                typeChar(c)
            } catch (e: Exception) {
                return false
            }
        }
        return true
    }
    
    /**
     * 输入单个字符
     */
    private fun typeChar(c: Char) {
        // 处理特殊字符和常规字符的输入
        val keyCode = KeyEvent.getExtendedKeyCodeForChar(c.code)
        
        if (keyCode == KeyEvent.VK_UNDEFINED) {
            throw IllegalArgumentException("Cannot type character: $c")
        }
        
        val isShiftNeeded = Character.isUpperCase(c) || isShiftChar(c)
        
        if (isShiftNeeded) {
            robot.keyPress(KeyEvent.VK_SHIFT)
        }
        
        robot.keyPress(keyCode)
        robot.keyRelease(keyCode)
        
        if (isShiftNeeded) {
            robot.keyRelease(KeyEvent.VK_SHIFT)
        }
    }
    
    /**
     * 判断是否需要Shift键的字符
     */
    private fun isShiftChar(c: Char): Boolean {
        return "~!@#$%^&*()_+{}|:\"<>?".contains(c)
    }
    
    /**
     * 按下指定的按键
     */
    actual override fun pressKey(keyCode: Int): Boolean {
        robot.keyPress(keyCode)
        robot.keyRelease(keyCode)
        return true
    }
    
    /**
     * 返回操作 (模拟ESC键)
     */
    actual override fun back(): Boolean {
        return pressKey(KeyEvent.VK_ESCAPE)
    }
    
    /**
     * 主页操作 (Windows键)
     */
    actual override fun home(): Boolean {
        robot.keyPress(KeyEvent.VK_WINDOWS)
        robot.keyRelease(KeyEvent.VK_WINDOWS)
        return true
    }
    
    /**
     * 最近任务操作 (Alt+Tab)
     */
    actual override fun recents(): Boolean {
        robot.keyPress(KeyEvent.VK_ALT)
        robot.keyPress(KeyEvent.VK_TAB)
        robot.keyRelease(KeyEvent.VK_TAB)
        robot.keyRelease(KeyEvent.VK_ALT)
        return true
    }
    
    /**
     * 截取屏幕
     */
    actual override fun screenshot(): ByteArray {
        val screenSize = getScreenSize()
        val screenRect = Rectangle(screenSize.left, screenSize.top, screenSize.width, screenSize.height)
        val screenshot = robot.createScreenCapture(screenRect)
        
        return try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            ImageIO.write(screenshot, "PNG", byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            ByteArray(0)
        }
    }
    
    /**
     * 执行手势操作
     */
    actual override fun gesture(gesture: Gesture): Boolean {
        when (gesture) {
            is Gesture.Click -> return click(gesture.point, gesture.duration)
            is Gesture.LongClick -> return longClick(gesture.point, gesture.duration)
            is Gesture.Swipe -> return swipe(gesture.start, gesture.end, gesture.duration, gesture.steps)
            is Gesture.Pinch, is Gesture.Rotate -> return false // 桌面平台通常不支持这些手势
        }
    }
    
    /**
     * 等待元素出现
     */
    actual override fun waitFor(selector: ElementSelector, timeout: Long, interval: Long): ElementInfo? {
        val endTime = System.currentTimeMillis() + timeout
        
        while (System.currentTimeMillis() < endTime) {
            val element = find(selector)
            if (element != null) {
                return element
            }
            
            try {
                Thread.sleep(interval)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
        
        return null
    }
    
    /**
     * 等待元素消失
     */
    actual override fun waitForGone(selector: ElementSelector, timeout: Long, interval: Long): Boolean {
        val endTime = System.currentTimeMillis() + timeout
        
        while (System.currentTimeMillis() < endTime) {
            val element = find(selector)
            if (element == null) {
                return true
            }
            
            try {
                Thread.sleep(interval)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
        
        return false
    }
    
    /**
     * 获取设备屏幕尺寸
     */
    actual override fun getScreenSize(): Rect {
        val displayMode = Toolkit.getDefaultToolkit().screenSize
        return Rect(0, 0, displayMode.width, displayMode.height)
    }
} 