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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class VipSubscriptionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VipSubscriptionScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipSubscriptionScreen(
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: VIP, 1: SVIP
    var selectedPackage by remember { mutableStateOf(2) } // 0: 1个月, 1: 3个月, 2: 12个月
    var selectedPayment by remember { mutableStateOf(0) } // 0: 支付宝, 1: 微信
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8E1),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部导航栏
            TopAppBar(
                title = {
                    Text(
                        text = "VIP会员",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // VIP/SVIP标签页
                VipTabsSection(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 访客头像区域
                VipVisitorAvatarsSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 查看访客记录
                VipVisitorRecordsSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 套餐选择
                VipSubscriptionPackagesSection(
                    selectedPackage = selectedPackage,
                    onPackageSelected = { selectedPackage = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 节省金额
                VipSavingsSection()
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 支付方式
                VipPaymentMethodsSection(
                    selectedPayment = selectedPayment,
                    onPaymentSelected = { selectedPayment = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 立即开通按钮
                SubscribeButton()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 查看详细规则
                RulesLink()
            }
        }
    }
}

@Composable
fun VipTabsSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // VIP标签
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (selectedTab == 0) Color(0xFFF5F5F5) else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .clickable { onTabSelected(0) }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VIP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "享20大专属特权",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        // SVIP标签
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    if (selectedTab == 1) Color(0xFFF5F5F5) else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .clickable { onTabSelected(1) }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SVIP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "👑",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
                Text(
                    text = "享20大专属特权",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun VipVisitorAvatarsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // 中央头像
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        Color(0xFFE0E0E0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👨",
                    fontSize = 32.sp
                )
            }
            
            // 访问次数标签
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        Color(0xFFFFC107),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "最近被访问20次",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VipVisitorRecordsSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "查看访客记录",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "看看谁来看过你的资料和动态",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VipSubscriptionPackagesSection(
    selectedPackage: Int,
    onPackageSelected: (Int) -> Unit
) {
    val packages = listOf(
        Triple("1个月", "¥25", "原价¥25"),
        Triple("3个月", "¥68", "原价¥75"),
        Triple("12个月", "¥208", "原价¥300")
    )
    
    Column {
        Text(
            text = "选择套餐",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            packages.forEachIndexed { index, (duration, price, originalPrice) ->
                VipSubscriptionPackageCard(
                    duration = duration,
                    price = price,
                    originalPrice = originalPrice,
                    isSelected = selectedPackage == index,
                    isBest = index == 2,
                    onClick = { onPackageSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun VipSubscriptionPackageCard(
    duration: String,
    price: String,
    originalPrice: String,
    isSelected: Boolean,
    isBest: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 6.dp else 2.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isBest) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color.Red,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "最佳",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text(
                    text = duration,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = price,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                
                Text(
                    text = originalPrice,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun VipSavingsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "已额外省¥92",
            fontSize = 16.sp,
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun VipPaymentMethodsSection(
    selectedPayment: Int,
    onPaymentSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = "支付方式",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Text(
            text = "官方直减",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 支付宝
            VipPaymentMethodItem(
                name = "支付宝",
                icon = "🔵",
                isSelected = selectedPayment == 0,
                onClick = { onPaymentSelected(0) },
                modifier = Modifier.weight(1f)
            )
            
            // 微信
            VipPaymentMethodItem(
                name = "微信",
                icon = "💚",
                isSelected = selectedPayment == 1,
                onClick = { onPaymentSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun VipPaymentMethodItem(
    name: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = name,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color(0xFF1976D2),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SubscribeButton() {
    Button(
        onClick = { /* 处理订阅 */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1976D2)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = "立即开通VIP",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun RulesLink() {
    Text(
        text = "查看详细规则和权益",
        fontSize = 16.sp,
        color = Color(0xFF1976D2),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 处理查看规则 */ },
        textAlign = TextAlign.Center
    )
}
