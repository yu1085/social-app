package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.model.PrivilegeType
import com.example.myapplication.viewmodel.WealthLevelViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 财富等级主页面
 * 显示用户当前等级、特权等信息
 */
class WealthLevelMainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WealthLevelMainContent(activity = this)
                }
            }
        }
    }
}

/**
 * 财富等级主页面内容
 */
@Composable
private fun WealthLevelMainContent(activity: ComponentActivity) {
    val authManager = AuthManager.getInstance(activity)
    val token = authManager.getToken()
    val viewModel: WealthLevelViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    // 加载数据
    LaunchedEffect(token) {
        token?.let { 
            viewModel.loadWealthLevel(it)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = "财富等级",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { activity.finish() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White
                    )
                }
            },
            actions = {
                // 右上角规则说明按钮
                IconButton(
                    onClick = {
                        val intent = android.content.Intent(activity, WealthLevelActivity::class.java)
                        activity.startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "规则说明",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1E1E1E)
            )
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "加载失败",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error ?: "未知错误",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { token?.let { viewModel.loadWealthLevel(it) } }
                    ) {
                        Text("重试")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                // 当前等级卡片
                uiState.wealthLevel?.let { level ->
                    item {
                        CurrentLevelCard(
                            levelName = level.levelName,
                            levelIcon = level.levelIcon,
                            levelColor = level.levelColor,
                            wealthValue = level.wealthValue,
                            progressPercentage = level.progressPercentage,
                            nextLevelName = level.nextLevelName,
                            nextLevelRequirement = level.nextLevelRequirement
                        )
                    }
                }
                
                // 特权列表
                if (uiState.privileges.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        PrivilegeSection(privileges = uiState.privileges)
                    }
                }
                
                // 促销商城侧边栏
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    PromotionMallButton()
                }
            }
        }
    }
}

/**
 * 当前等级卡片
 */
@Composable
private fun CurrentLevelCard(
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
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "当前等级",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = levelName,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 等级图标
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = levelIcon,
                        fontSize = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "我的财富值: $wealthValue",
                color = Color.White,
                fontSize = 16.sp
            )
            
            if (nextLevelRequirement != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "财富值达${nextLevelRequirement}可享当前等级权益",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * 特权区域
 */
@Composable
private fun PrivilegeSection(privileges: List<PrivilegeType>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💎",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "尊享${privileges.size}项权益",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 特权网格
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(privileges.chunked(3)) { rowPrivileges ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowPrivileges.forEach { privilege ->
                            PrivilegeItem(privilege = privilege)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 特权项目
 */
@Composable
private fun PrivilegeItem(privilege: PrivilegeType) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3A3A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 特权图标
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 特权名称
            Text(
                text = privilege.displayName,
                color = Color.White,
                fontSize = 10.sp,
                maxLines = 2
            )
        }
    }
}

/**
 * 促销商城按钮
 */
@Composable
private fun PromotionMallButton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 跳转到促销商城 */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
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
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
    }
}
