package com.github.kirer.kace.automation.example

import com.github.kirer.kace.automation.UiAuto
import com.github.kirer.kace.automation.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle

/**
 * iOS平台的UI自动化示例ViewModel实现
 */
actual class AutomationDemoViewModel {
    private val uiAuto by lazy { UiAuto() }
    private var isEnabled = false
    
    /**
     * 检查服务是否启用
     * 注意：iOS平台需要特殊权限和配置
     */
    actual fun isServiceEnabled(): Boolean {
        return isEnabled
    }
    
    /**
     * 启用服务
     * 在iOS上，这通常需要用户在系统设置中手动启用
     */
    actual fun enableService() {
        // 在实际应用中，这里应该引导用户去系统设置中启用相关权限
        // 这里简单模拟一下
        isEnabled = true
    }
    
    /**
     * 获取UI自动化控制器
     */
    actual fun getUiAuto(): UiAuto? {
        return if (isEnabled) uiAuto else null
    }
    
    /**
     * 查找并点击文本
     */
    actual suspend fun findAndClickText(text: String): String? = withContext(Dispatchers.Main) {
        try {
            val auto = getUiAuto() ?: return@withContext "自动化服务未启用"
            
            val element = auto.text(text)
            if (element != null) {
                val success = auto.click(element)
                if (success) {
                    return@withContext "成功找到并点击了文本\"$text\"，位置：${element.bounds}"
                } else {
                    return@withContext "找到了文本\"$text\"，但点击操作失败"
                }
            } else {
                return@withContext "iOS平台尚未完全实现文本查找功能"
            }
        } catch (e: Exception) {
            return@withContext "操作出错: ${e.message}"
        }
    }
    
    /**
     * 点击屏幕中心
     */
    actual suspend fun clickScreenCenter(): String? = withContext(Dispatchers.Main) {
        try {
            val auto = getUiAuto() ?: return@withContext "自动化服务未启用"
            
            val screenSize = auto.getScreenSize()
            val centerX = screenSize.width / 2
            val centerY = screenSize.height / 2
            
            val success = auto.click(Point(centerX, centerY))
            if (success) {
                return@withContext "成功点击了屏幕中心点 ($centerX, $centerY)"
            } else {
                return@withContext "点击屏幕中心点失败"
            }
        } catch (e: Exception) {
            return@withContext "操作出错: ${e.message}"
        }
    }
    
    /**
     * 执行上滑操作
     */
    actual suspend fun swipeUp(): String? = withContext(Dispatchers.Main) {
        try {
            val auto = getUiAuto() ?: return@withContext "自动化服务未启用"
            
            val screenSize = auto.getScreenSize()
            val centerX = screenSize.width / 2
            val startY = screenSize.height * 3 / 4
            val endY = screenSize.height / 4
            
            val success = auto.swipe(
                Point(centerX, startY),
                Point(centerX, endY)
            )
            
            if (success) {
                return@withContext "成功执行了上滑操作"
            } else {
                return@withContext "上滑操作失败"
            }
        } catch (e: Exception) {
            return@withContext "操作出错: ${e.message}"
        }
    }
} 