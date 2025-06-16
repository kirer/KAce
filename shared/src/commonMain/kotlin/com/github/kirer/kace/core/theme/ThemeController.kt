package com.github.kirer.kace.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 主题控制器
 * 管理应用程序的明暗主题状态
 */
class ThemeController {
    var isDarkTheme by mutableStateOf(false)
        private set
    
    /**
     * 切换主题
     */
    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
    
    /**
     * 设置主题
     * @param isDark 是否为暗色主题
     */
    fun setTheme(isDark: Boolean) {
        isDarkTheme = isDark
    }
}

/**
 * 主题控制器的 CompositionLocal
 */
val LocalThemeController = compositionLocalOf<ThemeController> {
    error("ThemeController not provided")
}