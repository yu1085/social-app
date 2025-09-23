package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.WealthLevelCard
import com.example.myapplication.ui.components.PrivilegeList
import com.example.myapplication.ui.components.WealthLevelRules
import com.example.myapplication.viewmodel.WealthLevelViewModel
import com.example.myapplication.model.WealthLevelData
import com.example.myapplication.model.PrivilegeType
import kotlin.random.Random

/**
 * 财富等级主页面
 * 显示用户当前等级、特权等信息
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthLevelScreen(
    onBackClick: () -> Unit,
    onRulesClick: () -> Unit = {},
    token: String? = null,
    modifier: Modifier = Modifier
) {
    val viewModel: WealthLevelViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    // 加载数据
    LaunchedEffect(token) {
        token?.let { 
            viewModel.loadWealthLevel(it)
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F0F23),
                        Color(0xFF000000)
                    ),
                    radius = 1200f
                )
            )
            .drawWithContent {
                drawContent()
                // 绘制动态星空背景
                val starCount = 80
                repeat(starCount) {
                    val x = Random.nextFloat() * size.width
                    val y = Random.nextFloat() * size.height
                    val alpha = Random.nextFloat() * 0.9f + 0.1f
                    val starSize = Random.nextFloat() * 3f + 0.5f
                    
                    // 绘制星星
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = starSize,
                        center = Offset(x, y)
                    )
                    
                    // 添加星星闪烁效果
                    if (Random.nextFloat() > 0.7f) {
                        drawCircle(
                            color = Color(0xFFFFD700).copy(alpha = alpha * 0.6f),
                            radius = starSize * 1.5f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 自定义顶部导航栏
            CustomTopBar(
                onBackClick = onBackClick,
                onRulesClick = onRulesClick
            )
            
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFFD700),
                        strokeWidth = 3.dp
                    )
                }
            } else if (uiState.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "加载失败: ${uiState.error}",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        
                        Button(
                            onClick = { 
                                token?.let { viewModel.refresh(it) }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700)
                            )
                        ) {
                            Text("重试", color = Color.Black)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // 等级进度条
                    item {
                        LevelProgressBar(
                            currentLevel = uiState.wealthLevel?.levelName ?: "普通",
                            currentWealth = uiState.wealthLevel?.wealthValue ?: 0
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    // 当前等级卡片
                    uiState.wealthLevel?.let { level: WealthLevelData ->
                        item {
                            PremiumLevelCard(
                                levelName = level.levelName,
                                levelIcon = level.levelIcon,
                                levelColor = level.levelColor,
                                wealthValue = level.wealthValue,
                                progressPercentage = level.progressPercentage,
                                nextLevelName = level.nextLevelName,
                                nextLevelRequirement = level.nextLevelRequirement
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    
                    // 特权区域
                    if (uiState.privileges.isNotEmpty()) {
                        item {
                            PremiumPrivilegeSection(privileges = uiState.privileges)
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    
                    // 促销商城按钮
                    item {
                        PremiumPromotionMallButton()
                    }
                }
            }
        }
    }
}

/**
 * 自定义顶部导航栏
 */
