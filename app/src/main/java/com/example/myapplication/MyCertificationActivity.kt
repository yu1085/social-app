package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 我的认证页面Activity
 * 实现实名认证、手机认证、真人认证功能
 */
class MyCertificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MyCertificationScreen(
                    onBackClick = { finish() },
                    onRealNameClick = {
                        // 处理实名认证点击事件
                    },
                    onPhoneClick = {
                        // 处理手机认证点击事件
                    },
                    onRealPersonClick = {
                        // 处理真人认证点击事件
                    }
                )
            }
        }
    }
}

/**
 * 我的认证页面内容
 */
@Composable
fun MyCertificationScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onRealNameClick: () -> Unit = {},
    onPhoneClick: () -> Unit = {},
    onRealPersonClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD), // 浅蓝色渐变
                        Color(0xFFF5F5F5)  // 浅灰色渐变
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 顶部导航栏
            TopNavigationBar(onBackClick = onBackClick)
            
            // 页面标题区域
            TitleSection()
            
            // 认证选项列表
            CertificationList(
                onRealNameClick = onRealNameClick,
                onPhoneClick = onPhoneClick,
                onRealPersonClick = onRealPersonClick
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
        
        // 背景装饰元素
        BackgroundDecorations()
    }
}

/**
 * 顶部导航栏
 */
@Composable
private fun TopNavigationBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color(0xFF333333),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 页面标题区域
 */
@Composable
private fun TitleSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // 主标题
        Text(
            text = "我的认证",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 说明文字
        Text(
            text = "平台提倡真实交友，完成认证你会更受欢迎",
            fontSize = 14.sp,
            color = Color(0xFF666666),
            lineHeight = 20.sp
        )
    }
}

/**
 * 认证选项列表
 */
@Composable
private fun CertificationList(
    onRealNameClick: () -> Unit,
    onPhoneClick: () -> Unit,
    onRealPersonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // 实名认证
        CertificationCard(
            icon = Icons.Default.Person,
            iconColor = Color(0xFFFF5722),
            title = "实名认证",
            description = "通过身份证和活体认证",
            buttonText = "去认证",
            buttonColor = Color(0xFF2196F3),
            onButtonClick = onRealNameClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 手机认证
        CertificationCard(
            icon = Icons.Default.Phone,
            iconColor = Color(0xFF2196F3),
            title = "手机认证",
            description = "通过真实手机号认证",
            buttonText = "已认证",
            buttonColor = Color(0xFF4CAF50),
            isVerified = true,
            onButtonClick = onPhoneClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 真人认证
        CertificationCard(
            icon = Icons.Default.VerifiedUser,
            iconColor = Color(0xFFFF9800),
            title = "真人认证",
            description = "通过活体检测认证",
            buttonText = "去认证",
            buttonColor = Color(0xFF2196F3),
            onButtonClick = onRealPersonClick
        )
    }
}

/**
 * 认证卡片组件
 */
@Composable
private fun CertificationCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    buttonText: String,
    buttonColor: Color,
    isVerified: Boolean = false,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 中间内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            
            // 右侧按钮
            if (isVerified) {
                // 已认证状态 - 显示绿色印章
                Box(
                    modifier = Modifier
                        .size(60.dp, 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(buttonColor),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "已认证",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = buttonText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // 未认证状态 - 显示蓝色按钮
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * 背景装饰元素
 */
@Composable
private fun BackgroundDecorations() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 右上角装饰卡片
        Card(
            modifier = Modifier
                .size(80.dp, 60.dp)
                .offset(x = 280.dp, y = 100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 星形徽章
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "认证徽章",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        // 其他装饰元素
        Box(
            modifier = Modifier
                .size(40.dp)
                .offset(x = 50.dp, y = 200.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2FD).copy(alpha = 0.6f))
        )
        
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(x = 300.dp, y = 300.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFFE3F2FD).copy(alpha = 0.4f))
        )
    }
}
