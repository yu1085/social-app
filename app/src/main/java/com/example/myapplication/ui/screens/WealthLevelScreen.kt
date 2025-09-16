package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

/**
 * 财富等级页面
 */
@Composable
fun WealthLevelScreen(
    onBackClick: () -> Unit,
    onRulesClick: () -> Unit = {},
    onPromotionMallClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 顶部导航栏
            TopNavigationBar(
                onBackClick = onBackClick,
                onRulesClick = onRulesClick
            )
            
            // 等级进度指示器
            LevelProgressIndicator()
            
            // 当前等级信息卡片
            CurrentLevelCard()
            
            // 权益展示区域
            PrivilegesSection()
            
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        // 右侧促销商城按钮
        PromotionMallButton(
            onPromotionMallClick = onPromotionMallClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

/**
 * 顶部导航栏
 */
@Composable
private fun TopNavigationBar(
    onBackClick: () -> Unit,
    onRulesClick: () -> Unit
) {
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
                tint = Color.White
            )
        }
        
        Text(
            text = "财富等级",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "规则说明*",
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.clickable { onRulesClick() }
        )
    }
}

/**
 * 等级进度指示器
 */
@Composable
private fun LevelProgressIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // 进度条
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Color(0xFF2D2D2D),
                    RoundedCornerShape(2.dp)
                )
        ) {
            // 金色进度条
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f) // 60%进度
                    .background(
                        Color(0xFFFFD700),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 等级标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 白银
            LevelLabel(
                text = "白银",
                isActive = false,
                modifier = Modifier.weight(1f)
            )
            
            // 黄金
            LevelLabel(
                text = "黄金",
                isActive = true,
                modifier = Modifier.weight(1f)
            )
            
            // 铂金
            LevelLabel(
                text = "铂金",
                isActive = false,
                isLocked = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 等级标签
 */
@Composable
private fun LevelLabel(
    text: String,
    isActive: Boolean,
    isLocked: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isActive) {
            // 当前等级显示金色圆圈
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        Color(0xFFFFD700),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            Color(0xFFFFD700),
                            CircleShape
                        )
                )
            }
        } else if (isLocked) {
            // 锁定状态显示锁图标
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "锁定",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF666666)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isActive) Color(0xFFFFD700) else Color.White,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * 当前等级信息卡片
 */
@Composable
private fun CurrentLevelCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 当前等级标签
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFFFD700),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "当前等级",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 等级名称
                Text(
                    text = "黄金",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 财富值信息
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "我的财富值: 9668?",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = "帮助",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "财富值达5000可享当前等级权益",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
            
            // 右侧徽章
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFFFFD700),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "黄金徽章",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF1A1A2E)
                )
            }
        }
    }
}

/**
 * 权益展示区域
 */
@Composable
private fun PrivilegesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 标题
        Text(
            text = "◇ 尊享黄金4项权益 ◇",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 权益网格
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 第一行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrivilegeItem(
                    icon = Icons.Default.DirectionsCar,
                    title = "进场特效折扣",
                    discount = "7折",
                    isUnlocked = true,
                    modifier = Modifier.weight(1f)
                )
                PrivilegeItem(
                    icon = Icons.Default.Badge,
                    title = "靓号购买折扣",
                    discount = "8折",
                    isUnlocked = true,
                    modifier = Modifier.weight(1f)
                )
                PrivilegeItem(
                    icon = Icons.Default.LocalOffer,
                    title = "每周促销",
                    discount = null,
                    isUnlocked = true,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 第二行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrivilegeItem(
                    icon = Icons.Default.CheckCircle,
                    title = "购买会员折扣",
                    discount = "8折",
                    isUnlocked = true,
                    modifier = Modifier.weight(1f)
                )
                PrivilegeItem(
                    icon = Icons.Default.HeadsetMic,
                    title = "专属客服特权",
                    discount = null,
                    isUnlocked = false,
                    modifier = Modifier.weight(1f)
                )
                PrivilegeItem(
                    icon = Icons.Default.DirectionsCar,
                    title = "专属进场特效",
                    discount = null,
                    isUnlocked = false,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 第三行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrivilegeItem(
                    icon = Icons.Default.CardGiftcard,
                    title = "专属礼物特权",
                    discount = null,
                    isUnlocked = false,
                    modifier = Modifier.weight(1f)
                )
                PrivilegeItem(
                    icon = Icons.Default.Star,
                    title = "靓号定制特权",
                    discount = null,
                    isUnlocked = false,
                    modifier = Modifier.weight(1f)
                )
                // 第三列留空
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * 权益项
 */
@Composable
private fun PrivilegeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    discount: String?,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            // 权益图标
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        if (isUnlocked) Color(0xFFFFD700) else Color(0xFF666666),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = if (isUnlocked) Color(0xFF1A1A2E) else Color(0xFF999999)
                )
            }
            
            // 折扣标签
            if (discount != null && isUnlocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            Color(0xFFFF4444),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = discount,
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 锁定图标
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "锁定",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp),
                    tint = Color(0xFF999999)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (isUnlocked) Color.White else Color(0xFF666666),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

/**
 * 促销商城按钮
 */
@Composable
private fun PromotionMallButton(
    onPromotionMallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(60.dp)
            .height(120.dp)
            .background(
                Color(0xFFFFD700),
                RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
            )
            .clickable { onPromotionMallClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "促销\n商城",
            fontSize = 12.sp,
            color = Color(0xFF1A1A2E),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}
