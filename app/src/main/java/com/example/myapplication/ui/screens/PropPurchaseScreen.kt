package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.LuckyNumber
import com.example.myapplication.viewmodel.PropPurchaseViewModel
import com.example.myapplication.viewmodel.PurchaseResult

/**
 * 道具购买页面
 */
@Composable
fun PropPurchaseScreen(
    luckyNumber: LuckyNumber,
    token: String?,
    onBackClick: () -> Unit,
    onPurchaseSuccess: (String) -> Unit,
    onPurchaseError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PropPurchaseViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    // 初始化数据
    LaunchedEffect(luckyNumber) {
        viewModel.initializeData(luckyNumber, token)
    }
    
    // 处理购买结果
    LaunchedEffect(uiState.purchaseResult) {
        uiState.purchaseResult?.let { result ->
            when (result) {
                is PurchaseResult.Success -> {
                    onPurchaseSuccess(result.message)
                }
                is PurchaseResult.Error -> {
                    onPurchaseError(result.message)
                }
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "购买靓号",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp)) // 占位符，保持标题居中
        }
        
        // 商品信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "商品信息",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 靓号信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            text = luckyNumber.icon,
                            fontSize = 24.sp,
                            color = Color(0xFF1A1A2E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "靓号: ${luckyNumber.number}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "等级: ${luckyNumber.tier.displayName}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 价格信息
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "原价",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = luckyNumber.price.toString(),
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "金币",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFD700)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 折扣信息
                    if (uiState.discountAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "折扣优惠",
                                fontSize = 14.sp,
                                color = Color(0xFF1677FF)
                            )
                            Text(
                                text = "-${uiState.discountAmount} 金币",
                                fontSize = 14.sp,
                                color = Color(0xFF1677FF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    
                    Divider()
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 最终价格
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "实付",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.finalPrice.toString(),
                                fontSize = 20.sp,
                                color = Color(0xFFFF6B6B),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "金币",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFFFFD700)
                            )
                        }
                    }
                }
            }
        }
        
        // 购买按钮
        Button(
            onClick = { viewModel.showPaymentDialog() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1677FF)),
            shape = RoundedCornerShape(8.dp),
            enabled = !uiState.isPurchasing
        ) {
            if (uiState.isPurchasing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "立即购买",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
    
    // 支付确认对话框
    if (uiState.showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hidePaymentDialog() },
            title = { Text("确认购买") },
            text = {
                Text("您确定要花费 ${uiState.finalPrice} 金币购买靓号 ${luckyNumber.number} 吗？")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.hidePaymentDialog()
                        viewModel.confirmPurchase()
                    }
                ) {
                    Text("确认购买")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hidePaymentDialog() }) {
                    Text("取消")
                }
            }
        )
    }
}