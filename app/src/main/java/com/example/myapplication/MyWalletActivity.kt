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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.dto.WalletDTO
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.WalletViewModel
import java.math.BigDecimal

class MyWalletActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val walletViewModel: WalletViewModel = viewModel()
                
                // 加载钱包数据
                LaunchedEffect(Unit) {
                    walletViewModel.loadWalletBalance()
                }
                
                MyWalletScreen(
                walletViewModel = walletViewModel,
                onBackClick = { finish() },
                onDetailsClick = {
                    val intent = android.content.Intent(this, com.example.myapplication.WalletDetailsActivity::class.java)
                    startActivity(intent)
                },
                onRechargeClick = {
                    // 暂时注释掉充值功能
                    // val intent = android.content.Intent(this, com.example.myapplication.RechargeActivity::class.java)
                    // startActivity(intent)
                },
                onEarnCoinsClick = {
                    val intent = android.content.Intent(this, com.example.myapplication.HowToEarnCoinsActivity::class.java)
                    startActivity(intent)
                },
                onCustomerServiceClick = {
                    val intent = android.content.Intent(this, com.example.myapplication.MyCustomerServiceActivity::class.java)
                    startActivity(intent)
                }
            )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWalletScreen(
    walletViewModel: WalletViewModel,
    onBackClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onRechargeClick: () -> Unit,
    onEarnCoinsClick: () -> Unit,
    onCustomerServiceClick: () -> Unit
) {
    // 从ViewModel获取真实数据
    val walletData by walletViewModel.walletData.observeAsState()
    val isLoading by walletViewModel.isLoading.observeAsState(false)
    val errorMessage by walletViewModel.errorMessage.observeAsState()
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
                        text = "我的钱包",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onDetailsClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "明细",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF424242)
                )
            )
            
            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 错误消息显示
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "加载失败: $error",
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // 余额卡片
                WalletBalanceCard(
                    walletData = walletData,
                    isLoading = isLoading,
                    onIncomeClick = { /* 处理收益余额点击 */ }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 充值等级
                RechargeLevelCard(
                    currentLevel = "普通",
                    progress = 0,
                    maxProgress = 1000,
                    onLevelClick = { /* 处理等级点击 */ }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 充值套餐
                RechargePackagesSection(
                    onRechargeClick = onRechargeClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 底部链接
                BottomLinksSection(
                    onEarnCoinsClick = onEarnCoinsClick,
                    onCustomerServiceClick = onCustomerServiceClick
                )
            }
        }
    }
}

@Composable
fun WalletBalanceCard(
    walletData: WalletDTO?,
    isLoading: Boolean,
    onIncomeClick: () -> Unit
) {
    val totalBalance = try { walletData?.balance?.toInt() ?: 0 } catch (e: Exception) { 0 }
    val rechargeBalance = try { walletData?.balance?.toInt() ?: 0 } catch (e: Exception) { 0 }  // 暂时使用总余额
    val giftBalance = 0  // 赠送余额，暂时为0
    val exchangeBalance = 0  // 兑换余额，暂时为0
    val incomeBalance = 0  // 收益余额，暂时为0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 总余额
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的余额",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = totalBalance.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "🪙",
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 余额分类
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceItem(
                    label = "充值余额",
                    value = rechargeBalance
                )
                BalanceItem(
                    label = "赠送余额",
                    value = giftBalance
                )
                BalanceItem(
                    label = "兑换余额",
                    value = exchangeBalance
                )
                BalanceItem(
                    label = "收益余额",
                    value = incomeBalance,
                    showArrow = true,
                    onClick = onIncomeClick
                )
            }
        }
    }
}

@Composable
fun BalanceItem(
    label: String,
    value: Int,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        if (showArrow) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "进入",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun RechargeLevelCard(
    currentLevel: String,
    progress: Int,
    maxProgress: Int,
    onLevelClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLevelClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "当前充值等级: $currentLevel",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            // 进度条
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((progress.toFloat() / maxProgress * 100).dp)
                        .background(Color(0xFF1976D2), RoundedCornerShape(4.dp))
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "$progress/$maxProgress",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "进入",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun RechargePackagesSection(
    onRechargeClick: () -> Unit
) {
    Column {
        Text(
            text = "充值套餐",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 大套餐
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RechargePackageCard(
                coins = 1200,
                price = 12,
                bonus = 100,
                isSelected = true,
                onClick = onRechargeClick,
                modifier = Modifier.weight(1f)
            )
            RechargePackageCard(
                coins = 3800,
                price = 38,
                bonus = 0,
                isSelected = false,
                onClick = onRechargeClick,
                modifier = Modifier.weight(1f)
            )
            RechargePackageCard(
                coins = 5800,
                price = 58,
                bonus = 0,
                isSelected = false,
                onClick = onRechargeClick,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 小套餐网格
        val smallPackages = listOf(
            Pair(800, 8),
            Pair(2800, 28),
            Pair(9800, 98),
            Pair(15800, 0),
            Pair(19800, 0),
            Pair(23800, 0)
        )
        
        for (i in 0 until smallPackages.size step 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (j in 0 until 3) {
                    val index = i + j
                    if (index < smallPackages.size) {
                        val (coins, price) = smallPackages[index]
                        SmallRechargePackageCard(
                            coins = coins,
                            price = price,
                            onClick = onRechargeClick,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (i + 3 < smallPackages.size) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RechargePackageCard(
    coins: Int,
    price: Int,
    bonus: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (bonus > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            Color(0xFFFF5722),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+${bonus}聊币",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🪙",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = coins.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "¥$price",
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SmallRechargePackageCard(
    coins: Int,
    price: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🪙",
                fontSize = 16.sp
            )
            Text(
                text = coins.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (price > 0) {
                Text(
                    text = "¥$price",
                    fontSize = 10.sp,
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

@Composable
fun BottomLinksSection(
    onEarnCoinsClick: () -> Unit,
    onCustomerServiceClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "如何赚聊币?",
            fontSize = 14.sp,
            color = Color(0xFF1976D2),
            modifier = Modifier.clickable { onEarnCoinsClick() }
        )
        Text(
            text = "充值遇到问题?",
            fontSize = 14.sp,
            color = Color(0xFF1976D2),
            modifier = Modifier.clickable { onCustomerServiceClick() }
        )
    }
}
