package com.github.kirer.kace

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.github.kirer.kace.core.navigation.AppNavigation
import com.github.kirer.kace.core.theme.KAceTheme
import com.github.kirer.kace.core.theme.LocalThemeController
import com.github.kirer.kace.core.theme.ThemeController

/**
 * KAce CMS 应用程序主入口
 * 使用 Material Design 3 设计规范
 * 支持明暗主题切换和响应式设计
 */
@Composable
fun App() {
    val themeController = ThemeController()
    
    CompositionLocalProvider(
        LocalThemeController provides themeController
    ) {
        KAceTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigation()
            }
        }
    }
}