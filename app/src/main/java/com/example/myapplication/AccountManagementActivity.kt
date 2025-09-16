package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AccountManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountManagementScreen(
                onBackClick = { finish() },
                onPhoneChangeClick = {
                    // 跳转到更换手机号页面
                    val intent = android.content.Intent(this, ChangePhoneActivity::class.java)
                    startActivity(intent)
                },
                onQQBindClick = {
                    // 处理QQ绑定
                    android.widget.Toast.makeText(this, "绑定QQ", android.widget.Toast.LENGTH_SHORT).show()
                },
                onWeChatBindClick = {
                    // 处理微信绑定
                    android.widget.Toast.makeText(this, "绑定微信", android.widget.Toast.LENGTH_SHORT).show()
                },
                onAccountDeactivateClick = {
                    // 处理注销账号
                    android.widget.Toast.makeText(this, "注销账号", android.widget.Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    onBackClick: () -> Unit,
    onPhoneChangeClick: () -> Unit,
    onQQBindClick: () -> Unit,
    onWeChatBindClick: () -> Unit,
    onAccountDeactivateClick: () -> Unit
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
                        text = "账号相关",
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
                // 说明文字
                Text(
                    text = "你可以通过绑定以下账号登录知聊",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // 账号列表
                AccountListSection(
                    onPhoneChangeClick = onPhoneChangeClick,
                    onQQBindClick = onQQBindClick,
                    onWeChatBindClick = onWeChatBindClick
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // 注销账号
                AccountDeactivateSection(
                    onAccountDeactivateClick = onAccountDeactivateClick
                )
            }
        }
    }
}

@Composable
fun AccountListSection(
    onPhoneChangeClick: () -> Unit,
    onQQBindClick: () -> Unit,
    onWeChatBindClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 手机号
        AccountItem(
            icon = "📱",
            iconColor = Color(0xFFFF9800),
            title = "手机号",
            subtitle = "181******36",
            buttonText = "更换",
            onButtonClick = onPhoneChangeClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // QQ
        AccountItem(
            icon = "🐧",
            iconColor = Color(0xFF2196F3),
            title = "QQ",
            subtitle = null,
            buttonText = "去绑定",
            onButtonClick = onQQBindClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 微信
        AccountItem(
            icon = "💬",
            iconColor = Color(0xFF4CAF50),
            title = "微信",
            subtitle = null,
            buttonText = "去绑定",
            onButtonClick = onWeChatBindClick
        )
    }
}

@Composable
fun AccountItem(
    icon: String,
    iconColor: Color,
    title: String,
    subtitle: String?,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        iconColor,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 标题和副标题
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            // 按钮
            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AccountDeactivateSection(
    onAccountDeactivateClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "注销账号",
            fontSize = 16.sp,
            color = Color.Red,
            modifier = Modifier
                .clickable { onAccountDeactivateClick() }
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}
