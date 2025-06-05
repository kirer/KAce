package com.github.kirer.kace.automation.example

import com.github.kirer.kace.automation.UiAuto
import com.github.kirer.kace.automation.model.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UI自动化示例ViewModel
 * 处理业务逻辑，可在多平台共享
 */
expect class AutomationDemoViewModel() {
    /**
     * 检查服务是否启用
     */
    fun isServiceEnabled(): Boolean
    
    /**
     * 启用服务
     */
    fun enableService()
    
    /**
     * 获取UI自动化控制器
     */
    fun getUiAuto(): UiAuto?
    
    /**
     * 查找并点击文本
     */
    suspend fun findAndClickText(text: String): String?
    
    /**
     * 点击屏幕中心
     */
    suspend fun clickScreenCenter(): String?
    
    /**
     * 执行上滑操作
     */
    suspend fun swipeUp(): String?
} 