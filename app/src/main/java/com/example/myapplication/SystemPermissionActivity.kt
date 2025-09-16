package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SystemPermissionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SystemPermissionScreen(
                onBackClick = { finish() },
                onPermissionClick = { permission ->
                    // 处理权限点击事件
                    android.widget.Toast.makeText(this, "点击了: $permission", android.widget.Toast.LENGTH_SHORT).show()
                },
                onPermissionDescriptionClick = {
                    // 处理权限说明点击事件
                    android.widget.Toast.makeText(this, "点击了权限说明", android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemPermissionScreen(
    onBackClick: () -> Unit,
    onPermissionClick: (String) -> Unit,
    onPermissionDescriptionClick: () -> Unit
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
                        text = "系统权限管理",
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 介绍文字
                IntroductionText()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 权限列表
                PermissionList(
                    onPermissionClick = onPermissionClick
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 权限说明链接
                PermissionDescriptionLink(
                    onPermissionDescriptionClick = onPermissionDescriptionClick
                )
            }
        }
    }
}

@Composable
fun IntroductionText() {
    Text(
        text = "为保障产品和功能的使用,本软件会向你申请手机系统权限,以下常用权限可以在这里操作管理",
        fontSize = 14.sp,
        color = Color.Black,
        lineHeight = 20.sp,
        textAlign = TextAlign.Start
    )
}

@Composable
fun PermissionList(
    onPermissionClick: (String) -> Unit
) {
    val permissions = listOf(
        "位置",
        "存储权限",
        "相机拍摄权限",
        "麦克风录音权限",
        "电话权限",
        "悬浮窗"
    )
    
    Column {
        permissions.forEachIndexed { index, permission ->
            PermissionItem(
                permission = permission,
                onClick = { onPermissionClick(permission) }
            )
            
            if (index < permissions.size - 1) {
                Divider(
                    modifier = Modifier.padding(horizontal = 0.dp),
                    color = Color(0xFFF0F0F0)
                )
            }
        }
    }
}

@Composable
fun PermissionItem(
    permission: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 权限名称
        Text(
            text = permission,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 权限状态
            Text(
                text = "未允许访问",
                fontSize = 14.sp,
                color = Color(0xFF007AFF)
            )
            
            // 右箭头
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "进入",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun PermissionDescriptionLink(
    onPermissionDescriptionClick: () -> Unit
) {
    Text(
        text = "您可以在《权限说明》中了解到权限的详细应用说明",
        fontSize = 14.sp,
        color = Color.Black,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPermissionDescriptionClick() }
    )
}
