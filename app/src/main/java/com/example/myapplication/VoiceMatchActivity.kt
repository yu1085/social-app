package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class VoiceMatchActivity : ComponentActivity() {
    
    // 价格区间参数
    private var minPrice: Double = 20.0
    private var maxPrice: Double = 200.0
    private var defaultPrice: Double = 50.0
    private var onlineCount: Int = 1153
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取传递的参数
        minPrice = intent.getDoubleExtra("min_price", 20.0)
        maxPrice = intent.getDoubleExtra("max_price", 200.0)
        defaultPrice = intent.getDoubleExtra("default_price", 50.0)
        onlineCount = intent.getIntExtra("online_count", 1153)
        
        setContent {
            VoiceMatchScreen(
                minPrice = minPrice,
                maxPrice = maxPrice,
                defaultPrice = defaultPrice,
                onlineCount = onlineCount,
                onBackClick = { finish() },
                onRechargeClick = {
                    // 跳转到充值页面
                    android.widget.Toast.makeText(
                        this,
                        "跳转到充值页面",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                },
                onMatchClick = { selectedPrice ->
                    // 开始匹配，传递选择的价格
                    android.widget.Toast.makeText(
                        this,
                        "开始语音匹配，价格区间: $selectedPrice 元/分钟",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}

@Composable
fun VoiceMatchScreen(
    minPrice: Double = 20.0,
    maxPrice: Double = 200.0,
    defaultPrice: Double = 50.0,
    onlineCount: Int = 1153,
    onBackClick: () -> Unit,
    onRechargeClick: () -> Unit,
    onMatchClick: (Double) -> Unit
) {
    var selectedOption by remember { mutableStateOf(1) } // 默认选择人气女生
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDDA0DD),
                        Color(0xFFF0E6FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 顶部状态栏和标题
            VoiceMatchHeader(onBackClick = onBackClick)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 匹配统计信息
            VoiceMatchStatistics(
                waitingCount = "1169",
                matchedCount = "520"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 用户头像和匹配动画
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                VoiceMatchAnimationArea()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 充值提示
            VoiceRechargePrompt(
                coinCount = "0",
                onRechargeClick = onRechargeClick
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 匹配选项
            VoiceMatchOptions(
                selectedOption = selectedOption,
                onOptionSelect = { selectedOption = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 价格区间选择
            VoicePriceRangeSelector(
                minPrice = minPrice,
                maxPrice = maxPrice,
                defaultPrice = defaultPrice,
                onPriceChange = { /* 价格变化处理 */ }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 立即匹配按钮
            VoiceMatchButton(
                icon = Icons.Default.Phone,
                text = "立即匹配",
                onClick = { onMatchClick(defaultPrice) }
            )
        }
    }
}

@Composable
fun VoiceMatchHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = "语音速配",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.width(48.dp)) // 平衡布局
    }
}

@Composable
fun VoiceMatchStatistics(
    waitingCount: String,
    matchedCount: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "${waitingCount}位女生在等待匹配,今日已成功匹配${matchedCount}对",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun VoiceMatchAnimationArea() {
    Box(
        modifier = Modifier.size(280.dp)
    ) {
        // 背景圆形
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFFDDA0DD).copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        
        // 中心用户头像
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .background(
                    color = Color(0xFFDDA0DD),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "用户头像",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        // 周围女性头像
        VoiceFemaleAvatar(
            modifier = Modifier
                .size(50.dp)
                .offset(x = 80.dp, y = 40.dp),
            isTopLeft = true
        )
        
        VoiceFemaleAvatar(
            modifier = Modifier
                .size(50.dp)
                .offset(x = -80.dp, y = 40.dp),
            isTopLeft = false
        )
        
        VoiceFemaleAvatar(
            modifier = Modifier
                .size(50.dp)
                .offset(x = 60.dp, y = -60.dp),
            isTopLeft = false
        )
    }
}

@Composable
fun VoiceFemaleAvatar(
    modifier: Modifier = Modifier,
    isTopLeft: Boolean
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFFFB6C1),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "女性头像",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun VoiceRechargePrompt(
    coinCount: String,
    onRechargeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRechargeClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "金币",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = coinCount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "去充值 >",
            fontSize = 14.sp,
            color = Color(0xFF2196F3)
        )
    }
}

@Composable
fun VoiceMatchOptions(
    selectedOption: Int,
    onOptionSelect: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 提示文字
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "提示",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "勾选越多匹配越快(优先以最低价格匹配)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // 选项列表
            val options = listOf(
                Triple("活跃女生", "(真人认证)", "100-200/分钟"),
                Triple("人气女生", "(真人认证 不尬聊)", "200-350/分钟"),
                Triple("高颜女生", "(真人认证 颜值爆表)", "350-500/分钟")
            )
            
            options.forEachIndexed { index, (title, subtitle, price) ->
                VoiceMatchOptionItem(
                    title = title,
                    subtitle = subtitle,
                    price = price,
                    isSelected = selectedOption == index,
                    onSelect = { onOptionSelect(index) }
                )
                
                if (index < options.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun VoiceMatchOptionItem(
    title: String,
    subtitle: String,
    price: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "金币",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = price,
                fontSize = 12.sp,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF2196F3),
                    unselectedColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun VoiceMatchButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun VoicePriceRangeSelector(
    minPrice: Double,
    maxPrice: Double,
    defaultPrice: Double,
    onPriceChange: (Double) -> Unit
) {
    var selectedOption by remember { mutableStateOf(1) } // 默认选择人气女生
    
    // 定义语音匹配价格区间选项
    val priceOptions = listOf(
        Triple("活跃女生", "(真人认证)", "50-100/分钟"),
        Triple("人气女生", "(真人认证 不尬聊)", "100-150/分钟"),
        Triple("高颜女生", "(真人认证 颜值爆表)", "150-200/分钟")
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 提示文字
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "勾选越多匹配越快",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            
            // 价格区间选项
            priceOptions.forEachIndexed { index, (title, subtitle, priceRange) ->
                VoicePriceOptionItem(
                    title = title,
                    subtitle = subtitle,
                    priceRange = priceRange,
                    isSelected = selectedOption == index,
                    onClick = {
                        selectedOption = index
                        // 计算选择的价格区间中点
                        val priceMid = when (index) {
                            0 -> 75.0  // 50-100的中点
                            1 -> 125.0 // 100-150的中点
                            2 -> 175.0 // 150-200的中点
                            else -> defaultPrice
                        }
                        onPriceChange(priceMid)
                    }
                )
                
                if (index < priceOptions.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun VoicePriceOptionItem(
    title: String,
    subtitle: String,
    priceRange: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 选择圆圈
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 文字内容
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
        
        // 价格区间
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = priceRange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2196F3)
            )
        }
    }
}
