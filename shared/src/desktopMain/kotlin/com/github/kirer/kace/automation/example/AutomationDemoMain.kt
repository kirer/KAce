package com.github.kirer.kace.automation.example

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize

/**
 * 桌面平台的UI自动化示例应用入口
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KAce UI自动化示例",
        state = rememberWindowState()
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                AutomationDemoApp()
            }
        }
    }
} 