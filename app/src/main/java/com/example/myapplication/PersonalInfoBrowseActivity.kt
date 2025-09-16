package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PersonalInfoBrowseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalInfoBrowseScreen(
                onBackClick = { finish() },
                onExportClick = {
                    val intent = android.content.Intent(this, PersonalInfoExportActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoBrowseScreen(
    onBackClick: () -> Unit,
    onExportClick: () -> Unit
) {
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
                        text = "个人信息浏览导出",
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
                // 个人信息展示区域
                PersonalInfoSection()
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // 导出按钮
                Button(
                    onClick = onExportClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF)
                    )
                ) {
                    Text(
                        text = "导出个人信息",
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
fun PersonalInfoSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 头像
        PersonalInfoItem(
            label = "头像",
            value = "👤"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 昵称
        PersonalInfoItem(
            label = "昵称",
            value = "不忘随风起745"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 手机号
        PersonalInfoItem(
            label = "手机号",
            value = "18151930836"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 性别
        PersonalInfoItem(
            label = "性别",
            value = "男"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 生日
        PersonalInfoItem(
            label = "生日",
            value = "1990-01-01"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 星座
        PersonalInfoItem(
            label = "星座",
            value = "魔羯座"
        )
    }
}

@Composable
fun PersonalInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 标签
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        // 值
        if (label == "头像") {
            // 头像显示
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFE3F2FD),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp
                )
            }
        } else {
            // 普通文本显示
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}
