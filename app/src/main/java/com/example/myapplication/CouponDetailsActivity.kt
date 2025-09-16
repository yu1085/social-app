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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

class CouponDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CouponDetailsScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponDetailsScreen(
    onBackClick: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(2025) }
    var selectedMonth by remember { mutableStateOf(9) }
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
                    containerColor = Color(0xFFF5F5F5)
                )
            )
            
            // 日期选择区域
            DateSelectionCard(
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                onDateClick = { showDatePicker = true }
            )
            
            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 空状态
                EmptyStateIllustration()
            }
        }
        
        // 日期选择器对话框
        if (showDatePicker) {
            CouponDatePickerDialog(
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                onYearSelected = { selectedYear = it },
                onMonthSelected = { selectedMonth = it },
                onDismiss = { showDatePicker = false },
                onConfirm = { 
                    showDatePicker = false
                    // 这里可以添加刷新数据的逻辑
                }
            )
        }
    }
}

@Composable
fun DateSelectionCard(
    selectedYear: Int,
    selectedMonth: Int,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onDateClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "日期选择",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "${selectedYear}年${String.format("%02d", selectedMonth)}月",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "展开",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EmptyStateIllustration() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 空状态图标
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Color(0xFFE3F2FD),
                    RoundedCornerShape(40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📄",
                fontSize = 32.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "暂无数据",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CouponDatePickerDialog(
    selectedYear: Int,
    selectedMonth: Int,
    onYearSelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val years = (2020..2030).toList()
    val months = (1..12).toList()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "选择日期",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 年份选择
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "年份",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(years.size) { index ->
                            val year = years[index]
                            Text(
                                text = "${year}年",
                                fontSize = 16.sp,
                                color = if (year == selectedYear) Color(0xFF1976D2) else Color.Black,
                                fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onYearSelected(year) }
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                            )
                        }
                    }
                }
                
                // 月份选择
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "月份",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(months.size) { index ->
                            val month = months[index]
                            Text(
                                text = "${month}月",
                                fontSize = 16.sp,
                                color = if (month == selectedMonth) Color(0xFF1976D2) else Color.Black,
                                fontWeight = if (month == selectedMonth) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMonthSelected(month) }
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF1976D2)
                )
            ) {
                Text(
                    text = "确定",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text(
                    text = "取消",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}
