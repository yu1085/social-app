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
import androidx.compose.material.icons.filled.MoreVert
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

class WhoViewedMeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhoViewedMeScreen(
                onBackClick = { finish() },
                onVipButtonClick = {
                    // VIP按钮点击事件将在WhoViewedMeScreen中处理
                },
                onMoreOptionsClick = {
                    val intent = android.content.Intent(this, com.example.myapplication.VipSubscriptionActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhoViewedMeScreen(
    onBackClick: () -> Unit,
    onVipButtonClick: () -> Unit,
    onMoreOptionsClick: () -> Unit
) {
    var showVipDialog by remember { mutableStateOf(false) }
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
                        text = "谁看过我",
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
                actions = {
                    IconButton(onClick = onMoreOptionsClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多选项",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
            
            // 内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // 访客记录列表
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // 访客记录项
                    VisitorRecordItem(
                        avatar = "👩", // 这里应该使用真实的头像
                        name = "***对你很感兴趣",
                        visitInfo = "访问1次 · 27分钟前来看过",
                        isOnline = true,
                        onMoreClick = {}
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 可以添加更多访客记录
                    // VisitorRecordItem(...)
                }
                
                // 底部VIP开通按钮
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { showVipDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "立即开通VIP",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
        
        // VIP弹窗
        if (showVipDialog) {
            VipSubscriptionDialog(
                onDismiss = { showVipDialog = false },
                onSubscribe = { /* 处理订阅 */ }
            )
        }
    }
}

@Composable
fun VisitorRecordItem(
    avatar: String,
    name: String,
    visitInfo: String,
    isOnline: Boolean,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Box(
            modifier = Modifier.size(60.dp)
        ) {
            // 模糊头像背景
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE0E0E0),
                                Color(0xFFBDBDBD)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatar,
                    fontSize = 24.sp
                )
            }
            
            // 在线状态指示器
            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .align(Alignment.BottomEnd)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 访客信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = visitInfo,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        // 更多选项按钮
        IconButton(
            onClick = onMoreClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF1976D2))
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多选项",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun VipSubscriptionDialog(
    onDismiss: () -> Unit,
    onSubscribe: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: VIP, 1: SVIP
    var selectedPackage by remember { mutableStateOf(2) } // 0: 1个月, 1: 3个月, 2: 12个月
    var selectedPayment by remember { mutableStateOf(0) } // 0: 支付宝, 1: 微信
    
    // 半透明背景
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // 顶部关闭按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color(0xFF1976D2),
                                CircleShape
                            )
                    ) {
                        Text(
                            text = "×",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // VIP/SVIP标签页
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // VIP标签
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedTab == 0) Color(0xFFF5F5F5) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "VIP",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "享20大专属特权",
                                fontSize = 12.sp,
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
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 12.dp),
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
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "👑",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            Text(
                                text = "享20大专属特权",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 访客头像区域
                VisitorAvatarsSection()
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 查看访客记录
                Text(
                    text = "查看访客记录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "看看谁来看过你的资料和动态",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 套餐选择
                SubscriptionPackagesSection(
                    selectedPackage = selectedPackage,
                    onPackageSelected = { selectedPackage = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 节省金额
                Text(
                    text = "已额外省¥92",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 支付方式
                PaymentMethodsSection(
                    selectedPayment = selectedPayment,
                    onPaymentSelected = { selectedPayment = it }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 立即开通按钮
                Button(
                    onClick = onSubscribe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "立即开通VIP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 查看详细规则
                Text(
                    text = "查看详细规则和权益",
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* 处理查看规则 */ },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun VisitorAvatarsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // 中央头像
        Box(
            modifier = Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Color(0xFFE0E0E0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👨",
                    fontSize = 24.sp
                )
            }
            
            // 访问次数标签
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        Color(0xFFFFC107),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "最近被访问20次",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SubscriptionPackagesSection(
    selectedPackage: Int,
    onPackageSelected: (Int) -> Unit
) {
    val packages = listOf(
        Triple("1个月", "¥25", "原价¥25"),
        Triple("3个月", "¥68", "原价¥75"),
        Triple("12个月", "¥208", "原价¥300")
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        packages.forEachIndexed { index, (duration, price, originalPrice) ->
            SubscriptionPackageCard(
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

@Composable
fun SubscriptionPackageCard(
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
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 4.dp else 1.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isBest) {
                    Box(
                        modifier = Modifier
                            .background(
                                Color.Red,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "最佳",
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                Text(
                    text = duration,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                
                Text(
                    text = originalPrice,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PaymentMethodsSection(
    selectedPayment: Int,
    onPaymentSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = "官方直减",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 支付宝
            PaymentMethodItem(
                name = "支付宝",
                icon = "🔵",
                isSelected = selectedPayment == 0,
                onClick = { onPaymentSelected(0) },
                modifier = Modifier.weight(1f)
            )
            
            // 微信
            PaymentMethodItem(
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
fun PaymentMethodItem(
    name: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = name,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            Color(0xFF1976D2),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
