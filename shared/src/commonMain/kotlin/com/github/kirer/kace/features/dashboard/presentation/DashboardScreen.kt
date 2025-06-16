package com.github.kirer.kace.features.dashboard.presentation

// import androidx.compose.animation.core.animateFloatAsState
// import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.kirer.kace.core.theme.LocalThemeController
import com.github.kirer.kace.core.ui.ResponsiveUtils
import kotlinx.coroutines.launch

/**
 * 导航菜单项数据类
 */
data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

/**
 * 仪表板卡片数据类
 */
data class DashboardCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val count: String,
    val onClick: () -> Unit
)

/**
 * 主页面
 * Material Design 3 风格的仪表板界面
 * 包含导航抽屉、统计卡片和快捷操作
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToUserManagement: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val themeController = LocalThemeController.current
    val isCompactScreen = ResponsiveUtils.isCompactScreen()
    val gridColumns = ResponsiveUtils.getGridColumns()
    val cardSpacing = ResponsiveUtils.getCardSpacing()
    
    // 导航菜单项
    val navigationItems = listOf(
        NavigationItem(
            title = "内容管理",
            icon = Icons.Default.Article,
            onClick = { /* TODO: 导航到内容管理 */ }
        ),
        NavigationItem(
            title = "用户管理",
            icon = Icons.Default.Group,
            onClick = onNavigateToUserManagement
        ),
        NavigationItem(
            title = "媒体库",
            icon = Icons.Default.PhotoLibrary,
            onClick = { /* TODO: 导航到媒体库 */ }
        ),
        NavigationItem(
            title = "数据分析",
            icon = Icons.Default.Analytics,
            onClick = { /* TODO: 导航到数据分析 */ }
        ),
        NavigationItem(
            title = "通知中心",
            icon = Icons.Default.Notifications,
            onClick = { /* TODO: 导航到通知中心 */ }
        ),
        NavigationItem(
            title = "系统设置",
            icon = Icons.Default.Settings,
            onClick = { /* TODO: 导航到系统设置 */ }
        )
    )
    
    // 仪表板统计卡片
    val dashboardCards = listOf(
        DashboardCard(
            title = "总用户数",
            subtitle = "注册用户",
            icon = Icons.Default.Person,
            count = "1,234",
            onClick = onNavigateToUserManagement
        ),
        DashboardCard(
            title = "内容数量",
            subtitle = "已发布内容",
            icon = Icons.Default.Article,
            count = "567",
            onClick = { /* TODO: 导航到内容管理 */ }
        ),
        DashboardCard(
            title = "媒体文件",
            subtitle = "上传文件",
            icon = Icons.Default.PhotoLibrary,
            count = "890",
            onClick = { /* TODO: 导航到媒体库 */ }
        ),
        DashboardCard(
            title = "今日访问",
            subtitle = "页面浏览量",
            icon = Icons.Default.Analytics,
            count = "2,345",
            onClick = { /* TODO: 导航到数据分析 */ }
        )
    )
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 抽屉头部
                    Text(
                        text = "KAce CMS",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "内容管理系统",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 主题切换
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (themeController.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "切换主题"
                            )
                        },
                        label = {
                            Text(if (themeController.isDarkTheme) "浅色主题" else "深色主题")
                        },
                        selected = false,
                        onClick = {
                            themeController.toggleTheme()
                            scope.launch { drawerState.close() }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 导航菜单项
                    navigationItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = false,
                            onClick = {
                                item.onClick()
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("仪表板") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "打开菜单"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // 欢迎信息
                Text(
                    text = "欢迎回来！",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "这里是您的内容管理中心",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 统计卡片网格
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 220.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(350.dp)
                ) {
                    items(dashboardCards) { card ->
                        StatCard(
                            title = card.title,
                            value = card.count,
                            icon = card.icon,
                            progress = when (card.title) {
                                "总用户数" -> 0.75f
                                "内容数量" -> 0.60f
                                "媒体文件" -> 0.45f
                                "今日访问" -> 0.85f
                                else -> 0f
                            },
                            trend = when (card.title) {
                                "总用户数" -> "↗ +12% 本月"
                                "内容数量" -> "↗ +8% 本周"
                                "媒体文件" -> "→ 无变化"
                                "今日访问" -> "↗ +25% 今日"
                                else -> ""
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 统计卡片组件
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    progress: Float = 0f,
    trend: String = "",
    modifier: Modifier = Modifier
) {
    // val animatedProgress by animateFloatAsState(
        //     targetValue = progress,
        //     animationSpec = tween(durationMillis = 1000),
        //     label = "progress"
        // )
        val animatedProgress = progress
    
    Card(
        modifier = modifier.clickable { },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // if (progress > 0) {
            //     LinearProgressIndicator(
            //         progress = animatedProgress,
            //         modifier = Modifier.fillMaxWidth(),
            //         color = MaterialTheme.colorScheme.primary,
            //         trackColor = MaterialTheme.colorScheme.surfaceVariant,
            //     )
            // }
            
            if (trend.isNotEmpty()) {
                Text(
                    text = trend,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 仪表板卡片组件
 */
@Composable
fun DashboardCardItem(
    card: DashboardCard,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = card.onClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = card.icon,
                contentDescription = card.title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = card.count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = card.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}