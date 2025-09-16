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
import androidx.compose.material.icons.filled.Star
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
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 邀请好友页面Activity
 * 实现"呼朋唤友 赚现金"主题的邀请奖励页面
 */
class InviteFriendsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                InviteFriendsScreen(
                    onBackClick = { finish() },
                    onInviteClick = {
                        // 处理立即邀请点击事件
                        // 这里可以添加分享功能
                    }
                )
            }
        }
    }
}

/**
 * 邀请好友页面内容
 */
@Composable
fun InviteFriendsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onInviteClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF6B9D), // 粉色渐变
                        Color(0xFFFF8E53)  // 橙色渐变
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
            
            // 主标题区域
            MainTitleSection()
            
            // 用户提现通知
            WithdrawalNotificationSection()
            
            // 奖励详情卡片
            RewardDetailsCard()
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // 立即邀请按钮
            InviteButton(onInviteClick = onInviteClick)
            
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
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 主标题区域
 */
@Composable
private fun MainTitleSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "呼朋唤友",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "赚现金",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 用户提现通知
 */
@Composable
private fun WithdrawalNotificationSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B9D).copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 用户头像
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "琦",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B9D)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "琦* 刚刚提现了¥58",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

/**
 * 奖励详情卡片
 */
@Composable
private fun RewardDetailsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 卡片标题和规则按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "最高可获得以下奖励",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Button(
                    onClick = { /* 显示规则 */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B9D)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "规则",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 邀请男用户部分
            InviteSection(
                title = "邀请男用户",
                rewards = listOf(
                    RewardItem("1元", "注册成功"),
                    RewardItem("2元", "通话达到3分钟"),
                    RewardItem("10%", "充值提成")
                )
            )
            
            // 分隔线
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )
            
            // 邀请女用户部分
            InviteSection(
                title = "邀请女用户",
                rewards = listOf(
                    RewardItem("10%", "收益提成")
                )
            )
        }
    }
}

/**
 * 邀请部分组件
 */
@Composable
private fun InviteSection(
    title: String,
    rewards: List<RewardItem>
) {
    Column {
        // 标题带星星
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 奖励项
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            rewards.forEach { reward ->
                RewardItemCard(reward = reward)
            }
        }
    }
}

/**
 * 奖励项卡片
 */
@Composable
private fun RewardItemCard(reward: RewardItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = reward.amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B9D)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = reward.description,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 立即邀请按钮
 */
@Composable
private fun InviteButton(onInviteClick: () -> Unit) {
    Button(
        onClick = onInviteClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF8E53)
        ),
        shape = RoundedCornerShape(25.dp)
    ) {
        Text(
            text = "立即邀请",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/**
 * 背景装饰元素
 */
@Composable
private fun BackgroundDecorations() {
    // 这里可以添加一些装饰性的图标，如红包、金钱等
    // 由于没有具体的图标资源，这里用简单的形状代替
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 右上角装饰
        Box(
            modifier = Modifier
                .size(40.dp)
                .offset(x = 300.dp, y = 100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        )
        
        // 左上角装饰
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x = 50.dp, y = 150.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
        )
    }
}

/**
 * 奖励项数据类
 */
data class RewardItem(
    val amount: String,
    val description: String
)
