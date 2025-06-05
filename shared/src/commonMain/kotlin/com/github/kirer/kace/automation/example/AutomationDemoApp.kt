package com.github.kirer.kace.automation.example

import androidx.compose.runtime.*
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.launch

/**
 * UI自动化示例应用
 */
@Composable
fun AutomationDemoApp() {
    val viewModel = remember { AutomationDemoViewModel() }
    val isServiceEnabled = remember { mutableStateOf(viewModel.isServiceEnabled()) }
    val coroutineScope = rememberCoroutineScope()
    
    // 定期检查服务状态
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            isServiceEnabled.value = viewModel.isServiceEnabled()
        }
    }
    
    AutomationDemoScreen(
        isServiceEnabled = isServiceEnabled.value,
        onEnableServiceClick = { viewModel.enableService() },
        onFindByTextClick = { viewModel.findAndClickText("设置") },
        onClickPointClick = { viewModel.clickScreenCenter() },
        onSwipeClick = { viewModel.swipeUp() }
    )
}

/**
 * 为iOS平台创建ViewController
 */
fun MainViewController() = ComposeUIViewController { 
    AutomationDemoApp() 
} 