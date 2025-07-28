package com.github.kirer.boss

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

/**
 * boss CMS 应用程序主入口
 * 使用 Material Design 3 设计规范
 * 支持明暗主题切换和响应式设计
 */
@Composable
fun App() {

    CompositionLocalProvider(
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
            }
        }
    }
}