package com.github.kirer.kace.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 屏幕尺寸类型
 */
enum class ScreenSize {
    COMPACT,    // 手机竖屏 < 600dp
    MEDIUM,     // 平板竖屏或手机横屏 600dp - 840dp
    EXPANDED    // 平板横屏或桌面 > 840dp
}

/**
 * 响应式设计工具类
 * 根据屏幕宽度确定布局类型
 */
object ResponsiveUtils {
    
    /**
     * 获取当前屏幕尺寸类型
     */
    @Composable
    fun getScreenSize(): ScreenSize {
        val density = LocalDensity.current
        // 这里需要获取屏幕宽度，暂时使用默认值
        // 在实际使用中，可以通过平台特定的方式获取屏幕尺寸
        val screenWidth = 400.dp // 默认值，实际应该从系统获取
        
        return when {
            screenWidth < 600.dp -> ScreenSize.COMPACT
            screenWidth < 840.dp -> ScreenSize.MEDIUM
            else -> ScreenSize.EXPANDED
        }
    }
    
    /**
     * 根据屏幕尺寸获取内容边距
     */
    @Composable
    fun getContentPadding(): Dp {
        return when (getScreenSize()) {
            ScreenSize.COMPACT -> 16.dp
            ScreenSize.MEDIUM -> 24.dp
            ScreenSize.EXPANDED -> 32.dp
        }
    }
    
    /**
     * 根据屏幕尺寸获取卡片间距
     */
    @Composable
    fun getCardSpacing(): Dp {
        return when (getScreenSize()) {
            ScreenSize.COMPACT -> 8.dp
            ScreenSize.MEDIUM -> 12.dp
            ScreenSize.EXPANDED -> 16.dp
        }
    }
    
    /**
     * 根据屏幕尺寸获取网格列数
     */
    @Composable
    fun getGridColumns(): Int {
        return when (getScreenSize()) {
            ScreenSize.COMPACT -> 1
            ScreenSize.MEDIUM -> 2
            ScreenSize.EXPANDED -> 3
        }
    }
    
    /**
     * 判断是否为紧凑屏幕（手机）
     */
    @Composable
    fun isCompactScreen(): Boolean {
        return getScreenSize() == ScreenSize.COMPACT
    }
    
    /**
     * 判断是否为扩展屏幕（桌面/大平板）
     */
    @Composable
    fun isExpandedScreen(): Boolean {
        return getScreenSize() == ScreenSize.EXPANDED
    }
}