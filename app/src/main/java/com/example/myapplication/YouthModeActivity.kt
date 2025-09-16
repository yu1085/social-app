package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class YouthModeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouthModeScreen(
                onBackClick = { finish() },
                onEnableYouthModeClick = {
                    android.widget.Toast.makeText(this, "青少年模式已开启", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouthModeScreen(
    onBackClick: () -> Unit,
    onEnableYouthModeClick: () -> Unit
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
                        text = "青少年模式",
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // 插图区域
                YouthModeIllustration()
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // 模式限制说明
                ModeRestrictionsSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 使用方法说明
                UsageInstructionsSection()
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // 开启青少年模式按钮
                Button(
                    onClick = onEnableYouthModeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF)
                    )
                ) {
                    Text(
                        text = "开启青少年模式",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun YouthModeIllustration() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFBBDEFB)
                    )
                ),
                RoundedCornerShape(100.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // 雨伞图标
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // 雨伞主体
            Box(
                modifier = Modifier
                    .size(80.dp, 60.dp)
                    .background(
                        Color(0xFF2196F3),
                        RoundedCornerShape(40.dp, 40.dp, 0.dp, 0.dp)
                    )
            )
            
            // 雨伞手柄
            Box(
                modifier = Modifier
                    .size(4.dp, 40.dp)
                    .background(
                        Color(0xFFFFC107),
                        RoundedCornerShape(2.dp)
                    )
                    .offset(y = 30.dp)
            )
            
            // 雨伞顶部小点
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        Color(0xFFFFC107),
                        RoundedCornerShape(4.dp)
                    )
                    .offset(y = -30.dp)
            )
        }
        
        // 爱心图标
        Row(
            modifier = Modifier.offset(x = 20.dp, y = -20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "💖",
                fontSize = 20.sp
            )
            Text(
                text = "💖",
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun ModeRestrictionsSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "模式限制:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "无法使用App的所有功能",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UsageInstructionsSection() {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "使用方法:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 使用方法列表
        UsageInstructionItem(
            number = "1",
            text = "开启青少年模式需设置一个独立密码;"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        UsageInstructionItem(
            number = "2",
            text = "青少年模式开启后会立即生效,输入设置的独立密码可关闭青少年模式;"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        UsageInstructionItem(
            number = "3",
            text = "青少年模式开启后,每次重新登录帐号或重启APP时均需输入设置的独立密码才能关闭青少年模式;"
        )
    }
}

@Composable
fun UsageInstructionItem(
    number: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        // 数字标识
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    Color(0xFF007AFF),
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 说明文字
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
