package com.github.kirer.kace.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.kirer.kace.features.auth.presentation.LoginScreen
import com.github.kirer.kace.features.auth.presentation.RegisterScreen
import com.github.kirer.kace.features.dashboard.presentation.DashboardScreen
import com.github.kirer.kace.features.splash.presentation.SplashScreen
import com.github.kirer.kace.features.user.presentation.UserManagementScreen

/**
 * 应用程序导航路由
 */
enum class Screen {
    SPLASH,
    LOGIN,
    REGISTER,
    DASHBOARD,
    USER_MANAGEMENT
}

/**
 * 应用程序导航控制器
 */
class NavigationController {
    var currentScreen by mutableStateOf(Screen.SPLASH)
        private set
    
    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
    
    fun navigateBack() {
        // 简单的返回逻辑，可以根据需要扩展
        when (currentScreen) {
            Screen.REGISTER -> currentScreen = Screen.LOGIN
            Screen.LOGIN -> currentScreen = Screen.SPLASH
            Screen.USER_MANAGEMENT -> currentScreen = Screen.DASHBOARD
            else -> {}
        }
    }
}

/**
 * 应用程序主导航组件
 * 根据当前屏幕状态显示相应的页面
 */
@Composable
fun AppNavigation() {
    val navigationController = remember { NavigationController() }
    
    when (navigationController.currentScreen) {
        Screen.SPLASH -> {
            SplashScreen(
                onNavigateToLogin = {
                    navigationController.navigateTo(Screen.LOGIN)
                }
            )
        }
        
        Screen.LOGIN -> {
            LoginScreen(
                onNavigateToRegister = {
                    navigationController.navigateTo(Screen.REGISTER)
                },
                onNavigateToDashboard = {
                    navigationController.navigateTo(Screen.DASHBOARD)
                }
            )
        }
        
        Screen.REGISTER -> {
            RegisterScreen(
                onNavigateToLogin = {
                    navigationController.navigateTo(Screen.LOGIN)
                },
                onNavigateToDashboard = {
                    navigationController.navigateTo(Screen.DASHBOARD)
                }
            )
        }
        
        Screen.DASHBOARD -> {
            DashboardScreen(
                onNavigateToUserManagement = {
                    navigationController.navigateTo(Screen.USER_MANAGEMENT)
                }
            )
        }
        
        Screen.USER_MANAGEMENT -> {
            UserManagementScreen(
                onNavigateBack = {
                    navigationController.navigateTo(Screen.DASHBOARD)
                }
            )
        }
    }
}