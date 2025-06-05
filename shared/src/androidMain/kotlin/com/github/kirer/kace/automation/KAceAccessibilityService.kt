package com.github.kirer.kace.automation

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/**
 * KAce无障碍服务，提供Android平台的UI自动化能力
 * 需要在AndroidManifest.xml中注册，并由用户在系统设置中启用
 */
class KAceAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "KAceAccessibility"
        
        // 全局单例实例，用于在其他地方访问服务
        private var INSTANCE: KAceAccessibilityService? = null
        
        // 全局UI自动化控制器
        private val uiAuto: UiAuto by lazy { 
            UiAuto().apply { 
                INSTANCE?.let { initialize(it) } 
            } 
        }
        
        /**
         * 获取UI自动化控制器实例
         * 如果服务未启动，将返回未初始化的控制器
         */
        fun getUiAuto(): UiAuto = uiAuto
        
        /**
         * 检查无障碍服务是否已启用
         */
        fun isServiceEnabled(): Boolean = INSTANCE != null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "KAce无障碍服务已连接")
        
        // 配置服务
        val info = serviceInfo
        info.apply {
            // 监听所有事件类型
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            
            // 设置反馈类型
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            
            // 请求增强的无障碍功能
            flags = flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
                    
            // 设置超时
            notificationTimeout = 100
        }
        
        // 更新服务配置
        serviceInfo = info
        
        // 保存实例引用
        INSTANCE = this
        
        // 初始化UI自动化控制器
        uiAuto.initialize(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 处理无障碍事件
        // 这里可以根据需要添加事件处理逻辑
    }

    override fun onInterrupt() {
        Log.w(TAG, "KAce无障碍服务被中断")
    }
    
    override fun onUnbind(intent: Intent): Boolean {
        Log.i(TAG, "KAce无障碍服务已解绑")
        INSTANCE = null
        return super.onUnbind(intent)
    }
} 