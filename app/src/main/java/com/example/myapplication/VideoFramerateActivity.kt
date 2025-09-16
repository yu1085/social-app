package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class VideoFramerateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoFramerateScreen(
                onBackClick = { finish() },
                onConfirmClick = { selectedFramerate ->
                    // 处理确认选择
                    android.widget.Toast.makeText(this, "已选择帧率: $selectedFramerate", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFramerateScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (String) -> Unit
) {
    var selectedFramerate by remember { mutableStateOf("24帧率") }

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
                        text = "视频帧率",
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
                    TextButton(
                        onClick = { onConfirmClick(selectedFramerate) }
                    ) {
                        Text(
                            text = "确定",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF007AFF)
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
                // 说明文字
                Text(
                    text = "请设置视频帧率",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // 帧率选项
                FramerateOptions(
                    selectedFramerate = selectedFramerate,
                    onFramerateSelected = { selectedFramerate = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 提示文字
                Text(
                    text = "* 视频过程中如遇卡顿可调整帧率,建议开启24帧即可获得流畅体验",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun FramerateOptions(
    selectedFramerate: String,
    onFramerateSelected: (String) -> Unit
) {
    val framerates = listOf("15帧率", "24帧率", "30帧率")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        framerates.forEach { framerate ->
            Box(
                modifier = Modifier.weight(1f)
            ) {
                FramerateOption(
                    framerate = framerate,
                    isSelected = framerate == selectedFramerate,
                    onClick = { onFramerateSelected(framerate) }
                )
            }
        }
    }
}

@Composable
fun FramerateOption(
    framerate: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                Color.White,
                RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color(0xFF007AFF) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = framerate,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color(0xFF007AFF) else Color.Black
        )
    }
}
