package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.RechargeViewModel
import com.example.myapplication.model.RechargePackage
import com.example.myapplication.model.PaymentMethod
import java.math.BigDecimal

/**
 * 充值页面 - 参考截图样式实现
 */
class RechargeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val viewModel: RechargeViewModel = viewModel(
                    factory = viewModelFactory {
                        addInitializer(RechargeViewModel::class) {
                            RechargeViewModel(this@RechargeActivity)
                        }
                    }
                )
                
                RechargeScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onRechargeSuccess = {
                        Toast.makeText(this, "充值成功！", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    viewModel: RechargeViewModel,
    onBackClick: () -> Unit,
    onRechargeSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    var selectedPackage by remember { mutableStateOf<RechargePackage?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
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
                        onClick = { 
                            // 跳转到明细页面
                            val intent = android.content.Intent(context, WalletDetailsActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Text(
                            text = "明细",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C2C2C)
                )
            )
            
            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 余额卡片
                WalletBalanceCard(
                    balance = uiState.currentBalance,
                    rechargeBalance = uiState.rechargeBalance,
                    giftBalance = uiState.giftBalance,
                    exchangeBalance = uiState.exchangeBalance,
                    earnBalance = uiState.earnBalance
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 充值等级
                RechargeLevel(
                    currentLevel = uiState.rechargeLevel,
                    progress = uiState.levelProgress,
                    maxProgress = uiState.maxLevelProgress
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 充值套餐标题
                Text(
                    text = "充值套餐",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 充值套餐网格
                RechargePackagesGrid(
                    packages = uiState.rechargePackages,
                    onPackageClick = { rechargePackage ->
                        selectedPackage = rechargePackage
                        showPaymentDialog = true
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 底部链接
                BottomLinks()
            }
        }
        
        // 支付方式弹窗
        if (showPaymentDialog && selectedPackage != null) {
            PaymentMethodDialog(
                rechargePackage = selectedPackage!!,
                onDismiss = { showPaymentDialog = false },
                onPaymentConfirm = { paymentMethod ->
                    viewModel.processPayment(selectedPackage!!, paymentMethod)
                    showPaymentDialog = false
                }
            )
        }
        
        // 加载状态
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun WalletBalanceCard(
    balance: Long,
    rechargeBalance: Long,
    giftBalance: Long,
    exchangeBalance: Long,
    earnBalance: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4A90E2),
                            Color(0xFF7B68EE)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "我的余额",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$balance",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 金币图标
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFFFFD700), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "币",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 余额详情
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BalanceDetailItem("充值余额", rechargeBalance)
                    BalanceDetailItem("赠送余额", giftBalance)
                    BalanceDetailItem("兑换余额", exchangeBalance)
                    BalanceDetailItem("收益余额", earnBalance, showArrow = true)
                }
            }
        }
    }
}

@Composable
fun BalanceDetailItem(
    title: String,
    amount: Long,
    showArrow: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$amount",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            if (showArrow) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun RechargeLevel(
    currentLevel: String,
    progress: Int,
    maxProgress: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "当前充值等级：$currentLevel",
            fontSize = 14.sp,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "$progress/$maxProgress",
            fontSize = 12.sp,
            color = Color(0xFF999999)
        )
    }
    
    // 进度条
    LinearProgressIndicator(
        progress = progress.toFloat() / maxProgress.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
        color = Color(0xFF4A90E2),
        trackColor = Color(0xFFE0E0E0)
    )
}

@Composable
fun RechargePackagesGrid(
    packages: List<RechargePackage>,
    onPackageClick: (RechargePackage) -> Unit
) {
    // 前三个大包
    val mainPackages = packages.take(3)
    // 后面的小包
    val smallPackages = packages.drop(3)
    
    Column {
        // 前三个大包 - 竖直排列
        mainPackages.forEach { rechargePackage ->
            MainRechargePackageCard(
                rechargePackage = rechargePackage,
                onClick = { onPackageClick(rechargePackage) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 小包 - 网格排列
        if (smallPackages.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp) // 固定高度避免嵌套滚动
            ) {
                items(smallPackages) { rechargePackage ->
                    SmallRechargePackageCard(
                        rechargePackage = rechargePackage,
                        onClick = { onPackageClick(rechargePackage) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainRechargePackageCard(
    rechargePackage: RechargePackage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rechargePackage.isRecommended) Color(0xFFF0F8FF) else Color.White
        ),
        border = if (rechargePackage.isRecommended) 
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4A90E2)) 
        else 
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 金币图标和数量
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFFFD700), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "币",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "${rechargePackage.coins}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            
            // 价格
            Text(
                text = "¥ ${rechargePackage.price.toInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A90E2)
            )
        }
    }
}

@Composable
fun SmallRechargePackageCard(
    rechargePackage: RechargePackage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 金币图标
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color(0xFFFFD700), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "币",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 金币数量
            Text(
                text = "${rechargePackage.coins}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // 价格
            Text(
                text = "¥${rechargePackage.price.toInt()}",
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
fun BottomLinks() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = { /* 跳转到如何赚取金币页面 */ }
        ) {
            Text(
                text = "如何赚取币？",
                fontSize = 14.sp,
                color = Color(0xFF999999)
            )
        }
        
        TextButton(
            onClick = { /* 跳转到充值遇到问题页面 */ }
        ) {
            Text(
                text = "充值遇到问题？",
                fontSize = 14.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
fun PaymentMethodDialog(
    rechargePackage: RechargePackage,
    onDismiss: () -> Unit,
    onPaymentConfirm: (PaymentMethod) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.ALIPAY) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // 标题和关闭按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "支付方式",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = Color(0xFF999999)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 商品信息
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFFFFD700), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "币",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "${rechargePackage.coins}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "¥${rechargePackage.price.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 支付方式选择
                PaymentMethod.values().forEach { method ->
                    PaymentMethodItem(
                        method = method,
                        isSelected = selectedMethod == method,
                        onClick = { selectedMethod = method }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 确认支付按钮
                Button(
                    onClick = { onPaymentConfirm(selectedMethod) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    )
                ) {
                    Text(
                        text = "确认支付（${rechargePackage.price.toInt()}元）",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
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
        // 支付方式图标
        Icon(
            painter = painterResource(method.iconRes),
            contentDescription = method.displayName,
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 支付方式名称
        Text(
            text = method.displayName,
            fontSize = 16.sp,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
        
        // 推荐标签（支付宝）
        if (method == PaymentMethod.ALIPAY) {
            Text(
                text = "推荐",
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier
                    .background(
                        Color(0xFFFF6B6B),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        // 选择状态
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF4A90E2)
            )
        )
    }
}