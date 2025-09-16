package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReportScreen(
                onBackClick = { finish() },
                onMoreClick = {
                    // 跳转到举报记录页面
                    val intent = android.content.Intent(this, ReportRecordsActivity::class.java)
                    startActivity(intent)
                },
                onSubmitClick = { reason, description, screenshots ->
                    if (reason.isNotEmpty() && description.isNotEmpty()) {
                        // 处理提交举报
                        android.widget.Toast.makeText(
                            this,
                            "举报提交成功，我们会尽快处理！",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        android.widget.Toast.makeText(
                            this,
                            "请填写举报理由和详细描述",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onSubmitClick: (String, String, List<String>) -> Unit
) {
    var selectedReason by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var screenshots by remember { mutableStateOf<List<String>>(emptyList()) }
    val maxLength = 120
    val maxScreenshots = 9
    val remainingChars = maxLength - description.length

    val reportReasons = listOf(
        "涉政暴恐", "色情", "广告", "虚假信息",
        "谩骂", "欺诈", "诱导消费", "其他"
    )

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
                        text = "举报",
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
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "举报记录",
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
                    .padding(16.dp)
            ) {
                // 举报理由部分
                ReportReasonSection(
                    reasons = reportReasons,
                    selectedReason = selectedReason,
                    onReasonSelected = { selectedReason = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 详细描述部分
                DetailedDescriptionSection(
                    description = description,
                    onDescriptionChange = { description = it },
                    remainingChars = remainingChars,
                    maxLength = maxLength
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 问题截图部分
                ScreenshotSection(
                    screenshots = screenshots,
                    onScreenshotsChange = { screenshots = it },
                    maxScreenshots = maxScreenshots
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 提交按钮
                Button(
                    onClick = {
                        if (selectedReason.isNotEmpty() && description.isNotEmpty()) {
                            onSubmitClick(selectedReason, description, screenshots)
                        } else {
                            // 空内容提示在Activity中处理
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "提交",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ReportReasonSection(
    reasons: List<String>,
    selectedReason: String,
    onReasonSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "举报理由*",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 第一行理由
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reasons.take(4).forEach { reason ->
                ReasonTag(
                    text = reason,
                    isSelected = reason == selectedReason,
                    onClick = { onReasonSelected(reason) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 第二行理由
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reasons.drop(4).forEach { reason ->
                ReasonTag(
                    text = reason,
                    isSelected = reason == selectedReason,
                    onClick = { onReasonSelected(reason) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ReasonTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = if (isSelected) Color.Transparent else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun DetailedDescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit,
    remainingChars: Int,
    maxLength: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "详细描述*",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Text(
                text = "$remainingChars/$maxLength",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) {
                    onDescriptionChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = "请详细描述您的内容,包括用户ID、违规时间、相关凭证等资料,便于人工核实处理",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFF8F9FA),
                unfocusedContainerColor = Color(0xFFF8F9FA)
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            maxLines = 5
        )
    }
}

@Composable
fun ScreenshotSection(
    screenshots: List<String>,
    onScreenshotsChange: (List<String>) -> Unit,
    maxScreenshots: Int
) {
    Column {
        Text(
            text = "问题截图",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "若举报内容涉及第三方平台,请提供对方要求添加第三方平台的聊天截图",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 截图网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp)
        ) {
            // 显示已添加的截图
            items(screenshots.size) { index ->
                ScreenshotItem(
                    imagePath = screenshots[index],
                    onRemove = {
                        val newScreenshots = screenshots.toMutableList()
                        newScreenshots.removeAt(index)
                        onScreenshotsChange(newScreenshots)
                    }
                )
            }
            
            // 添加截图按钮
            if (screenshots.size < maxScreenshots) {
                item {
                    AddScreenshotButton(
                        onClick = {
                            // 这里可以添加选择图片的逻辑
                            val newScreenshots = screenshots.toMutableList()
                            newScreenshots.add("screenshot_${screenshots.size + 1}.jpg")
                            onScreenshotsChange(newScreenshots)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenshotItem(
    imagePath: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "截图",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun AddScreenshotButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加截图",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "0/9",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
