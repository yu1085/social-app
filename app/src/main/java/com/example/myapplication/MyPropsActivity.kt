package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MyPropsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPropsScreen(
                onBackClick = { finish() },
                onRecordsClick = {
                    // 跳转到道具记录页面
                    val intent = android.content.Intent(this, PropsRecordsActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun MyPropsScreen(
    onBackClick: () -> Unit,
    onRecordsClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: 靓号, 1: 进场特效, 2: 首饰
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部标题栏
        MyPropsHeader(
            onBackClick = onBackClick,
            onRecordsClick = onRecordsClick
        )
        
        // 标签导航栏
        MyPropsTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // 内容区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> EmptyPropsContent("暂无靓号")
                1 -> EmptyPropsContent("暂无进场特效")
                2 -> EmptyPropsContent("暂无首饰")
            }
        }
    }
}

@Composable
fun MyPropsHeader(
    onBackClick: () -> Unit,
    onRecordsClick: () -> Unit
) {
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
            text = "我的道具",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = "记录",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.clickable { onRecordsClick() }
        )
    }
}

@Composable
fun MyPropsTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("靓号", "进场特效", "首饰")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            MyPropsTab(
                text = tab,
                isSelected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
fun MyPropsTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                color = if (isSelected) Color(0xFF87CEEB) else Color(0xFFF5F5F5)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun EmptyPropsContent(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 魔法棒插画
        MagicWandIllustration()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun MagicWandIllustration() {
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
                    shape = RoundedCornerShape(8.dp)
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
                    shape = RoundedCornerShape(4.dp)
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
                    shape = RoundedCornerShape(4.dp)
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
                    shape = RoundedCornerShape(4.dp)
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
                    shape = RoundedCornerShape(4.dp)
                )
        )
        
        // 魔法棒尖端
        Box(
            modifier = Modifier
                .size(12.dp)
                .offset(x = 45.dp, y = 20.dp)
                .background(
                    color = Color(0xFF1E90FF),
                    shape = RoundedCornerShape(6.dp)
                )
        )
    }
}
