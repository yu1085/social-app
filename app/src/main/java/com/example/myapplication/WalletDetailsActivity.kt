package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.TransactionRecord
import com.example.myapplication.model.TransactionStatus
import com.example.myapplication.model.TransactionType
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.WalletDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 钱包明细页面
 */
class WalletDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val viewModel: WalletDetailsViewModel = viewModel()
                
                WalletDetailsScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailsScreen(
    viewModel: WalletDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: 全部, 1: 充值, 2: 消费
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = "钱包明细",
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2C2C2C)
            )
        )
        
        // 余额概览卡片
        WalletSummaryCard(
            totalBalance = uiState.totalBalance,
            totalRecharge = uiState.totalRecharge,
            totalConsume = uiState.totalConsume
        )
        
        // 标签页
        TransactionTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // 交易记录列表
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val filteredTransactions = when (selectedTab) {
                0 -> uiState.transactions
                1 -> uiState.transactions.filter { it.type == TransactionType.RECHARGE }
                2 -> uiState.transactions.filter { it.type == TransactionType.CONSUME }
                else -> uiState.transactions
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTransactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
                
                if (filteredTransactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_empty_state),
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFF999999)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "暂无交易记录",
                                    fontSize = 16.sp,
                                    color = Color(0xFF999999)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletSummaryCard(
    totalBalance: Long,
    totalRecharge: Long,
    totalConsume: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "余额概览",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "当前余额",
                    amount = totalBalance,
                    color = Color(0xFF4A90E2)
                )
                
                SummaryItem(
                    title = "累计充值",
                    amount = totalRecharge,
                    color = Color(0xFF52C41A)
                )
                
                SummaryItem(
                    title = "累计消费",
                    amount = totalConsume,
                    color = Color(0xFFFF7875)
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    amount: Long,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color(0xFF999999)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "$amount",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun TransactionTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("全部", "充值", "消费")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF4A90E2) else Color.White
                ),
                onClick = { onTabSelected(index) }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionRecord) {
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val timeText = dateFormat.format(Date(transaction.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 交易类型图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = getTransactionTypeColor(transaction.type).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(getTransactionTypeIcon(transaction.type)),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = getTransactionTypeColor(transaction.type)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 交易信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeText,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 状态标签
                    if (transaction.status != TransactionStatus.SUCCESS) {
                        Text(
                            text = getStatusText(transaction.status),
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    color = getStatusColor(transaction.status),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // 金额
            Text(
                text = formatAmount(transaction.type, transaction.amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.RECHARGE) 
                    Color(0xFF52C41A) else Color(0xFFFF7875)
            )
        }
    }
}

@Composable
fun getTransactionTypeIcon(type: TransactionType): Int {
    return when (type) {
        TransactionType.RECHARGE -> R.drawable.ic_add_circle
        TransactionType.CONSUME -> R.drawable.ic_remove_circle
        TransactionType.GIFT -> R.drawable.ic_gift
        TransactionType.EARN -> R.drawable.ic_star
        TransactionType.EXCHANGE -> R.drawable.ic_swap
    }
}

@Composable
fun getTransactionTypeColor(type: TransactionType): Color {
    return when (type) {
        TransactionType.RECHARGE -> Color(0xFF52C41A)
        TransactionType.CONSUME -> Color(0xFFFF7875)
        TransactionType.GIFT -> Color(0xFFFF9C6E)
        TransactionType.EARN -> Color(0xFFFFD666)
        TransactionType.EXCHANGE -> Color(0xFF9254DE)
    }
}

fun getStatusText(status: TransactionStatus): String {
    return when (status) {
        TransactionStatus.SUCCESS -> "成功"
        TransactionStatus.PENDING -> "处理中"
        TransactionStatus.FAILED -> "失败"
    }
}

fun getStatusColor(status: TransactionStatus): Color {
    return when (status) {
        TransactionStatus.SUCCESS -> Color(0xFF52C41A)
        TransactionStatus.PENDING -> Color(0xFFFA8C16)
        TransactionStatus.FAILED -> Color(0xFFFF4D4F)
    }
}

fun formatAmount(type: TransactionType, amount: Long): String {
    val prefix = when (type) {
        TransactionType.RECHARGE, TransactionType.GIFT, TransactionType.EARN -> "+"
        TransactionType.CONSUME -> "-"
        TransactionType.EXCHANGE -> ""
    }
    return "$prefix$amount"
}