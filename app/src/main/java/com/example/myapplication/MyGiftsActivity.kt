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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MyGiftsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyGiftsScreen(
                onBackClick = { finish() },
                onDetailsClick = {
                    val intent = android.content.Intent(this, GiftDetailsActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGiftsScreen(
    onBackClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
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
                        text = "我的礼物",
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
                actions = {
                    TextButton(
                        onClick = onDetailsClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF1976D2)
                        )
                    ) {
                        Text(
                            text = "明细",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
            
            // 标签页
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 收到礼物标签
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .clickable { selectedTab = 0 }
                ) {
                    Text(
                        text = "收到礼物",
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) Color.Black else Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (selectedTab == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF1976D2),
                                            Color(0xFF42A5F5)
                                        )
                                    ),
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
                
                // 送出礼物标签
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .clickable { selectedTab = 1 }
                ) {
                    Text(
                        text = "送出礼物",
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 1) Color.Black else Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (selectedTab == 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF1976D2),
                                            Color(0xFF42A5F5)
                                        )
                                    ),
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
            
            // 内容区域
            when (selectedTab) {
                0 -> ReceivedGiftsContent()
                1 -> SentGiftsContent()
            }
        }
    }
}

@Composable
fun ReceivedGiftsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 礼物盒图标
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // 礼物盒主体
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF42A5F5)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                
                // 礼物盒丝带
                Box(
                    modifier = Modifier
                        .size(100.dp, 8.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                )
                
                // 礼物盒装饰线
                Box(
                    modifier = Modifier
                        .size(60.dp, 4.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "当前还没有人给你送礼物哦",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SentGiftsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 礼物盒图标
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // 礼物盒主体
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF42A5F5)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                
                // 礼物盒丝带
                Box(
                    modifier = Modifier
                        .size(100.dp, 8.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                )
                
                // 礼物盒装饰线
                Box(
                    modifier = Modifier
                        .size(60.dp, 4.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "当前还没有送出过礼物哦~",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
