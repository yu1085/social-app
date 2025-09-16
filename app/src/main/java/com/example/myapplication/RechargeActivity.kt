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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class RechargeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RechargeScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    onBackClick: () -> Unit
) {
    var selectedPackage by remember { mutableStateOf(0) }
    
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
                        text = "充值",
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
                    containerColor = Color.White
                )
            )
            
            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 充值套餐
                RechargePackagesSection(
                    selectedPackage = selectedPackage,
                    onPackageSelected = { selectedPackage = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 支付方式
                PaymentMethodsSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 充值按钮
                Button(
                    onClick = { /* 处理充值 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "立即充值",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun RechargePackagesSection(
    selectedPackage: Int,
    onPackageSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = "选择充值套餐",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        val packages = listOf(
            RechargePackage(1200, 12, 100, true),
            RechargePackage(3800, 38, 0, false),
            RechargePackage(5800, 58, 0, false),
            RechargePackage(800, 8, 0, false),
            RechargePackage(2800, 28, 0, false),
            RechargePackage(9800, 98, 0, false),
            RechargePackage(15800, 158, 0, false),
            RechargePackage(19800, 198, 0, false),
            RechargePackage(23800, 238, 0, false)
        )
        
        // 大套餐
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            packages.take(3).forEachIndexed { index, packageItem ->
                RechargePackageCard(
                    packageItem = packageItem,
                    isSelected = selectedPackage == index,
                    onClick = { onPackageSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 小套餐网格
        for (i in 3 until packages.size step 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (j in 0 until 3) {
                    val index = i + j
                    if (index < packages.size) {
                        RechargePackageCard(
                            packageItem = packages[index],
                            isSelected = selectedPackage == index,
                            onClick = { onPackageSelected(index) },
                            modifier = Modifier.weight(1f),
                            isSmall = true
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            if (i + 3 < packages.size) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun RechargePackageCard(
    packageItem: RechargePackage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSmall: Boolean = false
) {
    Card(
        modifier = modifier
            .height(if (isSmall) 80.dp else 100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSmall) 8.dp else 12.dp)
        ) {
            if (packageItem.bonus > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(
                            Color(0xFFFF5722),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+${packageItem.bonus}聊币",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🪙",
                    fontSize = if (isSmall) 20.sp else 24.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = packageItem.coins.toString(),
                    fontSize = if (isSmall) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                if (packageItem.price > 0) {
                    Text(
                        text = "¥${packageItem.price}",
                        fontSize = if (isSmall) 12.sp else 14.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodsSection() {
    Column {
        Text(
            text = "支付方式",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        val paymentMethods = listOf(
            PaymentMethod("微信支付", "💚", true),
            PaymentMethod("支付宝", "🔵", false),
            PaymentMethod("银行卡", "💳", false)
        )
        
        paymentMethods.forEach { method ->
            PaymentMethodItem(
                method = method,
                onClick = { /* 处理支付方式选择 */ }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (method.isSelected) Color(0xFFE3F2FD) else Color.White
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
                text = method.icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = method.name,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            if (method.isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1976D2)),
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

data class RechargePackage(
    val coins: Int,
    val price: Int,
    val bonus: Int,
    val isRecommended: Boolean
)

data class PaymentMethod(
    val name: String,
    val icon: String,
    val isSelected: Boolean
)
