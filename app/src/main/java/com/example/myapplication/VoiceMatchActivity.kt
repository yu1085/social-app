package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Phone
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
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.network.NetworkConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class VoiceMatchActivity : ComponentActivity() {

    companion object {
        private const val TAG = "VoiceMatchActivity"
    }

    // 价格区间参数
    private var minPrice: Double = 20.0
    private var maxPrice: Double = 500.0
    private var defaultPrice: Double = 50.0
    private var onlineCount: Int = 1153

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取传递的参数
        minPrice = intent.getDoubleExtra("min_price", 20.0)
        maxPrice = intent.getDoubleExtra("max_price", 500.0)
        defaultPrice = intent.getDoubleExtra("default_price", 100.0)
        onlineCount = intent.getIntExtra("online_count", 1153)

        setContent {
            val isMatching = remember { mutableStateOf(false) }
            val matchingStatus = remember { mutableStateOf("") }

            VoiceMatchScreen(
                minPrice = minPrice,
                maxPrice = maxPrice,
                defaultPrice = defaultPrice,
                onlineCount = onlineCount,
                isMatching = isMatching.value,
                matchingStatus = matchingStatus.value,
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
                    startMatching(isMatching, matchingStatus, selectedPrice)
                },
                onCancelMatch = {
                    isMatching.value = false
                    matchingStatus.value = ""
                    android.widget.Toast.makeText(
                        this,
                        "已取消匹配",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun startMatching(
        isMatching: MutableState<Boolean>,
        matchingStatus: MutableState<String>,
        selectedPrice: Double
    ) {
        if (isMatching.value) return

        Log.d(TAG, "开始语音匹配，价格: $selectedPrice 元/分钟")
        isMatching.value = true
        matchingStatus.value = "正在为您匹配合适的用户..."

        lifecycleScope.launch {
            try {
                // 模拟匹配延迟 (2-5秒)
                val delay = (2000..5000).random().toLong()
                delay(delay)

                // 调用后端API获取随机女性用户
                Log.d(TAG, "调用API获取用户列表...")
                val apiService = NetworkConfig.getApiService()
                val response = apiService.searchUsers(
                    null, // keyword
                    "FEMALE", // gender
                    null, // location
                    null, // minAge
                    null, // maxAge
                    0, // page
                    20 // size
                ).awaitResponse()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true && !apiResponse.data.isNullOrEmpty()) {
                        val users = apiResponse.data
                        val randomUser = users.random()

                        Log.d(TAG, "匹配成功！用户ID: ${randomUser.id}, 昵称: ${randomUser.nickname}")
                        matchingStatus.value = "匹配成功！正在连接..."

                        // 延迟1秒后跳转
                        delay(1000)

                        // 跳转到视频通话界面
                        val intent = Intent(this@VoiceMatchActivity, VideoChatActivity::class.java)
                        intent.putExtra("userId", randomUser.id)
                        intent.putExtra("userName", randomUser.nickname)
                        intent.putExtra("userAvatar", randomUser.avatarUrl)
                        intent.putExtra("isVoiceOnly", true)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w(TAG, "未找到合适的用户")
                        onMatchFailed(isMatching, matchingStatus, "未找到合适的用户，请稍后重试")
                    }
                } else {
                    Log.e(TAG, "API调用失败: ${response.code()}")
                    onMatchFailed(isMatching, matchingStatus, "匹配失败，请稍后重试")
                }
            } catch (e: Exception) {
                Log.e(TAG, "匹配过程出错", e)
                onMatchFailed(isMatching, matchingStatus, "网络错误：${e.message}")
            }
        }
    }

    private fun onMatchFailed(
        isMatching: MutableState<Boolean>,
        matchingStatus: MutableState<String>,
        message: String
    ) {
        isMatching.value = false
        matchingStatus.value = ""
        android.widget.Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun VoiceMatchScreen(
    minPrice: Double = 20.0,
    maxPrice: Double = 500.0,
    defaultPrice: Double = 50.0,
    onlineCount: Int = 1153,
    isMatching: Boolean = false,
    matchingStatus: String = "",
    onBackClick: () -> Unit,
    onRechargeClick: () -> Unit,
    onMatchClick: (Double) -> Unit,
    onCancelMatch: () -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf(1) } // 默认选择人气女生
    var selectedPrice by remember { mutableStateOf(125.0) } // 默认人气女生的价格中点
    
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
            MatchStatistics(
                waitingCount = "1169",
                matchedCount = "520"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 用户头像和匹配动画
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MatchAnimationArea(isMatching = isMatching)

                    if (isMatching && matchingStatus.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = matchingStatus,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // 充值提示
            RechargePrompt(
                coinCount = "0",
                onRechargeClick = onRechargeClick
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // 价格区间选择
            PriceRangeSelector(
                minPrice = minPrice,
                maxPrice = maxPrice,
                defaultPrice = defaultPrice,
                selectedOption = selectedOption,
                onOptionSelect = { selectedOption = it },
                onPriceChange = { selectedPrice = it }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 立即匹配/取消匹配按钮
            if (isMatching) {
                MatchButton(
                    icon = Icons.Default.Phone,
                    text = "取消匹配",
                    onClick = onCancelMatch,
                    backgroundColor = Color(0xFFFF5722) // 红色表示取消
                )
            } else {
                MatchButton(
                    icon = Icons.Default.Phone,
                    text = "立即匹配",
                    onClick = { onMatchClick(selectedPrice) }
                )
            }
        }
    }
}

@Composable
private fun VoiceMatchHeader(onBackClick: () -> Unit) {
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
private fun MatchStatistics(
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
private fun MatchAnimationArea(isMatching: Boolean = false) {
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
        FemaleAvatar(
            modifier = Modifier
                .size(50.dp)
                .offset(x = 80.dp, y = 40.dp),
            isTopLeft = true
        )
        
        FemaleAvatar(
            modifier = Modifier
                .size(50.dp)
                .offset(x = -80.dp, y = 40.dp),
            isTopLeft = false
        )
        
        FemaleAvatar(
            modifier = Modifier
                .size(50.dp)
                .offset(x = 60.dp, y = -60.dp),
            isTopLeft = false
        )
    }
}

@Composable
private fun FemaleAvatar(
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
private fun RechargePrompt(
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
private fun MatchOptions(
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
                MatchOptionItem(
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
private fun MatchOptionItem(
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
private fun MatchButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Color(0xFF2196F3)
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
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
private fun PriceRangeSelector(
    minPrice: Double,
    maxPrice: Double,
    defaultPrice: Double,
    selectedOption: Int,
    onOptionSelect: (Int) -> Unit,
    onPriceChange: (Double) -> Unit
) {
    
    // 定义价格区间选项
    val priceOptions = listOf(
        Triple("活跃女生", "(真人认证)", "100-200/分钟"),
        Triple("人气女生", "(真人认证 不尬聊)", "200-350/分钟"),
        Triple("高颜女生", "(真人认证 颜值爆表)", "350-500/分钟")
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
                PriceOptionItem(
                    title = title,
                    subtitle = subtitle,
                    priceRange = priceRange,
                    isSelected = selectedOption == index,
                    onClick = {
                        onOptionSelect(index)
                        // 计算选择的价格区间中点
                        val priceMid = when (index) {
                            0 -> 120.0 // 100-200的中点
                            1 -> 125.0 // 200-350的中点
                            2 -> 425.0 // 350-500的中点
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
private fun PriceOptionItem(
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
