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

class GuardIncomeRankingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuardIncomeRankingScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardIncomeRankingScreen(
    onBackClick: () -> Unit
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
                        text = "守护收入榜",
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
            
            // 标签页
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 周榜标签
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .clickable { selectedTab = 0 }
                ) {
                    Text(
                        text = "周榜",
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
                
                // 总榜标签
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .clickable { selectedTab = 1 }
                ) {
                    Text(
                        text = "总榜",
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
                0 -> WeeklyRankingContent()
                1 -> TotalRankingContent()
            }
        }
    }
}

@Composable
fun WeeklyRankingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 排行榜数据
        val weeklyRankings = listOf(
            RankingItem(1, "1*****7", "本周守护1位女神", 174731, true),
            RankingItem(2, "2*****5", "本周守护3位女神", 160939, true),
            RankingItem(3, "2*****8", "本周守护2位女神", 138468, true),
            RankingItem(4, "1*****5", "本周守护1位女神", 118393, false),
            RankingItem(5, "1*****8", "本周守护1位女神", 92458, false),
            RankingItem(6, "2*****5", "本周守护1位女神", 91892, false),
            RankingItem(7, "1*****0", "本周守护1位女神", 90021, false),
            RankingItem(8, "1*****9", "本周守护1位女神", 85587, false),
            RankingItem(9, "2*****7", "本周守护1位女神", 84937, false)
        )
        
        weeklyRankings.forEach { item ->
            RankingItemCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 当前用户状态
        CurrentUserStatus(
            income = "00.00",
            hasRanking = false
        )
    }
}

@Composable
fun TotalRankingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 总榜数据
        val totalRankings = listOf(
            RankingItem(1, "1*****7", "累计守护71位女神", 4685343, true),
            RankingItem(2, "2*****8", "累计守护267位女神", 4391736, true),
            RankingItem(3, "2*****8", "累计守护65位女神", 4063941, true),
            RankingItem(4, "1*****5", "累计守护243位女神", 4011234, false),
            RankingItem(5, "1*****8", "累计守护177位女神", 3954549, false),
            RankingItem(6, "2*****5", "累计守护27位女神", 3826816, false),
            RankingItem(7, "1*****0", "累计守护84位女神", 3806888, false),
            RankingItem(8, "1*****9", "累计守护157位女神", 3703192, false),
            RankingItem(9, "2*****7", "累计守护93位女神", 3547416, false)
        )
        
        totalRankings.forEach { item ->
            RankingItemCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 当前用户状态
        CurrentUserStatus(
            income = "00.00",
            hasRanking = false
        )
    }
}

@Composable
fun RankingItemCard(item: RankingItem) {
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
            // 排名图标
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                when (item.rank) {
                    1 -> Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "第一名",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                    2 -> Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "第二名",
                        tint = Color(0xFFC0C0C0),
                        modifier = Modifier.size(32.dp)
                    )
                    3 -> Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "第三名",
                        tint = Color(0xFFCD7F32),
                        modifier = Modifier.size(32.dp)
                    )
                    else -> Text(
                        text = item.rank.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 头像
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD),
                                Color(0xFFBBDEFB)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 用户信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ID: ${item.userId}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            // 收入
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.income.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "🪙",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentUserStatus(
    income: String,
    hasRanking: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 当前用户头像
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD),
                                Color(0xFFBBDEFB)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 当前用户信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (hasRanking) "累计收益$income" else "本周收益$income",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "🪙",
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (hasRanking) "排名: 未上榜" else "暂无排名",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

data class RankingItem(
    val rank: Int,
    val userId: String,
    val description: String,
    val income: Int,
    val isTopThree: Boolean
)
