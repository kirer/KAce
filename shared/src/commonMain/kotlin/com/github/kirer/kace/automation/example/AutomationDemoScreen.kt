package com.github.kirer.kace.automation.example

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kirer.kace.automation.model.Point
import kotlinx.coroutines.launch

/**
 * UI自动化示例界面（Compose Multiplatform）
 */
@Composable
fun AutomationDemoScreen(
    isServiceEnabled: Boolean,
    onEnableServiceClick: () -> Unit,
    onFindByTextClick: suspend () -> String?,
    onClickPointClick: suspend () -> String?,
    onSwipeClick: suspend () -> String?
) {
    val coroutineScope = rememberCoroutineScope()
    var resultMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "KAce UI自动化示例",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 服务状态
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "无障碍服务状态",
                    style = MaterialTheme.typography.subtitle1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (isServiceEnabled) "已启用" else "未启用",
                    color = if (isServiceEnabled) 
                        MaterialTheme.colors.primary 
                    else 
                        MaterialTheme.colors.error
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (!isServiceEnabled) {
                    Button(
                        onClick = onEnableServiceClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("启用无障碍服务")
                    }
                }
            }
        }

        // 操作示例
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "自动化操作示例",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = {
                        if (!isServiceEnabled) {
                            resultMessage = "请先启用无障碍服务"
                            return@Button
                        }
                        
                        isLoading = true
                        coroutineScope.launch {
                            resultMessage = onFindByTextClick() ?: "操作失败"
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = !isLoading
                ) {
                    Text("查找并点击"设置"按钮")
                }
                
                Button(
                    onClick = {
                        if (!isServiceEnabled) {
                            resultMessage = "请先启用无障碍服务"
                            return@Button
                        }
                        
                        isLoading = true
                        coroutineScope.launch {
                            resultMessage = onClickPointClick() ?: "操作失败"
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = !isLoading
                ) {
                    Text("点击屏幕中心")
                }
                
                Button(
                    onClick = {
                        if (!isServiceEnabled) {
                            resultMessage = "请先启用无障碍服务"
                            return@Button
                        }
                        
                        isLoading = true
                        coroutineScope.launch {
                            resultMessage = onSwipeClick() ?: "操作失败"
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = !isLoading
                ) {
                    Text("执行上滑操作")
                }
            }
        }

        // 结果显示
        if (resultMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "操作结果",
                        style = MaterialTheme.typography.subtitle1
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(text = resultMessage)
                }
            }
        }
        
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "注意：使用这些功能需要先启用无障碍服务",
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
} 