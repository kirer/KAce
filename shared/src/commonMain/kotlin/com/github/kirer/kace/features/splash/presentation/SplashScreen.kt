package com.github.kirer.kace.features.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.kirer.kace.core.ui.ResponsiveUtils
import kotlinx.coroutines.delay

/**
 * 启动屏幕
 * Material Design 3 风格的启动页面
 * 支持明暗主题和响应式设计
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    val scaleAnimation = remember { Animatable(0f) }
    val alphaAnimation = remember { Animatable(0f) }
    val contentPadding = ResponsiveUtils.getContentPadding()
    
    // 启动动画
    LaunchedEffect(Unit) {
        // Logo缩放动画
        scaleAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        
        // 文字淡入动画
        alphaAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        
        // 等待一段时间后跳转到登录页面
        delay(2000)
        onNavigateToLogin()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo区域
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnimation.value)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 应用名称
            Text(
                text = "KAce CMS",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alphaAnimation.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 副标题
            Text(
                text = "内容管理系统",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.alpha(alphaAnimation.value)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // 加载指示器
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .alpha(alphaAnimation.value),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
        
        // 版本信息（底部）
        Text(
            text = "v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(alphaAnimation.value)
        )
    }
}