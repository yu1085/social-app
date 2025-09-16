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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.VolumeUp
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

class MyGuardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyGuardScreen(
                onBackClick = { finish() },
                onRankingClick = {
                    val intent = android.content.Intent(this, GuardIncomeRankingActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGuardScreen(
    onBackClick: () -> Unit,
    onRankingClick: () -> Unit
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
                        text = "守护",
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
                    IconButton(onClick = onRankingClick) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "守护收入榜",
                            tint = Color(0xFFFFD700)
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
                // 等待守护标签
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                        .clickable { selectedTab = 0 }
                ) {
                    Text(
                        text = "等待守护",
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
                                .background(Color.Black, RoundedCornerShape(2.dp))
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
                
                // 正在守护标签
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clickable { selectedTab = 1 }
                ) {
                    Text(
                        text = "正在守护",
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
                                .background(Color.Black, RoundedCornerShape(2.dp))
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
                
                // 最近守护标签
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .clickable { selectedTab = 2 }
                ) {
                    Text(
                        text = "最近守护",
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 2) Color.Black else Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (selectedTab == 2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(Color.Black, RoundedCornerShape(2.dp))
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
            
            // 信息提示条
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8E1))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "提示",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (selectedTab) {
                        0 -> "本周有0位女神等待你的守护"
                        1 -> "成为守护者,获得5%收入分成"
                        2 -> "最近3个月守护了0位女神"
                        else -> ""
                    },
                    fontSize = 14.sp,
                    color = Color(0xFFE65100)
                )
            }
            
            // 内容区域
            when (selectedTab) {
                0 -> WaitingGuardContent()
                1 -> CurrentGuardContent()
                2 -> RecentGuardContent()
            }
        }
    }
}

@Composable
fun WaitingGuardContent() {
    EmptyStateContent()
}

@Composable
fun CurrentGuardContent() {
    EmptyStateContent()
}

@Composable
fun RecentGuardContent() {
    EmptyStateContent()
}

@Composable
fun EmptyStateContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "暂无数据",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE0E0E0)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "暂无数据",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}
