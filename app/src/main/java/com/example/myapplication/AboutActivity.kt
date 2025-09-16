package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AboutScreen(
                onBackClick = { finish() },
                onMenuItemClick = { menuItem ->
                    // 处理菜单项点击
                    android.widget.Toast.makeText(
                        this,
                        "点击了$menuItem",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onMenuItemClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部导航栏
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
            
            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 应用信息区域
                AppInfoSection()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 菜单列表
                MenuListSection(onMenuItemClick = onMenuItemClick)
            }
        }
    }
}

@Composable
fun AppInfoSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 应用Logo
        AppLogo()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 应用名称
        Text(
            text = "知聊",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 应用标语
        Text(
            text = "知你所聊",
            fontSize = 16.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 版本号
        Text(
            text = "V6.19.2.3",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun AppLogo() {
    Box(
        modifier = Modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // 蓝色气泡
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(x = (-8).dp, y = 0.dp)
                .background(
                    color = Color(0xFF2196F3),
                    shape = RoundedCornerShape(30.dp)
                )
        )
        
        // 红色气泡
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(x = 8.dp, y = 0.dp)
                .background(
                    color = Color(0xFFE91E63),
                    shape = RoundedCornerShape(30.dp)
                )
        ) {
            // 白色圆圈（眼睛）
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .offset(x = 8.dp, y = 12.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun MenuListSection(
    onMenuItemClick: (String) -> Unit
) {
    val menuItems = listOf(
        "版本更新",
        "用户协议",
        "隐私政策",
        "真人认证协议",
        "用户违规行为管理办法",
        "证照信息",
        "APP备案编号",
        "给知聊打分",
        "日志上传"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            menuItems.forEachIndexed { index, menuItem ->
                MenuItem(
                    text = menuItem,
                    onClick = { onMenuItemClick(menuItem) },
                    showDivider = index < menuItems.size - 1
                )
            }
        }
    }
}

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.Black
            )
            
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "进入",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
        
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(start = 16.dp),
                color = Color(0xFFE0E0E0),
                thickness = 0.5.dp
            )
        }
    }
}
