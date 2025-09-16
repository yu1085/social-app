package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ReportRecordsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReportRecordsScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportRecordsScreen(
    onBackClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("全部") }
    var showDropdown by remember { mutableStateOf(false) }
    
    val filterOptions = listOf("全部", "涉政暴恐", "色情", "广告", "虚假信息", "谩骂", "欺诈", "诱导消费", "其他")
    val reportReasons = listOf("涉政暴恐", "色情", "广告", "虚假信息", "谩骂", "欺诈", "诱导消费", "其他")

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
                        text = "举报记录",
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
            
            // 筛选区域
            FilterSection(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                showDropdown = showDropdown,
                onDropdownToggle = { showDropdown = !showDropdown },
                filterOptions = filterOptions,
                reportReasons = reportReasons
            )
            
            // 内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
            ) {
                // 空状态
                ReportEmptyStateContent()
            }
        }
        
        // 下拉框
        if (showDropdown) {
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier
                    .background(Color.White)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                filterOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                color = if (option == selectedFilter) Color(0xFF2196F3) else Color.Black
                            )
                        },
                        onClick = {
                            selectedFilter = option
                            showDropdown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    showDropdown: Boolean,
    onDropdownToggle: () -> Unit,
    filterOptions: List<String>,
    reportReasons: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // 筛选标签行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 第一行标签
            reportReasons.take(4).forEach { reason ->
                FilterTag(
                    text = reason,
                    isSelected = reason == selectedFilter,
                    onClick = { onFilterSelected(reason) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 第二行标签
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reportReasons.drop(4).forEach { reason ->
                FilterTag(
                    text = reason,
                    isSelected = reason == selectedFilter,
                    onClick = { onFilterSelected(reason) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 全部下拉框
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clickable { onDropdownToggle() }
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = selectedFilter,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "下拉",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun ReportEmptyStateContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 空状态插图
        ReportEmptyStateIllustration()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 空状态文字
        Text(
            text = "暂无举报记录",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

@Composable
fun ReportEmptyStateIllustration() {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // 剪贴板
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
        ) {
            // 剪贴板内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color(0xFFE0E0E0))
                    )
                }
            }
        }
        
        // 放大镜
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = 20.dp, y = 20.dp)
                .background(
                    color = Color(0xFF2196F3),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // 放大镜内容
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        
        // 云朵装饰
        CloudDecoration()
    }
}

@Composable
fun CloudDecoration() {
    // 左上云朵
    Box(
        modifier = Modifier
            .size(16.dp)
            .offset(x = (-30).dp, y = (-20).dp)
            .background(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(8.dp)
            )
    )
    
    // 右下云朵
    Box(
        modifier = Modifier
            .size(12.dp)
            .offset(x = 25.dp, y = 25.dp)
            .background(
                color = Color(0xFFE3F2FD),
                shape = RoundedCornerShape(6.dp)
            )
    )
    
    // 星星装饰
    repeat(3) { index ->
        Box(
            modifier = Modifier
                .size(4.dp)
                .offset(
                    x = (10 + index * 15).dp,
                    y = (-15 + index * 10).dp
                )
                .background(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}
