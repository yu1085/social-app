package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PropsRecordsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PropsRecordsScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun PropsRecordsScreen(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 顶部标题栏
            PropsRecordsHeader(onBackClick = onBackClick)
            
            // 内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyRecordsContent()
            }
        }
    }
}

@Composable
fun PropsRecordsHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = "记录",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.width(48.dp)) // 平衡布局
    }
}

@Composable
fun EmptyRecordsContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 魔法棒插画
        MagicWandRecordsIllustration()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "暂无数据",
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun MagicWandRecordsIllustration() {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // 大星星
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFF4169E1),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "★",
                fontSize = 24.sp,
                color = Color.White
            )
        }
        
        // 小星星1 (左上)
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = (-30).dp, y = (-20).dp)
                .background(
                    color = Color(0xFF4169E1),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "★",
                fontSize = 10.sp,
                color = Color.White
            )
        }
        
        // 小星星2 (左下)
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = (-25).dp, y = 15.dp)
                .background(
                    color = Color(0xFF4169E1),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "★",
                fontSize = 10.sp,
                color = Color.White
            )
        }
        
        // 小星星3 (右上)
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = 20.dp, y = (-15).dp)
                .background(
                    color = Color(0xFF4169E1),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "★",
                fontSize = 10.sp,
                color = Color.White
            )
        }
        
        // 魔法棒 (右下)
        Box(
            modifier = Modifier
                .size(60.dp, 8.dp)
                .offset(x = 15.dp, y = 25.dp)
                .background(
                    color = Color(0xFF1E90FF),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                )
        )
        
        // 魔法棒尖端
        Box(
            modifier = Modifier
                .size(12.dp)
                .offset(x = 45.dp, y = 20.dp)
                .background(
                    color = Color(0xFF1E90FF),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                )
        )
    }
}
