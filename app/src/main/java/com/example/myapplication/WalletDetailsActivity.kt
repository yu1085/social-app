package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
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

class WalletDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletDetailsScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailsScreen(
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf("2025年09月") }
    var showDatePicker by remember { mutableStateOf(false) }
    
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
                        text = "明细",
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
            
            // 标签页
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                val tabs = listOf("支出", "充值", "收入", "提现", "兑换")
                
                tabs.forEachIndexed { index, tab ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clickable { selectedTab = index }
                    ) {
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.White else Color.Gray,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    if (selectedTab == index) Color(0xFF1976D2) else Color.Transparent,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            // 日期选择器
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { showDatePicker = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "日历",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = selectedDate,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "下拉",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // 内容区域
            when (selectedTab) {
                0 -> ExpenditureContent()
                1 -> RechargeContent()
                2 -> IncomeContent()
                3 -> WithdrawalContent()
                4 -> ExchangeContent()
            }
        }
        
        // 日期选择器对话框
        if (showDatePicker) {
            WalletDatePickerDialog(
                selectedDate = selectedDate,
                onDateSelected = { newDate ->
                    selectedDate = newDate
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@Composable
fun ExpenditureContent() {
    EmptyStateContent("支出")
}

@Composable
fun RechargeContent() {
    EmptyStateContent("充值")
}

@Composable
fun IncomeContent() {
    EmptyStateContent("收入")
}

@Composable
fun WithdrawalContent() {
    EmptyStateContent("提现")
}

@Composable
fun ExchangeContent() {
    EmptyStateContent("兑换")
}

@Composable
fun EmptyStateContent(type: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 空状态图标
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // 文档图标
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1976D2),
                                    Color(0xFF42A5F5)
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                
                // 货币符号
                Text(
                    text = "¥",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                // 装饰线
                Box(
                    modifier = Modifier
                        .size(60.dp, 4.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "暂无数据",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun WalletDatePickerDialog(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val years = listOf("2023年", "2024年", "2025年")
    val months = listOf(
        "01月", "02月", "03月", "04月", "05月", "06月",
        "07月", "08月", "09月", "10月", "11月", "12月"
    )
    
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
                .clickable { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "选择日期",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 年份选择
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "年份",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(years.size) { index ->
                                val year = years[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val currentMonth = selectedDate.substringAfter("年").substringBefore("月")
                                            onDateSelected("$year$currentMonth")
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = year,
                                        fontSize = 16.sp,
                                        color = if (selectedDate.contains(year)) Color(0xFF1976D2) else Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    if (selectedDate.contains(year)) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "选中",
                                            tint = Color(0xFF1976D2),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // 月份选择
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "月份",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(months.size) { index ->
                                val month = months[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val currentYear = selectedDate.substringBefore("年")
                                            onDateSelected("$currentYear$month")
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = month,
                                        fontSize = 16.sp,
                                        color = if (selectedDate.contains(month)) Color(0xFF1976D2) else Color.Black,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    if (selectedDate.contains(month)) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "选中",
                                            tint = Color(0xFF1976D2),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE0E0E0)
                        )
                    ) {
                        Text(
                            text = "取消",
                            color = Color.Black
                        )
                    }
                    
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        )
                    ) {
                        Text(
                            text = "确定",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
