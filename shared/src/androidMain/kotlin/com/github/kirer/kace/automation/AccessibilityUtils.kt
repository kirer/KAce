package com.github.kirer.kace.automation

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.util.Log

/**
 * 无障碍服务辅助工具类
 */
object AccessibilityUtils {
    private const val TAG = "AccessibilityUtils"

    /**
     * 检查无障碍服务是否已启用
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val serviceName = context.packageName + "/" + KAceAccessibilityService::class.java.canonicalName
        
        try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            if (enabledServices != null) {
                return enabledServices.split(":").any { it.equals(serviceName, ignoreCase = true) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "检查无障碍服务状态出错", e)
        }
        
        return false
    }

    /**
     * 打开无障碍服务设置页面
     */
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "打开无障碍设置页面出错", e)
        }
    }

    /**
     * 获取已初始化的UI自动化控制器
     * 如果无障碍服务未启用，将返回null
     */
    fun getInitializedUiAuto(context: Context): UiAuto? {
        return if (isAccessibilityServiceEnabled(context) && KAceAccessibilityService.isServiceEnabled()) {
            KAceAccessibilityService.getUiAuto()
        } else {
            Log.w(TAG, "无障碍服务未启用，UI自动化控制器不可用")
            null
        }
    }
} 