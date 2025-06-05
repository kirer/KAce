package com.github.kirer.kace.automation.example

import android.content.Context
import com.github.kirer.kace.automation.AccessibilityUtils
import com.github.kirer.kace.automation.UiAuto
import com.github.kirer.kace.automation.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android平台的UI自动化示例ViewModel实现
 */
actual class AutomationDemoViewModel {
    private var appContext: Context? = null
    
    /**
     * 初始化ViewModel
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    
    /**
     * 检查服务是否启用
     */
    actual fun isServiceEnabled(): Boolean {
        val context = appContext ?: return false
        return AccessibilityUtils.isAccessibilityServiceEnabled(context)
    }
    
    /**
     * 启用服务
     */
    actual fun enableService() {
        val context = appContext ?: return
        AccessibilityUtils.openAccessibilitySettings(context)
    }
    
    /**
     * 获取UI自动化控制器
     */
    actual fun getUiAuto(): UiAuto? {
        val context = appContext ?: return null
        return AccessibilityUtils.getInitializedUiAuto(context)
    }
    
    /**
     * 查找并点击文本
     */
    actual suspend fun findAndClickText(text: String): String? = withContext(Dispatchers.IO) {
        try {
            val auto = getUiAuto() ?: return@withContext "无障碍服务未启用"
            
            val element = auto.text(text)
            if (element != null) {
                val success = auto.click(element)
                if (success) {
                    return@withContext "成功找到并点击了文本\"$text\"，位置：${element.bounds}"
                } else {
                    return@withContext "找到了文本\"$text\"，但点击操作失败"
                }
            } else {
                return@withContext "未找到包含文本\"$text\"的元素"
            }
        } catch (e: Exception) {
            return@withContext "操作出错: ${e.message}"
        }
    }
    
    /**
     * 点击屏幕中心
     */
    actual suspend fun clickScreenCenter(): String? = withContext(Dispatchers.IO) {
        try {
            val auto = getUiAuto() ?: return@withContext "无障碍服务未启用"
            
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
    actual suspend fun swipeUp(): String? = withContext(Dispatchers.IO) {
        try {
            val auto = getUiAuto() ?: return@withContext "无障碍服务未启用"
            
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