package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.*
import com.example.myapplication.viewmodel.LuckyNumberViewModel
import com.example.myapplication.viewmodel.LuckyNumberUiState

/**
 * 道具商城页面
 */
@Composable
fun PropMallScreen(
    onBackClick: () -> Unit,
    onMyPropsClick: () -> Unit = {},
    onItemClick: (PropItem) -> Unit = {},
    token: String? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(0) } // 0=靓号, 1=进场特效, 2=首饰
    val luckyNumberViewModel: LuckyNumberViewModel = viewModel()
    val luckyNumberUiState by luckyNumberViewModel.uiState.collectAsState()
    
    // 加载靓号数据
    LaunchedEffect(selectedCategory) {
        if (selectedCategory == 0) { // 靓号分类
            luckyNumberViewModel.loadLuckyNumbers()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        TopAppBar(
            title = { Text("道具商城") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = onMyPropsClick) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = "我的道具")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Black
            )
        )
        
        // 分类标签
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("靓号", "进场特效", "首饰").forEachIndexed { index, category ->
                FilterChip(
                    onClick = { selectedCategory = index },
                    label = { Text(category) },
                    selected = selectedCategory == index,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1677FF),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF0F0F0),
                        labelColor = Color.Black
                    )
                )
            }
        }
        
        // 内容区域
        when (selectedCategory) {
            0 -> LuckyNumberGrid(
                uiState = luckyNumberUiState,
                onItemClick = onItemClick,
                onPurchaseClick = { luckyNumber ->
                    // 直接跳转到购买页面
                    onItemClick(PropItem(luckyNumber.number, luckyNumber.price.toString(), luckyNumber = luckyNumber))
                }
            )
            1 -> ComingSoonContent("进场特效")
            2 -> ComingSoonContent("首饰")
        }
    }
}

/**
 * 靓号网格
 */
@Composable
private fun LuckyNumberGrid(
    uiState: LuckyNumberUiState,
    onItemClick: (PropItem) -> Unit,
    onPurchaseClick: (LuckyNumber) -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "错误",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Red
                    )
                    Text(
                        text = uiState.error,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.luckyNumbers) { luckyNumber ->
                    LuckyNumberCard(
                        luckyNumber = luckyNumber,
                        onClick = { 
                            // 直接跳转到购买页面，传递完整的LuckyNumber对象
                            onItemClick(PropItem(luckyNumber.number, luckyNumber.price.toString(), luckyNumber = luckyNumber))
                        },
                        onPurchaseClick = { onPurchaseClick(luckyNumber) }
                    )
                }
            }
        }
    }
}

/**
 * 靓号卡片
 */
@Composable
private fun LuckyNumberCard(
    luckyNumber: LuckyNumber,
    onClick: () -> Unit,
    onPurchaseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 靓号图标
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color(android.graphics.Color.parseColor(luckyNumber.tier.iconColor)),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = luckyNumber.tier.icon,
                    fontSize = 24.sp,
                    color = Color(0xFF1A1A2E),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 靓号信息
            Text(
                text = luckyNumber.number,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = luckyNumber.tier.displayName,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 价格信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = luckyNumber.price.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B)
                )
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "金币",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFFD700)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 购买按钮
            Button(
                onClick = onPurchaseClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1677FF)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "购买",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * 即将上线内容
 */
@Composable
private fun ComingSoonContent(category: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Construction,
                contentDescription = "建设中",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Text(
                text = "$category 即将上线",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = "敬请期待",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 道具项数据类
 */
data class PropItem(
    val id: String,
    val price: String,
    val isLimited: Boolean = true,
    val luckyNumber: LuckyNumber? = null
)