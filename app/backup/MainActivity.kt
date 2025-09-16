package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.components.BottomNavigation
import com.example.myapplication.ui.screens.SquareScreen
import com.example.myapplication.ui.screens.MessageScreen
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.ui.unit.dp
import com.example.myapplication.utils.ScreenAdaptUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化今日头条屏幕适配方案
        ScreenAdaptUtil.setCustomDensity(this, application)
        
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

/**
 * 主应用界面
 */
@Composable
fun MainApp() {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("square") }
    var notificationCount by remember { mutableStateOf(22) }
    
    Scaffold(
        bottomBar = {
            BottomNavigation(
                currentRoute = currentRoute,
                onRouteSelected = { route ->
                    currentRoute = route
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                notificationCount = notificationCount
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "square",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                // 首页界面
                HomeScreen()
            }
            
            composable("square") {
                // 广场界面
                SquareScreen(
                    onUserClick = { userId ->
                        // 处理用户点击事件
                    }
                )
            }
            
            composable("message") {
                // 私信界面
                MessageScreen()
            }
            
            composable("profile") {
                // 个人中心界面 - 使用真正的ProfileScreen
                ProfileScreen(
                    onSettingClick = {
                        // 处理设置点击事件
                        // 这里可以跳转到设置页面或显示设置对话框
                    },
                    onVipClick = {
                        // 处理VIP点击事件
                        // 这里可以跳转到VIP购买页面
                    },
                    onRechargeClick = {
                        // 处理充值点击事件
                        // 这里可以跳转到充值页面
                    },
                    onMenuClick = { menuItem ->
                        // 处理菜单点击事件
                        when (menuItem) {
                            "邀请好友" -> {
                                // 处理邀请好友
                            }
                            "我的认证" -> {
                                // 处理我的认证
                            }
                            "我的卡券" -> {
                                // 处理我的卡券
                            }
                            "谁看过我" -> {
                                // 处理谁看过我
                            }
                            "我的守护" -> {
                                // 处理我的守护
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * 首页界面（占位）
 */
@Composable
fun HomeScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "首页",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}