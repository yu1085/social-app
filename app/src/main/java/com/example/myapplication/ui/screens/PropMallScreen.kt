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

/**
 * 道具商城页面
 */
@Composable
fun PropMallScreen(
    onBackClick: () -> Unit,
    onMyPropsClick: () -> Unit = {},
    onItemClick: (PropItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(0) } // 0=靓号, 1=进场特效, 2=首饰
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部导航栏
        TopNavigationBar(
            onBackClick = onBackClick,
            onMyPropsClick = onMyPropsClick
        )
        
        // 分类标签
        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        
        // 商品网格
        PropItemsGrid(
            category = selectedCategory,
            onItemClick = onItemClick
        )
    }
}

/**
 * 顶部导航栏
 */
@Composable
private fun TopNavigationBar(
    onBackClick: () -> Unit,
    onMyPropsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black
            )
        }
        
        Text(
            text = "道具商城",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "我的道具",
            fontSize = 14.sp,
            color = Color(0xFF1677FF),
            modifier = Modifier.clickable { onMyPropsClick() }
        )
    }
}

/**
 * 分类标签
 */
@Composable
private fun CategoryTabs(
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val categories = listOf("靓号", "进场特效", "首饰")
        
        categories.forEachIndexed { index, category ->
            CategoryTab(
                text = category,
                isSelected = selectedCategory == index,
                onClick = { onCategorySelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 分类标签项
 */
@Composable
private fun CategoryTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) Color(0xFFE0E0E0) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.Black else Color(0xFF666666),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * 商品网格
 */
@Composable
private fun PropItemsGrid(
    category: Int,
    onItemClick: (PropItem) -> Unit
) {
    val items = getPropItemsByCategory(category)
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            PropItemCard(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

/**
 * 商品卡片
 */
@Composable
private fun PropItemCard(
    item: PropItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // 商品展示区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF2D2D2D))
            ) {
                // 限量标签
                if (item.isLimited) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                Color(0xFF666666),
                                RoundedCornerShape(bottomStart = 4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "限量",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
                
                // 商品图标和ID
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 商品图标
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFFFD700),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "靓",
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A2E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // 商品ID
                    Text(
                        text = item.id,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // 价格区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.price,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // 金币图标
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "金币",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

/**
 * 道具商品数据类
 */
data class PropItem(
    val id: String,
    val price: String,
    val isLimited: Boolean = true
)

/**
 * 根据分类获取商品列表
 */
private fun getPropItemsByCategory(category: Int): List<PropItem> {
    return when (category) {
        0 -> { // 靓号
            listOf(
                PropItem("10000005", "88800"),
                PropItem("12345678", "128000"),
                PropItem("10000010", "88800"),
                PropItem("10000011", "58800"),
                PropItem("10000012", "58800"),
                PropItem("10000013", "58800"),
                PropItem("10000014", "58800"),
                PropItem("10000015", "58800"),
                PropItem("10000016", "58800"),
                PropItem("10000017", "58800")
            )
        }
        1 -> { // 进场特效
            listOf(
                PropItem("EFFECT001", "50000"),
                PropItem("EFFECT002", "75000"),
                PropItem("EFFECT003", "60000"),
                PropItem("EFFECT004", "45000"),
                PropItem("EFFECT005", "55000"),
                PropItem("EFFECT006", "65000")
            )
        }
        2 -> { // 首饰
            listOf(
                PropItem("JEWEL001", "30000"),
                PropItem("JEWEL002", "45000"),
                PropItem("JEWEL003", "35000"),
                PropItem("JEWEL004", "40000"),
                PropItem("JEWEL005", "25000"),
                PropItem("JEWEL006", "50000")
            )
        }
        else -> emptyList()
    }
}
