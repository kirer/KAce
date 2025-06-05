package com.github.kirer.kace.automation

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityNodeInfo
import com.github.kirer.kace.automation.model.*

/**
 * Android平台的自动化控制器实现
 * 使用Android的AccessibilityService实现UI自动化
 */
actual class UiAuto : AutoController {
    private var accessibilityService: AccessibilityService? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    
    /**
     * 初始化控制器
     */
    fun initialize(service: AccessibilityService) {
        this.accessibilityService = service
    }
    
    /**
     * 检查是否已初始化
     */
    private fun checkInitialized() {
        if (accessibilityService == null) {
            throw IllegalStateException("UiAuto has not been initialized with an AccessibilityService")
        }
    }
    
    /**
     * 创建一个新的选择器
     */
    actual override fun selector(): ElementSelector {
        return ElementSelector()
    }
    
    /**
     * 根据文本查找元素
     */
    actual override fun text(text: String): ElementInfo? {
        return find(selector().text(text))
    }
    
    /**
     * 根据ID查找元素
     */
    actual override fun id(id: String): ElementInfo? {
        return find(selector().id(id))
    }
    
    /**
     * 根据类名查找元素
     */
    actual override fun className(className: String): ElementInfo? {
        return find(selector().className(className))
    }
    
    /**
     * 根据选择器查找单个元素
     */
    actual override fun find(selector: ElementSelector): ElementInfo? {
        checkInitialized()
        val rootNode = accessibilityService?.rootInActiveWindow ?: return null
        return findNodeMatchingSelector(rootNode, selector)
    }
    
    /**
     * 递归查找匹配选择器的节点
     */
    private fun findNodeMatchingSelector(node: AccessibilityNodeInfo, selector: ElementSelector): ElementInfo? {
        // 检查当前节点是否匹配
        val element = convertNodeToElementInfo(node)
        if (selector.matches(element)) {
            return element
        }
        
        // 递归检查子节点
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i) ?: continue
            val result = findNodeMatchingSelector(childNode, selector)
            if (result != null) {
                childNode.recycle()
                return result
            }
            childNode.recycle()
        }
        
        return null
    }
    
    /**
     * 根据选择器查找多个元素
     */
    actual override fun findAll(selector: ElementSelector): List<ElementInfo> {
        checkInitialized()
        val result = mutableListOf<ElementInfo>()
        val rootNode = accessibilityService?.rootInActiveWindow ?: return result
        findNodesMatchingSelector(rootNode, selector, result)
        return result
    }
    
    /**
     * 递归查找所有匹配选择器的节点
     */
    private fun findNodesMatchingSelector(node: AccessibilityNodeInfo, selector: ElementSelector, result: MutableList<ElementInfo>) {
        // 检查当前节点是否匹配
        val element = convertNodeToElementInfo(node)
        if (selector.matches(element)) {
            result.add(element)
        }
        
        // 递归检查子节点
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i) ?: continue
            findNodesMatchingSelector(childNode, selector, result)
            childNode.recycle()
        }
    }
    
    /**
     * 获取当前屏幕上的所有元素
     */
    actual override fun getAllElements(): List<ElementInfo> {
        checkInitialized()
        val result = mutableListOf<ElementInfo>()
        val rootNode = accessibilityService?.rootInActiveWindow ?: return result
        getAllNodesAsElements(rootNode, result)
        return result
    }
    
    /**
     * 递归获取所有节点作为元素
     */
    private fun getAllNodesAsElements(node: AccessibilityNodeInfo, result: MutableList<ElementInfo>) {
        result.add(convertNodeToElementInfo(node))
        
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i) ?: continue
            getAllNodesAsElements(childNode, result)
            childNode.recycle()
        }
    }
    
    /**
     * 将AccessibilityNodeInfo转换为ElementInfo
     */
    private fun convertNodeToElementInfo(node: AccessibilityNodeInfo): ElementInfo {
        val bounds = android.graphics.Rect()
        node.getBoundsInScreen(bounds)
        
        return ElementInfo(
            id = node.viewIdResourceName ?: "",
            text = node.text?.toString() ?: "",
            desc = node.contentDescription?.toString() ?: "",
            className = node.className?.toString() ?: "",
            packageName = node.packageName?.toString() ?: "",
            bounds = Rect(bounds.left, bounds.top, bounds.right, bounds.bottom),
            checkable = node.isCheckable,
            checked = node.isChecked,
            clickable = node.isClickable,
            enabled = node.isEnabled,
            focusable = node.isFocusable,
            focused = node.isFocused,
            scrollable = node.isScrollable,
            longClickable = node.isLongClickable,
            selected = node.isSelected,
            visible = true, // AccessibilityNodeInfo没有直接的可见性属性
            password = node.isPassword,
            editable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) node.isEditable else false,
            platformNodeInfo = node
        )
    }
    
    /**
     * 点击指定元素
     */
    actual override fun click(element: ElementInfo): Boolean {
        checkInitialized()
        
        // 如果有原始节点信息，直接使用performAction
        val nodeInfo = element.platformNodeInfo as? AccessibilityNodeInfo
        if (nodeInfo != null && nodeInfo.isClickable) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        
        // 否则，使用坐标点击
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
        checkInitialized()
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false // 手势API需要Android N或更高版本
        }
        
        val path = Path()
        path.moveTo(point.x.toFloat(), point.y.toFloat())
        
        val builder = GestureDescription.Builder()
        val gesture = builder
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        
        return accessibilityService?.dispatchGesture(gesture, null, null) ?: false
    }
    
    /**
     * 长按指定元素
     */
    actual override fun longClick(element: ElementInfo): Boolean {
        checkInitialized()
        
        // 如果有原始节点信息，直接使用performAction
        val nodeInfo = element.platformNodeInfo as? AccessibilityNodeInfo
        if (nodeInfo != null && nodeInfo.isLongClickable) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
        }
        
        // 否则，使用坐标长按
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
        checkInitialized()
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false // 手势API需要Android N或更高版本
        }
        
        val path = Path()
        path.moveTo(point.x.toFloat(), point.y.toFloat())
        
        val builder = GestureDescription.Builder()
        val gesture = builder
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        
        return accessibilityService?.dispatchGesture(gesture, null, null) ?: false
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
        checkInitialized()
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return false // 手势API需要Android N或更高版本
        }
        
        val path = Path()
        path.moveTo(from.x.toFloat(), from.y.toFloat())
        path.lineTo(to.x.toFloat(), to.y.toFloat())
        
        val builder = GestureDescription.Builder()
        val gesture = builder
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        
        return accessibilityService?.dispatchGesture(gesture, null, null) ?: false
    }
    
    /**
     * 在指定元素上输入文本
     */
    actual override fun input(element: ElementInfo, text: String): Boolean {
        checkInitialized()
        
        // 如果有原始节点信息，直接使用performAction
        val nodeInfo = element.platformNodeInfo as? AccessibilityNodeInfo
        if (nodeInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val arguments = Bundle()
                arguments.putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
                )
                return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            } else {
                // 旧版本Android不支持直接设置文本，尝试先点击元素获取焦点
                if (nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS)) {
                    return input(text)
                }
            }
        }
        
        return false
    }
    
    /**
     * 直接输入文本（在当前焦点的元素上）
     */
    actual override fun input(text: String): Boolean {
        // 在Android上，可以尝试使用全局粘贴操作
        // 注意：这需要额外的权限或者配合ADB/Shizuku使用
        // 这里提供一个基本实现，但实际应用可能需要更复杂的逻辑
        return false
    }
    
    /**
     * 按下指定的按键
     */
    actual override fun pressKey(keyCode: Int): Boolean {
        // 通过AccessibilityService无法直接发送按键事件
        // 这通常需要结合ADB/Shizuku使用
        return false
    }
    
    /**
     * 返回操作
     */
    actual override fun back(): Boolean {
        checkInitialized()
        return accessibilityService?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK) ?: false
    }
    
    /**
     * 主页操作
     */
    actual override fun home(): Boolean {
        checkInitialized()
        return accessibilityService?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME) ?: false
    }
    
    /**
     * 最近任务操作
     */
    actual override fun recents(): Boolean {
        checkInitialized()
        return accessibilityService?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS) ?: false
    }
    
    /**
     * 截取屏幕
     */
    actual override fun screenshot(): ByteArray {
        // 通过AccessibilityService无法直接截屏
        // 这通常需要结合ADB/Shizuku使用
        // 返回空数组作为占位符
        return ByteArray(0)
    }
    
    /**
     * 执行手势操作
     */
    actual override fun gesture(gesture: Gesture): Boolean {
        when (gesture) {
            is Gesture.Click -> return click(gesture.point, gesture.duration)
            is Gesture.LongClick -> return longClick(gesture.point, gesture.duration)
            is Gesture.Swipe -> return swipe(gesture.start, gesture.end, gesture.duration, gesture.steps)
            is Gesture.Pinch -> {
                // 实现双指缩放
                return false // 需要更复杂的实现
            }
            is Gesture.Rotate -> {
                // 实现旋转
                return false // 需要更复杂的实现
            }
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
        // 从当前窗口中获取屏幕尺寸
        checkInitialized()
        val displayMetrics = accessibilityService?.resources?.displayMetrics
            ?: return Rect(0, 0, 0, 0)
            
        return Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
    
    companion object {
        private const val TAG = "UiAuto"
    }
} 