@Composable
private fun CustomTopBar(
    onBackClick: () -> Unit,
    onRulesClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // 标题
        Text(
            text = "财富等级",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        // 规则说明按钮
        TextButton(
            onClick = onRulesClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFFFFD700)
            )
        ) {
            Text(
                text = "规则说明",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 等级进度条
 */
@Composable
private fun LevelProgressBar(
    currentLevel: String,
    currentWealth: Int
) {
    val levels = listOf("白银", "黄金", "铂金")
    val currentIndex = when (currentLevel) {
        "白银" -> 0
        "黄金" -> 1
        "铂金" -> 2
        else -> 0
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 进度条
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            levels.forEachIndexed { index, level ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 等级名称
                    Text(
                        text = level,
                        color = if (index <= currentIndex) Color(0xFFFFD700) else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // 连接线
                    if (index < levels.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(
                                    if (index < currentIndex) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.3f),
                                    RoundedCornerShape(1.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 星星装饰
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) { index ->
                Text(
                    text = if (index <= currentIndex) "⭐" else "☆",
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * 精美等级卡片
 */
@Composable
private fun PremiumLevelCard(
    levelName: String,
    levelIcon: String,
    levelColor: String,
    wealthValue: Int,
    progressPercentage: Double,
    nextLevelName: String?,
    nextLevelRequirement: Int?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.15f),
                            Color(0xFFFFA500).copy(alpha = 0.08f),
                            Color(0xFFFF8C00).copy(alpha = 0.05f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.3f),
                            Color(0xFFFFA500).copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(28.dp)
            ) {
                // 标题
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "当前等级",
                        color = Color(0xFFFFD700),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "◆",
                        color = Color(0xFFFFD700),
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 等级信息
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = levelName,
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "我的财富值: ",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "$wealthValue",
                                color = Color(0xFFFFD700),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " ?",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 16.sp
                            )
                        }
                        
                        if (nextLevelRequirement != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "财富值达${nextLevelRequirement}可享当前等级权益",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    
                    // 等级徽章
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFFA500),
                                        Color(0xFFFF8C00),
                                        Color(0xFFFF4500)
                                    )
                                ),
                                RoundedCornerShape(25.dp)
                            )
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(25.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👑",
                            fontSize = 50.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 精美特权区域
 */
@Composable
private fun PremiumPrivilegeSection(privileges: List<PrivilegeType>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E).copy(alpha = 0.9f),
                            Color(0xFF16213E).copy(alpha = 0.8f)
                        )
                    ),
                    RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // 标题
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "◇",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "尊享${privileges.size}项权益",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "◇",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 特权网格
                val privilegeChunks = privileges.chunked(3)
                privilegeChunks.forEach { rowPrivileges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowPrivileges.forEach { privilege ->
                            PremiumPrivilegeItem(
                                privilege = privilege,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // 填充空白
                        repeat(3 - rowPrivileges.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    if (rowPrivileges != privilegeChunks.last()) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

/**
 * 精美特权项目
 */
@Composable
private fun PremiumPrivilegeItem(
    privilege: PrivilegeType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 特权图标
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.3f),
                            Color(0xFFFFA500).copy(alpha = 0.1f)
                        )
                    ),
                    CircleShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // 根据特权类型显示不同图标
            val icon = when (privilege) {
                PrivilegeType.LUCKY_NUMBER_DISCOUNT -> "💎"
                PrivilegeType.WEEKLY_PROMOTION -> "🛍️"
                PrivilegeType.VIP_DISCOUNT -> "👑"
                PrivilegeType.EFFECT_DISCOUNT -> "✨"
                PrivilegeType.FREE_VIP -> "🎁"
                PrivilegeType.FREE_EFFECT -> "🌟"
                PrivilegeType.EXCLUSIVE_EFFECT -> "🎭"
                PrivilegeType.LUCKY_NUMBER_CUSTOM -> "🔢"
                PrivilegeType.EXCLUSIVE_GIFT -> "🎀"
                PrivilegeType.EXCLUSIVE_SERVICE -> "🎧"
            }
            
            Text(
                text = icon,
                fontSize = 28.sp
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 特权名称
        Text(
            text = privilege.displayName,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 16.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 折扣标签（如果有）
        if (privilege == PrivilegeType.LUCKY_NUMBER_DISCOUNT || 
            privilege == PrivilegeType.VIP_DISCOUNT || 
            privilege == PrivilegeType.EFFECT_DISCOUNT) {
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFFFF4444),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (privilege) {
                        PrivilegeType.LUCKY_NUMBER_DISCOUNT -> "8折"
                        PrivilegeType.VIP_DISCOUNT -> "8折"
                        PrivilegeType.EFFECT_DISCOUNT -> "7折"
                        else -> ""
                    },
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 精美促销商城按钮
 */
@Composable
private fun PremiumPromotionMallButton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 跳转到促销商城 */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "促销商城",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "促销商城",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = "→",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 财富等级数据类
 */
data class WealthLevelData(
    val levelName: String,
    val levelIcon: String,
    val levelColor: String,
    val wealthValue: Int,
    val progressPercentage: Double,
    val nextLevelName: String?,
    val nextLevelRequirement: Int?
)

