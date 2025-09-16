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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HowToEarnCoinsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HowToEarnCoinsScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToEarnCoinsScreen(
    onBackClick: () -> Unit
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
                        text = "如何赚聊币",
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
                // 主插图
                MainIllustration()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 邀请赚聊币部分
                InviteEarnCoinsSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 温馨提示部分
                WarmTipsSection()
            }
        }
    }
}

@Composable
fun MainIllustration() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 人物和金币的简单表示
                Text(
                    text = "🪙💰💎",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "躺在金币上的人",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun InviteEarnCoinsSection() {
    Column {
        Text(
            text = "邀请赚聊币",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF9800)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "邀请好友注册知聊，即可获得最高300聊币奖励；所邀请好友每次充值或收入，您都将获得其10%提出奖励",
            fontSize = 16.sp,
            color = Color.Black,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row {
            Text(
                text = "300",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = "聊币奖励",
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "10%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = "提成奖励",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun WarmTipsSection() {
    Column {
        Text(
            text = "温馨提示：",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 提示1
        TipItem(
            number = "1",
            text = "知聊是个绿色健康的平台，严禁色情哦，情节严重会封号处理。"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 提示2
        TipItem(
            number = "2",
            text = "视频聊天的时候记得装扮自己，打扮美美的，聊友会更喜欢，更愿意送礼哦。"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 提示3
        TipItem(
            number = "3",
            text = "如果一时没什么聊友找你聊天的话，可以发布动态试试，会有不少聊友关注到你呢。"
        )
    }
}

@Composable
fun TipItem(
    number: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    Color(0xFFFF9800),
                    RoundedCornerShape(12.dp)
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
        
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
